package no.clueless.emulation.impl;

import no.clueless.emulation.*;

public class BusImpl implements Bus {
    private final Cpu6502 cpu;
    private final Ppu2C02 ppu;
    private final APU     apu;

    private final int[]     cpuRam = new int[2048];
    private       Cartridge cartridge;

    public BusImpl(Cpu6502 cpu, Ppu2C02 ppu, APU apu) {
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
    public APU getApu() {
        return apu;
    }

    @Override
    public Cartridge getCartridge() {
        return null;
    }

    @Override
    public void insertCartridge(Cartridge cartridge) {
        this.cartridge = cartridge;
        this.ppu.connectToCartridge(cartridge);
    }

    @Override
    public void clock() {
        apu.clock();
        ppu.clock();
    }

    @Override
    public int read(int address) {
        var data = 0x00;

        if (address >= 0x0000 && address <= 0x1FFF) {
            data = cpuRam[address % cpuRam.length];
        }

        return data;
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
