package no.clueless.emulation.impl;

import no.clueless.emulation.*;
import no.clueless.emulation.impl.cartridge.CartridgeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static no.clueless.emulation.impl.CpuMemoryMap.*;

public class BusImpl implements Bus {
    private static final Logger     log              = LoggerFactory.getLogger(BusImpl.class);
    private final        Cpu6502    cpu;
    private final        Ppu2C02    ppu;
    private final        Apu2A03    apu;
    private final        Controller controller1;
    private final        Controller controller2;
    private final        int[]      controllers      = new int[2];
    private final        int[]      controller_state = new int[2];

    private final int[]     cpuRam          = new int[2048];
    private       Cartridge cartridge;
    private       int       totalClockCount = 0;

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

        this.cpu         = cpu;
        this.ppu         = ppu;
        this.apu         = apu;
        this.controller1 = controller1;
        this.controller2 = controller2;

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

    @Override
    public void clock() {
        ppu.clock();
        ppu.clock();
        ppu.clock();
        apu.clock();

        /*if (totalClockCount % 3 == 0) {
            cpu.clock();
        }*/

        cpu.clock();

        if (ppu.isNmi()) {
            ppu.clearNmi();
            cpu.nmi();
        }

        if (cartridge.getMapper().isIrqState()) {
            cartridge.getMapper().clearIrq();
            cpu.irq();
        }

        totalClockCount++;
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
        } else if (address >= PRG_ROM_START && address <= PRG_ROM_END) {
            // Cartridge
            data = cartridge.readPrg(address).orElseThrow();
        }

        return data & 0xFF;
    }

    private final int[] wram = new int[8192];

    @Override
    public void write(int address, int data) {
        if (address >= 0x0000 && address < 0x2000) {
            cpuRam[address % cpuRam.length] = data & 0xFF;
        } else if (address >= 0x2000 && address < 0x4000) {
            ppu.writeRegister(address, data);
        } else if ((address >= 0x4000 && address <= 0x4013) || address == 0x4015 || address == 0x4017) {
            apu.writeRegister(address, data);
        } else if (address == 0x4014) {
            // TODO: DMA
        } else if (address >= 0x4016 && address <= 0x4017) {
            controller_state[address & 0x0001] = controllers[address & 0x0001];
        }
    }

    @Override
    public void reset() {
        cpu.reset();
        ppu.reset();
        cartridge.reset();
    }
}
