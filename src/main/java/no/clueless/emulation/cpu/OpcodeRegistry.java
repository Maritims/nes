package no.clueless.emulation.cpu;

import no.clueless.emulation.types.UInt8;

import static no.clueless.emulation.cpu.Instruction.*;

/**
 * Represents the opcode registry.
 */
public class OpcodeRegistry {
    private static final Opcode[] LOOKUP_TABLE = new Opcode[256];

    /**
     * Register an opcode.
     *
     * @param opcode The opcode to register.
     * @throws IllegalArgumentException if opcode is null.
     */
    private static void register(Opcode opcode) {
        if (opcode == null) {
            throw new IllegalArgumentException("opcode cannot be null");
        }
        LOOKUP_TABLE[opcode.code().value()] = opcode;
    }

    /**
     * Get the opcode for the given opcode code.
     *
     * @param opcode The opcode code.
     * @return The opcode.
     * @throws IllegalArgumentException if opcode is null.
     */
    public static Opcode get(UInt8 opcode) {
        if (opcode == null) {
            throw new IllegalArgumentException("opcode cannot be null");
        }
        return LOOKUP_TABLE[opcode.value()];
    }

    static {
        // BRK
        register(new Opcode(0x00, BRK, 1, 7, cpu -> null));

        // LDA (Load Accumulator)
        register(new Opcode(0xA9, LDA, 2, 2, CPU::addressImmediate));
        register(new Opcode(0xA5, LDA, 2, 3, CPU::addressZeroPage));
        register(new Opcode(0xB5, LDA, 2, 4, CPU::addressZeroPageX));
        register(new Opcode(0xAD, LDA, 3, 4, CPU::addressAbsolute));
        register(new Opcode(0xBD, LDA, 3, 4, CPU::addressAbsoluteX));
        register(new Opcode(0xB9, LDA, 3, 4, CPU::addressAbsoluteY));
        register(new Opcode(0xA1, LDA, 2, 4, CPU::addressIndirectX));
        register(new Opcode(0xB1, LDA, 2, 4, CPU::addressIndirectY));

        // STA (Store Accumulator)
        register(new Opcode(0x85, STA, 2, 3, CPU::addressZeroPage));
        register(new Opcode(0x95, STA, 2, 4, CPU::addressZeroPageX));
        register(new Opcode(0x8D, STA, 3, 4, CPU::addressAbsolute));
        register(new Opcode(0x9D, STA, 3, 5, CPU::addressAbsoluteX));
        register(new Opcode(0x99, STA, 3, 5, CPU::addressAbsoluteY));
        register(new Opcode(0x81, STA, 2, 6, CPU::addressIndirectX));
        register(new Opcode(0x91, STA, 2, 6, CPU::addressIndirectY));
    }
}
