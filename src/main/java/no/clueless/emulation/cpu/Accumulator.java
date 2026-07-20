package no.clueless.emulation.cpu;

import no.clueless.emulation.types.UInt8;

/**
 * Represents the accumulator in the CPU.
 */
public class Accumulator extends Register<UInt8> {
    public Accumulator(UInt8 value) {
        super(value);
    }
}
