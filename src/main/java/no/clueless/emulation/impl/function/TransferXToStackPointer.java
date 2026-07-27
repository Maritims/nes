package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.cpu.OpcodeFunction;
import no.clueless.emulation.util.ResolvedAddress;

public class TransferXToStackPointer implements OpcodeFunction {
    @Override
    public int execute(Cpu6502 cpu, ResolvedAddress ignored) {
        cpu.setStackPointer(cpu.getX() & 0xFF);
        return 0;
    }
}
