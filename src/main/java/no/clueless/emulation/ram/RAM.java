package no.clueless.emulation.ram;

import no.clueless.emulation.Bus;
import no.clueless.emulation.types.UInt16;
import no.clueless.emulation.types.UInt8;

import java.util.Arrays;

/**
 * Represents the RAM in the CPU.
 */
public class RAM implements Bus {
    /**
     * 64K system memory.
     */
    private final UInt8[] memory = new UInt8[65536];

    /**
     * Constructor. Initializes the memory with all zeros.
     */
    public RAM() {
        Arrays.fill(memory, new UInt8(0x00));
    }

    @Override
    public UInt8 read(UInt16 address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        return memory[address.value()];
    }

    @Override
    public void write(UInt16 address, UInt8 value) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        memory[address.value()] = value;
    }
}
