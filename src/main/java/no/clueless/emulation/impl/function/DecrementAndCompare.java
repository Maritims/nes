package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;

import static no.clueless.emulation.impl.Opcode.CMP;

public class DecrementAndCompare implements OpcodeFunction {
    private final Decrement decrement = Decrement.DEC;
    private final Compare   compare   = Compare.CMP;

    @Override
    public int execute(Cpu6502 cpu, int address) {
        decrement.execute(cpu, address);
        return compare.execute(cpu, address);
    }
}
