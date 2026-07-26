package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;

public class Break implements OpcodeFunction {
    @Override
    public int execute(Cpu6502 cpu, int ignored) {
        var pc            = cpu.getProgramCounter();
        var returnAddress = (pc + 1) & 0xFFFF;

        cpu.pushToStack((returnAddress >> 8) & 0xFF);
        cpu.pushToStack(returnAddress & 0xFF);
        cpu.pushToStack(cpu.getStatusRegister() | Cpu6502.Flag.BREAK.getValue() | Cpu6502.Flag.UNUSED.getValue());

        cpu.setFlag(Cpu6502.Flag.INTERRUPT_DISABLE, true);

        var newPcLowByte  = cpu.read(0xFFFE);
        var newPcHighByte = cpu.read(0xFFFF);
        var newPc         = (newPcHighByte << 8) | newPcLowByte;

        cpu.setProgramCounter(newPc);

        return 7;
    }
}
