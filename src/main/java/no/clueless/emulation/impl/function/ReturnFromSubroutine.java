package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.cpu.OpcodeFunction;
import no.clueless.emulation.util.ResolvedAddress;

public class ReturnFromSubroutine implements OpcodeFunction {
    @Override
    public int execute(Cpu6502 cpu, ResolvedAddress ignored) {
        var pcLowByte  = cpu.pullFromStack();
        var pcHighByte = cpu.pullFromStack();
        var pc         = (pcHighByte << 8) | pcLowByte;
        cpu.setProgramCounter((pc + 1) & 0xFFFF);
        return 0;
    }
}
