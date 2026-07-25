package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;

public class SubtractWithCarry implements OpcodeFunction {
    private SubtractWithCarry() {}

    public static final SubtractWithCarry INSTANCE = new SubtractWithCarry();

    @Override
    public int execute(Cpu6502 cpu, int address) {
        return 0;
    }
}
