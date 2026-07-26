package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;

public class ReturnFromInterrupt implements OpcodeFunction {
    @Override
    public int execute(Cpu6502 cpu, int address) {
        var status = cpu.pullFromStack();
        cpu.setFlag(Cpu6502.Flag.NEGATIVE, (status & Cpu6502.Flag.NEGATIVE.getValue()) != 0);
        cpu.setFlag(Cpu6502.Flag.OVERFLOW, (status & Cpu6502.Flag.OVERFLOW.getValue()) != 0);
        cpu.setFlag(Cpu6502.Flag.DECIMAL_MODE, (status & Cpu6502.Flag.DECIMAL_MODE.getValue()) != 0);
        cpu.setFlag(Cpu6502.Flag.INTERRUPT_DISABLE, (status & Cpu6502.Flag.INTERRUPT_DISABLE.getValue()) != 0);
        cpu.setFlag(Cpu6502.Flag.ZERO, (status & Cpu6502.Flag.ZERO.getValue()) != 0);
        cpu.setFlag(Cpu6502.Flag.CARRY, (status & Cpu6502.Flag.CARRY.getValue()) != 0);

        var pcLowByte  = cpu.pullFromStack();
        var pcHighByte = cpu.pullFromStack();
        var pc         = (pcHighByte << 8) | pcLowByte;
        cpu.setProgramCounter(pc);

        return 6;
    }
}
