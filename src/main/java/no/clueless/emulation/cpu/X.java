package no.clueless.emulation.cpu;

import no.clueless.emulation.types.UInt8;

/**
 * Represents the X register in the CPU.
 */
public class X extends Register<UInt8, X> {
    public X(UInt8 value) {
        super(value);
    }

    @Override
    protected X create(UInt8 newValue) {
        return new X(newValue);
    }
}
