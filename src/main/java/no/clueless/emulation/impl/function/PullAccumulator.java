package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;
import no.clueless.emulation.util.ResolvedAddress;

public class PullAccumulator implements OpcodeFunction {
    @Override
    public int execute(Cpu6502 cpu, ResolvedAddress ignored) {
        var value = cpu.pullFromStack();

        cpu.setAccumulator(value);
        cpu.setFlag(Cpu6502.Flag.ZERO, value == 0);
        cpu.setFlag(Cpu6502.Flag.NEGATIVE, (value & 0x80) != 0);

        return 0;
    }
}
