package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;

public class ClearFlag implements OpcodeFunction {
    private final Cpu6502.Flag flag;

    public ClearFlag(Cpu6502.Flag flag) {
        this.flag = flag;
    }

    @Override
    public int execute(Cpu6502 cpu, int address) {
        cpu.setFlag(flag, false);
        return 2;
    }
}
