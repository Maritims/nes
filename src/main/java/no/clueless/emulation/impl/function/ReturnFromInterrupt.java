package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;
import no.clueless.emulation.util.ResolvedAddress;

public class ReturnFromInterrupt implements OpcodeFunction {
    @Override
    public int execute(Cpu6502 cpu, ResolvedAddress ignored) {
        var status    = cpu.pullFromStack() & 0xFF;
        var newStatus = (status | Cpu6502.Flag.UNUSED.getValue()) & ~Cpu6502.Flag.BREAK.getValue();
        cpu.setStatusRegister(newStatus);

        var pcLowByte  = cpu.pullFromStack() & 0xFF;
        var pcHighByte = cpu.pullFromStack() & 0xFF;
        var pc         = (pcHighByte << 8) | pcLowByte;

        cpu.setProgramCounter(pc);

        return 0;
    }
}
