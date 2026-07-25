package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;

public class BitwiseAND implements OpcodeFunction {
    private BitwiseAND() {}

    public static final BitwiseAND INSTANCE = new BitwiseAND();

    @Override
    public int execute(Cpu6502 cpu, int address) {
        return 0;
    }
}
