package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;

public class PushAccumulator implements OpcodeFunction {
    @Override
    public int execute(Cpu6502 cpu, int address) {
        cpu.pushToStack(cpu.getAccumulator());
        return 3;
    }
}
