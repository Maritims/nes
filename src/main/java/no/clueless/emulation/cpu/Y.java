package no.clueless.emulation.cpu;

import no.clueless.emulation.types.UInt8;

/**
 * Represents the Y register in the CPU.
 */
public class Y extends Register<UInt8> {
    protected Y(UInt8 value) {
        super(value);
    }
}