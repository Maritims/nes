package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;

public class PullProcessorStatus implements OpcodeFunction {
    @Override
    public int execute(Cpu6502 cpu, int address) {
        var value = cpu.pullFromStack() & 0xFF;

        var newStatus = (value | Cpu6502.Flag.UNUSED.getValue()) & ~Cpu6502.Flag.BREAK.getValue();
        cpu.setStatusRegister(newStatus);

        return 4;
    }
}
