package no.clueless.emulation.ram;

import no.clueless.emulation.*;
import no.clueless.emulation.types.UnsignedWord;
import no.clueless.emulation.types.UnsignedByte;

import java.util.Arrays;

/**
 * Represents the RAM in the CPU.
 */
public class RAM implements Bus {
    /**
     * 64K system memory.
     */
    private final UnsignedByte[] memory = new UnsignedByte[65536];

    /**
     * Constructor. Initializes the memory with all zeros.
     */
    public RAM() {
        Arrays.fill(memory, new UnsignedByte(0x00));
    }

    @Override
    public Cpu6502 getCpu() {
        return null;
    }

    @Override
    public Ppu2C02 getPpu() {
        return null;
    }

    @Override
    public APU getApu() {
        return null;
    }

    @Override
    public Cartridge getCartridge() {
        return null;
    }

    @Override
    public void insertCartridge(Cartridge cartridge) {

    }

    @Override
    public void clock() {

    }

    @Override
    public int read(int address) {
        return memory[address].intValue();
    }

    @Override
    public void write(int address, int value) {
        memory[address] = new UnsignedByte(value);
    }

    @Override
    public void reset() {

    }
}
