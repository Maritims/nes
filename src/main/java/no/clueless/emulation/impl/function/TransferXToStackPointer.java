package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;

public class TransferXToStackPointer implements OpcodeFunction {
    @Override
    public int execute(Cpu6502 cpu, int address) {
        cpu.setStackPointer(cpu.getX() & 0xFF);
        return 2;
    }
}
