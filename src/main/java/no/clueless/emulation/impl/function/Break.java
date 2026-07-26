package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;

public class Break implements OpcodeFunction {
    @Override
    public int execute(Cpu6502 cpu, int ignored) {
        var pc = cpu.getAndIncrementProgramCounter();

        cpu.setFlag(Cpu6502.Flag.INTERRUPT_DISABLE, true);
        var pcLowByte  = (pc >> 8) & 0x00FF;
        var pcHighByte = pc & 0x00FF;
        cpu.pushToStack(pcLowByte);
        cpu.pushToStack(pcHighByte);

        cpu.setFlag(Cpu6502.Flag.BREAK, true);
        cpu.pushToStack(cpu.getStatusRegister());
        cpu.setFlag(Cpu6502.Flag.BREAK, false);

        var newPcLowByte  = cpu.read(0xFFFE);
        var newPcHighByte = cpu.read(0xFFFF);
        var newPc         = (newPcHighByte << 8) | newPcLowByte;
        cpu.setProgramCounter(newPc);

        return 7;
    }
}
