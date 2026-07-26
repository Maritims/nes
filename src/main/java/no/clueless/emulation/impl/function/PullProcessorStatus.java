package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;

public class PullProcessorStatus implements OpcodeFunction {
    @Override
    public int execute(Cpu6502 cpu, int address) {
        var value = cpu.pullFromStack();

        cpu.setFlag(Cpu6502.Flag.NEGATIVE, (value & Cpu6502.Flag.NEGATIVE.getValue()) != 0);
        cpu.setFlag(Cpu6502.Flag.OVERFLOW, (value & Cpu6502.Flag.OVERFLOW.getValue()) != 0);
        cpu.setFlag(Cpu6502.Flag.DECIMAL_MODE, (value & Cpu6502.Flag.DECIMAL_MODE.getValue()) != 0);
        cpu.setFlag(Cpu6502.Flag.INTERRUPT_DISABLE, (value & Cpu6502.Flag.INTERRUPT_DISABLE.getValue()) != 0);
        cpu.setFlag(Cpu6502.Flag.ZERO, (value & Cpu6502.Flag.ZERO.getValue()) != 0);
        cpu.setFlag(Cpu6502.Flag.CARRY, (value & Cpu6502.Flag.CARRY.getValue()) != 0);

        return 4;
    }
}
