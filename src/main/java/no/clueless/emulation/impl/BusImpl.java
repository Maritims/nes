package no.clueless.emulation.impl;

import no.clueless.emulation.*;
import no.clueless.emulation.impl.cartridge.CartridgeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BusImpl implements Bus {
    private static final Logger  log = LoggerFactory.getLogger(BusImpl.class);
    private final        Cpu6502 cpu;
    private final        Ppu2C02 ppu;
    private final        Apu     apu;

    private final int[]     cpuRam          = new int[2048];
    private       Cartridge cartridge;
    private       int       totalClockCount = 0;

    public BusImpl(Cpu6502 cpu, Ppu2C02 ppu, Apu apu) {
        if (cpu == null) {
            throw new IllegalArgumentException("cpu cannot be null");
        }
        if (ppu == null) {
            throw new IllegalArgumentException("ppu cannot be null");
        }
        if (apu == null) {
            //throw new IllegalArgumentException("apu cannot be null");
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
    public Apu getApu() {
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
        //apu.clock();
        ppu.clock();

        if (totalClockCount > 0 && totalClockCount % 3 == 0) {
            cpu.clock();
        }

        totalClockCount++;
    }

    @Override
    public int read(int address) {
        var data = 0x00;

        if (address >= 0x0000 && address <= 0x1FFF) {
            data = cpuRam[address % cpuRam.length];
        } else if (address >= 0x2000 && address <= 0x3FFF) {
            data = ppu.read(address);
        } else if (address >= 0x4000 && address <= 0x04017) {
            // APU and I/O
        } else if (address >= 0x4018 && address <= 0x401F) {
            // APU and I/O test
        } else if (address >= 0x8000 && address <= 0xFFFF) {
            // Cartridge
            data = cartridge.readPrg(address).orElseThrow();
        } else {
            //log.warn("Read from unknown address: {}", "$%04X".formatted(address));
        }

        return data & 0xFF;
    }

    @Override
    public void write(int address, int data) {
        if (address >= 0x0000 && address <= 0x1FFF) {
            cpuRam[address % cpuRam.length] = data & 0xFF;
        }
    }

    @Override
    public void reset() {
        cpu.reset();
        ppu.reset();
        cartridge.reset();
    }
}
