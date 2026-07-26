package no.clueless.emulation.impl.function;

import no.clueless.emulation.impl.OpcodeFunction;

public class SubtractWithCarry extends AddWithCarry implements OpcodeFunction {
    public static final SubtractWithCarry SBC = new SubtractWithCarry();

    private SubtractWithCarry() {
        super(true);
    }
}
