package no.clueless.emulation.cpu;

import no.clueless.emulation.types.UnsignedWord;
import no.clueless.emulation.types.UnsignedByte;

/**
 * Represents the stack pointer in the CPU.
 */
public class StackPointer {
    private       UnsignedByte value;
    private final int          offset;

    public StackPointer(UnsignedByte value, int offset) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("offset must be positive");
        }

        this.value = value;
        this.offset = offset;
    }

    public UnsignedByte getValue() {
        return value;
    }

    public void setValue(UnsignedByte value) {
        this.value = value;
    }

    /**
     * Resolves the stack pointer to an address.
     *
     * @return The address.
     */
    public UnsignedWord toAddress() {
        return new UnsignedWord(offset + value.intValue());
    }

    public void decrement() {
        value = value.decrement();
    }

    public void increment() {
        value = value.increment();
    }
}