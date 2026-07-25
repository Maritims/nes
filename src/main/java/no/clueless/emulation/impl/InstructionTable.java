package no.clueless.emulation.impl;

import no.clueless.emulation.impl.function.*;

public class InstructionTable {
    private static final Instruction[][] instructions = new Instruction[][]{
            Instruction.arrayOf(
                    "BRK", Break.class, Immediate.class, 1,
                    "ORA", BitwiseOR.class, IndirectX.class, 6,
                    null, null, null, 0, // Nothing exists at 0x02
                    "SLO", ShiftLeftAndOr.class, IndirectX.class, 8,
                    "NOP", NoOperation.class, ZeroPage.class,3,
                    "ORA", BitwiseOR.class, ZeroPage.class, 3,
                    "ASL", ArithmeticShiftLeft.class, ZeroPage.class, 5,
                    "SLO", ShiftLeftAndOr.class, ZeroPage.class, 5,
                    "PHP", PushProcessorStatus.class, null, 3,
                    "ORA", BitwiseOR.class, Immediate.class, 2,
                    "ASL", ArithmeticShiftLeft.class, null, 2,
                    null, null, null, 0, // Nothing exists at 0x0B.
                    "NOP", NoOperation.class, Absolute.class, 4,
                    "ORA", BitwiseOR.class, Absolute.class, 4,
                    "ASL", ArithmeticShiftLeft.class, Absolute.class, 6,
                    "SLO", ShiftLeftAndOr.class, Absolute.class, 6
            )
    };

    public static Instruction getInstruction(int opcode) {
        return instructions[opcode & 0xFF][opcode & 0xFF];
    }
}
