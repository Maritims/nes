package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;

public class ShiftRightAndEor implements OpcodeFunction {
    private final LogicalShiftRight  lsr = LogicalShiftRight.LSR;
    private final BitwiseExclusiveOR eor = BitwiseExclusiveOR.EOR;

    private ShiftRightAndEor() {}

    public static final ShiftRightAndEor SRE = new ShiftRightAndEor();

    @Override
    public int execute(Cpu6502 cpu, int address) {
        lsr.execute(cpu, address);
        return eor.execute(cpu, address);
    }
}
