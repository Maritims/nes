package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.cpu.OpcodeFunction;
import no.clueless.emulation.util.ResolvedAddress;

public class SetFlag implements OpcodeFunction {
    private final Cpu6502.Flag flag;

    public SetFlag(Cpu6502.Flag flag) {
        this.flag = flag;
    }

    @Override
    public int execute(Cpu6502 cpu, ResolvedAddress ignored) {
        cpu.setFlag(flag, true);
        return 0;
    }
}
