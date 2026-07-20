package no.clueless.emulation.cpu;

import no.clueless.emulation.types.UInt16;

/**
 * Represents the program counter in the CPU.
 */
public class ProgramCounter extends Register<UInt16, ProgramCounter> {
    public ProgramCounter(UInt16 value) {
        super(value);
    }

    @Override
    protected ProgramCounter create(UInt16 newValue) {
        return new ProgramCounter(newValue);
    }
}
