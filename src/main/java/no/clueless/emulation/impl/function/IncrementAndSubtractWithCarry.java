package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;
import no.clueless.emulation.util.ResolvedAddress;

public class IncrementAndSubtractWithCarry implements OpcodeFunction {
    private final Increment         increment         = Increment.INC;
    private final SubtractWithCarry subtractWithCarry = SubtractWithCarry.SBC;

    @Override
    public int execute(Cpu6502 cpu, ResolvedAddress address) {
        increment.execute(cpu, address);
        subtractWithCarry.execute(cpu, address);
        return 0;
    }
}
