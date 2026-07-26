package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;
import no.clueless.emulation.util.ResolvedAddress;

public class DecrementAndCompare implements OpcodeFunction {
    private final Decrement decrement = Decrement.DEC;
    private final Compare   compare   = Compare.CMP;

    @Override
    public int execute(Cpu6502 cpu, ResolvedAddress address) {
        decrement.execute(cpu, address);
        compare.execute(cpu, address);
        return 0;
    }
}
