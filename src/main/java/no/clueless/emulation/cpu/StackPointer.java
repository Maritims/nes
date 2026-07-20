package no.clueless.emulation.cpu;

import no.clueless.emulation.types.UInt8;

/**
 * Represents the stack pointer in the CPU.
 */
public class StackPointer extends Register<UInt8, StackPointer> {
    public StackPointer(UInt8 value) {
        super(value);
    }

    @Override
    protected StackPointer create(UInt8 newValue) {
        return new StackPointer(newValue);
    }
}