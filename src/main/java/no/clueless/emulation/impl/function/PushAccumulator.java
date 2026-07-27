package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.cpu.OpcodeFunction;
import no.clueless.emulation.util.ResolvedAddress;

public class PushAccumulator implements OpcodeFunction {
    @Override
    public int execute(Cpu6502 cpu, ResolvedAddress ignored) {
        cpu.pushToStack(cpu.getAccumulator());
        return 0;
    }
}
