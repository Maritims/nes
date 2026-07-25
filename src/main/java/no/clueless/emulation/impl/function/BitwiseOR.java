package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;

public class BitwiseOR implements OpcodeFunction {
    private BitwiseOR() {}

    public static final BitwiseOR INSTANCE = new BitwiseOR();

    @Override
    public int execute(Cpu6502 cpu, int address) {
        return 0;
    }
}
