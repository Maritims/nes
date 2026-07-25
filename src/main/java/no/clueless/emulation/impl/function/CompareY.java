package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;

public class CompareY implements OpcodeFunction {
    @Override
    public int execute(Cpu6502 cpu, int address) {
        return 0;
    }
}
