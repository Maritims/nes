package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;
import no.clueless.emulation.util.ResolvedAddress;

public class ShiftRightAndEor implements OpcodeFunction {
    private ShiftRightAndEor() {
    }

    public static final ShiftRightAndEor SRE = new ShiftRightAndEor();

    @Override
    public int execute(Cpu6502 cpu, ResolvedAddress address) {
        LogicalShiftRight.LSR.execute(cpu, address);
        BitwiseOperation.EOR.execute(cpu, address);
        return 0;
    }
}
