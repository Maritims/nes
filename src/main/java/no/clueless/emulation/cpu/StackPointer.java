package no.clueless.emulation.cpu;

import no.clueless.emulation.types.UInt16;
import no.clueless.emulation.types.UInt8;

/**
 * Represents the stack pointer in the CPU.
 */
public class StackPointer extends Register<UInt8> {
    private final int offset;

    public StackPointer(UInt8 value, int offset) {
        super(value);

        if (offset < 0) {
            throw new IllegalArgumentException("offset must be positive");
        }

        this.offset = offset;
    }

    /**
     * Resolves the stack pointer to an address.
     *
     * @return The address.
     */
    public UInt16 toAddress() {
        return new UInt16(offset + getValue().value());
    }
}