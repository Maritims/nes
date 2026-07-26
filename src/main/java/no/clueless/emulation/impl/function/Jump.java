package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;

public class Jump implements OpcodeFunction {
    @Override
    public int execute(Cpu6502 cpu, int address) {
        cpu.setProgramCounter(address);
        return 0;
    }
}
