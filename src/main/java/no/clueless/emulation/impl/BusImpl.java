package no.clueless.emulation.impl;

import no.clueless.emulation.*;
import no.clueless.emulation.impl.cartridge.CartridgeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static no.clueless.emulation.impl.CpuMemoryMap.*;

public class BusImpl implements Bus {
    private static final Logger  log = LoggerFactory.getLogger(BusImpl.class);
    private final        Cpu6502 cpu;
    private final        Ppu2C02 ppu;
    private final        Apu2A03 apu;

    private final int[]     cpuRam          = new int[2048];
    private       Cartridge cartridge;
    private       int       totalClockCount = 0;

    public BusImpl(Cpu6502 cpu, Ppu2C02 ppu, Apu2A03 apu) {
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
    public void insertCartridge(Cartridge cartridge) {
        this.cartridge = cartridge;
        this.ppu.connectToCartridge(cartridge);
    }

    @Override
    public void clock() {
        cpu.clock();

        for (var i = 0; i < 3; i++) {
            ppu.clock();
            apu.clock();

            if (ppu.isNmi()) {
                ppu.handleNmi();
                cpu.nmi();
            }
        }

        if (cartridge.getMapper().isIrqState()) {
            cartridge.getMapper().clearIrq();
            cpu.irq();
        }

        totalClockCount += 3;
    }

    @Override
    public int read(int address) {
        var data = 0x00;

        if (address >= RAM_START && address <= RAM_END) {
            data = cpuRam[address % cpuRam.length];
        } else if (address >= PPU_REGISTER_START && address <= PPU_REGISTER_END) {
            data = ppu.readRegister(address);
        } else if (address >= APU_IO_START && address <= APU_IO_END) {
            // APU and I/O
            data = apu.readRegister(address);
        } else if (address >= APU_TEST_START && address <= APU_TEST_END) {
            // APU and I/O test
            data = apu.readRegister(address);
        } else if (address >= PRG_ROM_START && address <= PRG_ROM_END) {
            // Cartridge
            data = cartridge.readPrg(address).orElseThrow();
        } else if (address >= WRAM_START && address <= WRAM_END) {
            data = wram[address - WRAM_START];
        } else {
            //log.warn("Read from unknown address: {}", "$%04X".formatted(address));
        }

        return data & 0xFF;
    }

    private final int[] wram = new int[8192];

    @Override
    public void write(int address, int data) {
        if (address >= RAM_START && address <= RAM_END) {
            cpuRam[address % cpuRam.length] = data & 0xFF;
        } else if (address >= PPU_REGISTER_START && address <= PPU_REGISTER_END) {
            ppu.writeRegister(address, data);
        } else if (address >= APU_IO_START && address <= APU_IO_END) {
            apu.writeRegister(address, data);
        } else if (address >= WRAM_START && address <= WRAM_END) {
            //cartridge.writePrg(address, data);
            log.warn("Writing {} to {}", "%02X".formatted(data), "%04X".formatted(address));
            wram[address - WRAM_START] = data & 0xFF;
        }
    }

    @Override
    public void reset() {
        cpu.reset();
        ppu.reset();
        cartridge.reset();
    }
}
