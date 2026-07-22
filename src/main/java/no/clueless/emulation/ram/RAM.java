package no.clueless.emulation.ram;

import no.clueless.emulation.Bus;
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
    public UnsignedByte read(UnsignedWord address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        return memory[address.intValue()];
    }

    @Override
    public void write(UnsignedWord address, UnsignedByte value) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        memory[address.intValue()] = value;
    }
}
