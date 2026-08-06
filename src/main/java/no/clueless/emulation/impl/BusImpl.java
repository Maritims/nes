package no.clueless.emulation.impl;

import no.clueless.emulation.*;
import no.clueless.emulation.impl.cartridge.CartridgeImpl;

import static no.clueless.emulation.impl.Masks.MASK_16BIT;
import static no.clueless.emulation.impl.Masks.MASK_8BIT;

public class BusImpl implements Bus {
    private final Cpu6502 cpu;
    private final Ppu2C02 ppu;
    private final Apu2A03 apu;
    private final int[]   controllers      = new int[2];
    private final int[]   controller_state = new int[2];

    private final int[]     cpuRam              = new int[2048];
    private       Cartridge cartridge;
    private       int       totalClockCount     = 0;
    private       int       dmaPage;
    private       int       dmaAddress;
    private       boolean   isDmaTransfer;
    private       int       dmaData;
    private       boolean   isDmaAlignmentCycle = true;

    public BusImpl(Cpu6502 cpu, Ppu2C02 ppu, Apu2A03 apu, Controller controller1, Controller controller2) {
        if (cpu == null) {
            throw new IllegalArgumentException("cpu cannot be null");
        }
        if (ppu == null) {
            throw new IllegalArgumentException("ppu cannot be null");
        }
        if (apu == null) {
            throw new IllegalArgumentException("apu cannot be null");
        }

        this.cpu = cpu;
        this.ppu = ppu;
        this.apu = apu;

        this.cpu.connectToBus(this);
    }

    @Override
    public Cpu6502 getCpu() {
        return cpu;
    }

    @Override
    public Ppu2C02 getPpu() {
        return ppu;
    }

    @Override
    public Apu2A03 getApu() {
        return apu;
    }

    @Override
    public CartridgeImpl getCartridge() {
        return null;
    }

    @Override
    public int getController1() {
        return controllers[0];
    }

    @Override
    public void setController1(int controller) {
        controllers[0] = controller;
    }

    @Override
    public int getController2() {
        return controllers[1];
    }

    @Override
    public void setController2(int controller) {
        controllers[1] = controller;
    }

    @Override
    public void insertCartridge(Cartridge cartridge) {
        this.cartridge = cartridge;
        this.ppu.connectToCartridge(cartridge);
    }

    private boolean isBusReadCycle = true;

    @Override
    public void clock() {
        ppu.clock();
        ppu.clock();
        ppu.clock();
        apu.clock();
        apu.clock();
        apu.clock();

        // OAM DMA halts the CPU, performs an optional alignment cycle, and then gets and puts 256 times, taking 513 or 514 cycles. It attempts to halt on the first CPU cycle after the $4014 write.
        if (isDmaTransfer) {
            // Check if the DMA is currently in a dummy cycle (used for DMC DMA alignment/initiation)
            if (isDmaAlignmentCycle) {
                // DMC DMA dummy cycles wait for a CPU read cycle to successfully halt/sync the CPU
                if (isBusReadCycle) {
                    isDmaAlignmentCycle = false;
                }
            } else {
                if (isBusReadCycle) {
                    // GET cycle: Read a byte from CPU memory (e.g., from the DMA page in RAM/ROM)
                    dmaData = read(dmaPage << 8 | dmaAddress);
                } else {
                    // PUT cycle: Write the previously fetched byte to the PPU's OAM (Object Attribute Memory)
                    ppu.writePrimaryOAM(dmaAddress & MASK_16BIT, dmaData & MASK_16BIT);

                    // Advance the low-byte of the OAM address; wraps around after 256 bytes
                    dmaAddress = (dmaAddress + 1) & MASK_8BIT;

                    // Once all 256 bytes are transferred, complete the DMA process and reset alignment cycle state.
                    if (dmaAddress == 0x00) {
                        isDmaTransfer       = false;
                        isDmaAlignmentCycle = true;
                    }
                }
            }
        } else {
            cpu.clock();
        }

        isBusReadCycle = !isBusReadCycle;

        if (ppu.isNmi()) {
            ppu.clearNmi();
            cpu.nmi();
        }

        if (cartridge.getMapper().isIrqState()) {
            cartridge.getMapper().clearIrq();
            cpu.irq();
        }
    }

    @Override
    public int read(int address) {
        var data = 0x00;

        if (address >= 0x0000 && address < 0x2000) {
            data = cpuRam[address % cpuRam.length];
        } else if (address >= 0x2000 && address < 0x4000) {
            data = ppu.readRegister(address);
        } else if (address == 0x4015) {
            data = apu.readRegister(address);
        } else if (address >= 0x4016 && address <= 0x4017) {
            //log.debug("Controller read");
            data = (controller_state[address & 0x0001] & 0x80) > 0 ? 1 : 0;
            controller_state[address & 0x0001] <<= 1;
        } else if (address >= 0x8000 && address <= 0xFFFF) {
            // Cartridge
            data = cartridge.readPrg(address).orElseThrow();
        }

        return data & 0xFF;
    }

    @Override
    public void write(int address, int data) {
        if (address >= 0x0000 && address < 0x2000) {
            cpuRam[address % cpuRam.length] = data & 0xFF;
        } else if (address >= 0x2000 && address < 0x4000) {
            ppu.writeRegister(address, data);
        } else if ((address >= 0x4000 && address <= 0x4013) || address == 0x4015 || address == 0x4017) {
            apu.writeRegister(address, data);
        } else if (address == 0x4014) {
            // Initialize DMA transfer.
            dmaPage       = data;
            dmaAddress    = 0x00;
            isDmaTransfer = true;
        } else if (address >= 0x4016 && address <= 0x4017) {
            controller_state[address & 0x0001] = controllers[address & 0x0001];
        }
    }

    @Override
    public void reset() {
        cpu.reset();
        ppu.reset();
        cartridge.reset();
        totalClockCount     = 0;
        dmaPage             = 0x00;
        dmaAddress          = 0x00;
        dmaData             = 0x00;
        isDmaAlignmentCycle = true;
        isDmaTransfer       = false;
    }
}
