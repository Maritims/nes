package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;

public class PullAccumulator implements OpcodeFunction {
    @Override
    public int execute(Cpu6502 cpu, int ignored) {
        var value = cpu.pullFromStack();

        cpu.setAccumulator(value);
        cpu.setFlag(Cpu6502.Flag.ZERO, value == 0);
        cpu.setFlag(Cpu6502.Flag.NEGATIVE, (value & 0x80) != 0);

        return 4;
    }
}
