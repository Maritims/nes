package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;

public class ReturnFromSubroutine implements OpcodeFunction {
    @Override
    public int execute(Cpu6502 cpu, int address) {
        var pcLowByte  = cpu.pullFromStack();
        var pcHighByte = cpu.pullFromStack();
        var pc         = (pcHighByte << 8) | pcLowByte;
        cpu.setProgramCounter((pc + 1) & 0xFFFF);
        return 6;
    }
}
