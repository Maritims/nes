package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;

public class ShiftLeftAndOr implements OpcodeFunction {
    private final ArithmeticShiftLeft asl = ArithmeticShiftLeft.ASL;
    private final BitwiseOR           ora = BitwiseOR.ORA;

    @Override
    public int execute(Cpu6502 cpu, int address) {
        asl.execute(cpu, address);
        return ora.execute(cpu, address);
    }
}
