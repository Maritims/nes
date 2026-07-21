package no.clueless.emulation.cpu;

import no.clueless.emulation.types.UnsignedByte;

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
        LOOKUP_TABLE[opcode.code().intValue()] = opcode;
    }

    /**
     * Get the opcode for the given opcode code.
     *
     * @param opcode The opcode code.
     * @return The opcode.
     * @throws IllegalArgumentException if opcode is null.
     */
    public static Opcode get(UnsignedByte opcode) {
        if (opcode == null) {
            throw new IllegalArgumentException("opcode cannot be null");
        }
        return LOOKUP_TABLE[opcode.intValue()];
    }

    static {
        // BRK
        register(new Opcode(0x00, BRK, 1, 7, cpu -> null));

        register(new Opcode(0x24, Instruction.BIT, 2, 3, CPU::addressZeroPage));
        register(new Opcode(0x2C, Instruction.BIT, 3, 4, CPU::addressAbsolute));

        // --- Branch Operations ---
        register(new Opcode(0x90, Instruction.BCC, 2, 2, CPU::addressRelative)); // Branch if Carry Clear
        register(new Opcode(0xB0, Instruction.BCS, 2, 2, CPU::addressRelative)); // Branch if Carry Set
        register(new Opcode(0xF0, Instruction.BEQ, 2, 2, CPU::addressRelative)); // Branch if Equal (Zero Set)
        register(new Opcode(0xD0, Instruction.BNE, 2, 2, CPU::addressRelative)); // Branch if Not Equal (Zero Clear)
        register(new Opcode(0x30, Instruction.BMI, 2, 2, CPU::addressRelative)); // Branch if Minus (Negative Set)
        register(new Opcode(0x10, Instruction.BPL, 2, 2, CPU::addressRelative)); // Branch if Plus (Negative Clear)
        register(new Opcode(0x50, Instruction.BVC, 2, 2, CPU::addressRelative)); // Branch if Overflow Clear
        register(new Opcode(0x70, Instruction.BVS, 2, 2, CPU::addressRelative)); // Branch if Overflow Set

        // LDA (Load Accumulator)
        register(new Opcode(0xA9, LDA, 2, 2, CPU::addressImmediate));
        register(new Opcode(0xA5, LDA, 2, 3, CPU::addressZeroPage));
        register(new Opcode(0xB5, LDA, 2, 4, CPU::addressZeroPageX));
        register(new Opcode(0xAD, LDA, 3, 4, CPU::addressAbsolute));
        register(new Opcode(0xBD, LDA, 3, 4, CPU::addressAbsoluteX));
        register(new Opcode(0xB9, LDA, 3, 4, CPU::addressAbsoluteY));
        register(new Opcode(0xA1, LDA, 2, 6, CPU::addressIndirectX));
        register(new Opcode(0xB1, LDA, 2, 5, CPU::addressIndirectY));

        // LDX (Load X)
        register(new Opcode(0xA2, LDX, 2, 2, CPU::addressImmediate));
        register(new Opcode(0xA6, LDX, 2, 3, CPU::addressZeroPage));
        register(new Opcode(0xB6, LDX, 2, 4, CPU::addressZeroPageY));
        register(new Opcode(0xAE, LDX, 3, 4, CPU::addressAbsolute));
        register(new Opcode(0xBE, LDX, 3, 4, CPU::addressAbsoluteY));

        // LDY (Load Y)
        register(new Opcode(0xA0, LDY, 2, 2, CPU::addressImmediate));
        register(new Opcode(0xA4, LDY, 2, 3, CPU::addressZeroPage));
        register(new Opcode(0xB4, LDY, 2, 4, CPU::addressZeroPageX));
        register(new Opcode(0xAC, LDY, 3, 4, CPU::addressAbsolute));
        register(new Opcode(0xBC, LDY, 3, 4, CPU::addressAbsoluteX));

        // STA (Store Accumulator)
        register(new Opcode(0x85, STA, 2, 3, CPU::addressZeroPage));
        register(new Opcode(0x95, STA, 2, 4, CPU::addressZeroPageX));
        register(new Opcode(0x8D, STA, 3, 4, CPU::addressAbsolute));
        register(new Opcode(0x9D, STA, 3, 5, CPU::addressAbsoluteX));
        register(new Opcode(0x99, STA, 3, 5, CPU::addressAbsoluteY));
        register(new Opcode(0x81, STA, 2, 6, CPU::addressIndirectX));
        register(new Opcode(0x91, STA, 2, 6, CPU::addressIndirectY));

        // STX (Store X)
        register(new Opcode(0x86, STX, 2, 3, CPU::addressZeroPage));
        register(new Opcode(0x96, STX, 2, 4, CPU::addressZeroPageY));
        register(new Opcode(0x8E, STX, 3, 4, CPU::addressAbsolute));

        // STY (Store Y)
        register(new Opcode(0x84, STY, 2, 3, CPU::addressZeroPage));
        register(new Opcode(0x94, STY, 2, 4, CPU::addressZeroPageX));
        register(new Opcode(0x8C, STY, 3, 4, CPU::addressAbsolute));

        // ADC (Add with Carry)
        register(new Opcode(0x69, ADC, 2, 2, CPU::addressImmediate));
        register(new Opcode(0x65, ADC, 2, 3, CPU::addressZeroPage));
        register(new Opcode(0x75, ADC, 2, 4, CPU::addressZeroPageX));
        register(new Opcode(0x6D, ADC, 3, 4, CPU::addressAbsolute));
        register(new Opcode(0x7D, ADC, 3, 4, CPU::addressAbsoluteX));
        register(new Opcode(0x79, ADC, 3, 4, CPU::addressAbsoluteY));
        register(new Opcode(0x61, ADC, 2, 6, CPU::addressIndirectX));
        register(new Opcode(0x71, ADC, 2, 5, CPU::addressIndirectY));

        // SBC (Subtract with Carry)
        register(new Opcode(0xE9, SBC, 2, 2, CPU::addressImmediate));
        register(new Opcode(0xE5, SBC, 2, 3, CPU::addressZeroPage));
        register(new Opcode(0xF5, SBC, 2, 4, CPU::addressZeroPageX));
        register(new Opcode(0xED, SBC, 3, 4, CPU::addressAbsolute));
        register(new Opcode(0xFD, SBC, 3, 4, CPU::addressAbsoluteX));
        register(new Opcode(0xF9, SBC, 3, 4, CPU::addressAbsoluteY));
        register(new Opcode(0xE1, SBC, 2, 6, CPU::addressIndirectX));
        register(new Opcode(0xF1, SBC, 2, 5, CPU::addressIndirectY));

        // JMP
        register(new Opcode(0x4C, JMP, 3, 3, CPU::addressAbsolute));
        register(new Opcode(0x6C, JMP, 3, 5, CPU::addressIndirect));

        // JSR
        register(new Opcode(0x20, JSR, 3, 6, CPU::addressAbsolute));

        // RTS
        register(new Opcode(0x60, RTS, 1, 6, cpu -> null));

        // RTI
        register(new Opcode(0x40, RTI, 1, 6, cpu -> null));

        // SEC, SED, SEI, CLC, CLD, CLI, CLV
        register(new Opcode(0x38, SEC, 1, 2, cpu -> null));
        register(new Opcode(0xF8, SED, 1, 2, cpu -> null));
        register(new Opcode(0x78, SEI, 1, 2, cpu -> null));
        register(new Opcode(0x18, CLC, 1, 2, cpu -> null));
        register(new Opcode(0xD8, CLD, 1, 2, cpu -> null));
        register(new Opcode(0x58, CLI, 1, 2, cpu -> null));
        register(new Opcode(0xB8, CLV, 1, 2, cpu -> null));

        // PHA, PHP, PLA, PLP
        register(new Opcode(0x48, PHA, 1, 3, cpu -> null));
        register(new Opcode(0x08, PHP, 1, 3, cpu -> null));
        register(new Opcode(0x68, PLA, 1, 4, cpu -> null));
        register(new Opcode(0x28, PLP, 1, 4, cpu -> null));

        // TAX, TAY, TSX, TXA, TXS, TYA
        register(new Opcode(0xAA, TAX, 1, 2, cpu -> null));
        register(new Opcode(0xA8, TAY, 1, 2, cpu -> null));
        register(new Opcode(0xBA, TSX, 1, 2, cpu -> null));
        register(new Opcode(0x8A, TXA, 1, 2, cpu -> null));
        register(new Opcode(0x9A, TXS, 1, 2, cpu -> null));
        register(new Opcode(0x98, TYA, 1, 2, cpu -> null));

        // INX, INY, DEX, DEY
        register(new Opcode(0xE8, INX, 1, 2, cpu -> null));
        register(new Opcode(0xC8, INY, 1, 2, cpu -> null));
        register(new Opcode(0xCA, DEX, 1, 2, cpu -> null));
        register(new Opcode(0x88, DEY, 1, 2, cpu -> null));

        // NOP
        register(new Opcode(0xEA, NOP, 1, 2, cpu -> null));
        // Illegal NOPs
        register(new Opcode(0x04, NOP, 2, 3, CPU::addressZeroPage));
        register(new Opcode(0x44, NOP, 2, 3, CPU::addressZeroPage));
        register(new Opcode(0x64, NOP, 2, 3, CPU::addressZeroPage));
        register(new Opcode(0x0C, NOP, 3, 4, CPU::addressAbsolute));
        register(new Opcode(0x1C, NOP, 3, 4, CPU::addressAbsoluteX));
        register(new Opcode(0x3C, NOP, 3, 4, CPU::addressAbsoluteX));
        register(new Opcode(0x5C, NOP, 3, 4, CPU::addressAbsoluteX));
        register(new Opcode(0x7C, NOP, 3, 4, CPU::addressAbsoluteX));
        register(new Opcode(0xDC, NOP, 3, 4, CPU::addressAbsoluteX));
        register(new Opcode(0xFC, NOP, 3, 4, CPU::addressAbsoluteX));
        register(new Opcode(0x80, NOP, 2, 2, CPU::addressImmediate));
        register(new Opcode(0x82, NOP, 2, 2, CPU::addressImmediate));
        register(new Opcode(0x89, NOP, 2, 2, CPU::addressImmediate));
        register(new Opcode(0xC2, NOP, 2, 2, CPU::addressImmediate));
        register(new Opcode(0xE2, NOP, 2, 2, CPU::addressImmediate));
        register(new Opcode(0x14, NOP, 2, 4, CPU::addressZeroPageX));
        register(new Opcode(0x34, NOP, 2, 4, CPU::addressZeroPageX));
        register(new Opcode(0x54, NOP, 2, 4, CPU::addressZeroPageX));
        register(new Opcode(0x74, NOP, 2, 4, CPU::addressZeroPageX));
        register(new Opcode(0xD4, NOP, 2, 4, CPU::addressZeroPageX));
        register(new Opcode(0xF4, NOP, 2, 4, CPU::addressZeroPageX));
        register(new Opcode(0x1A, NOP, 1, 2, cpu -> null));
        register(new Opcode(0x3A, NOP, 1, 2, cpu -> null));
        register(new Opcode(0x5A, NOP, 1, 2, cpu -> null));
        register(new Opcode(0x7A, NOP, 1, 2, cpu -> null));
        register(new Opcode(0xDA, NOP, 1, 2, cpu -> null));
        register(new Opcode(0xFA, NOP, 1, 2, cpu -> null));

        // AND
        register(new Opcode(0x29, AND, 2, 2, CPU::addressImmediate));
        register(new Opcode(0x25, AND, 2, 3, CPU::addressZeroPage));
        register(new Opcode(0x35, AND, 2, 4, CPU::addressZeroPageX));
        register(new Opcode(0x2D, AND, 3, 4, CPU::addressAbsolute));
        register(new Opcode(0x3D, AND, 3, 4, CPU::addressAbsoluteX));
        register(new Opcode(0x39, AND, 3, 4, CPU::addressAbsoluteY));
        register(new Opcode(0x21, AND, 2, 6, CPU::addressIndirectX));
        register(new Opcode(0x31, AND, 2, 5, CPU::addressIndirectY));

        // ORA
        register(new Opcode(0x09, ORA, 2, 2, CPU::addressImmediate));
        register(new Opcode(0x05, ORA, 2, 3, CPU::addressZeroPage));
        register(new Opcode(0x15, ORA, 2, 4, CPU::addressZeroPageX));
        register(new Opcode(0x0D, ORA, 3, 4, CPU::addressAbsolute));
        register(new Opcode(0x1D, ORA, 3, 4, CPU::addressAbsoluteX));
        register(new Opcode(0x19, ORA, 3, 4, CPU::addressAbsoluteY));
        register(new Opcode(0x01, ORA, 2, 6, CPU::addressIndirectX));
        register(new Opcode(0x11, ORA, 2, 5, CPU::addressIndirectY));

        // EOR
        register(new Opcode(0x49, EOR, 2, 2, CPU::addressImmediate));
        register(new Opcode(0x45, EOR, 2, 3, CPU::addressZeroPage));
        register(new Opcode(0x55, EOR, 2, 4, CPU::addressZeroPageX));
        register(new Opcode(0x4D, EOR, 3, 4, CPU::addressAbsolute));
        register(new Opcode(0x5D, EOR, 3, 4, CPU::addressAbsoluteX));
        register(new Opcode(0x59, EOR, 3, 4, CPU::addressAbsoluteY));
        register(new Opcode(0x41, EOR, 2, 6, CPU::addressIndirectX));
        register(new Opcode(0x51, EOR, 2, 5, CPU::addressIndirectY));

        // CMP
        register(new Opcode(0xC9, CMP, 2, 2, CPU::addressImmediate));
        register(new Opcode(0xC5, CMP, 2, 3, CPU::addressZeroPage));
        register(new Opcode(0xD5, CMP, 2, 4, CPU::addressZeroPageX));
        register(new Opcode(0xCD, CMP, 3, 4, CPU::addressAbsolute));
        register(new Opcode(0xDD, CMP, 3, 4, CPU::addressAbsoluteX));
        register(new Opcode(0xD9, CMP, 3, 4, CPU::addressAbsoluteY));
        register(new Opcode(0xC1, CMP, 2, 6, CPU::addressIndirectX));
        register(new Opcode(0xD1, CMP, 2, 5, CPU::addressIndirectY));

        // CPX
        register(new Opcode(0xE0, CPX, 2, 2, CPU::addressImmediate));
        register(new Opcode(0xE4, CPX, 2, 3, CPU::addressZeroPage));
        register(new Opcode(0xEC, CPX, 3, 4, CPU::addressAbsolute));

        // CPY
        register(new Opcode(0xC0, CPY, 2, 2, CPU::addressImmediate));
        register(new Opcode(0xC4, CPY, 2, 3, CPU::addressZeroPage));
        register(new Opcode(0xCC, CPY, 3, 4, CPU::addressAbsolute));

        // ASL
        register(new Opcode(0x0A, ASL, 1, 2, cpu -> null));
        register(new Opcode(0x06, ASL, 2, 5, CPU::addressZeroPage));
        register(new Opcode(0x16, ASL, 2, 6, CPU::addressZeroPageX));
        register(new Opcode(0x0E, ASL, 3, 6, CPU::addressAbsolute));
        register(new Opcode(0x1E, ASL, 3, 7, CPU::addressAbsoluteX));

        // LSR
        register(new Opcode(0x4A, LSR, 1, 2, cpu -> null));
        register(new Opcode(0x46, LSR, 2, 5, CPU::addressZeroPage));
        register(new Opcode(0x56, LSR, 2, 6, CPU::addressZeroPageX));
        register(new Opcode(0x4E, LSR, 3, 6, CPU::addressAbsolute));
        register(new Opcode(0x5E, LSR, 3, 7, CPU::addressAbsoluteX));

        // ROL
        register(new Opcode(0x2A, ROL, 1, 2, cpu -> null));
        register(new Opcode(0x26, ROL, 2, 5, CPU::addressZeroPage));
        register(new Opcode(0x36, ROL, 2, 6, CPU::addressZeroPageX));
        register(new Opcode(0x2E, ROL, 3, 6, CPU::addressAbsolute));
        register(new Opcode(0x3E, ROL, 3, 7, CPU::addressAbsoluteX));

        // ROR
        register(new Opcode(0x6A, ROR, 1, 2, cpu -> null));
        register(new Opcode(0x66, ROR, 2, 5, CPU::addressZeroPage));
        register(new Opcode(0x76, ROR, 2, 6, CPU::addressZeroPageX));
        register(new Opcode(0x6E, ROR, 3, 6, CPU::addressAbsolute));
        register(new Opcode(0x7E, ROR, 3, 7, CPU::addressAbsoluteX));

        // INC
        register(new Opcode(0xE6, INC, 2, 5, CPU::addressZeroPage));
        register(new Opcode(0xF6, INC, 2, 6, CPU::addressZeroPageX));
        register(new Opcode(0xEE, INC, 3, 6, CPU::addressAbsolute));
        register(new Opcode(0xFE, INC, 3, 7, CPU::addressAbsoluteX));

        // DEC
        register(new Opcode(0xC6, DEC, 2, 5, CPU::addressZeroPage));
        register(new Opcode(0xD6, DEC, 2, 6, CPU::addressZeroPageX));
        register(new Opcode(0xCE, DEC, 3, 6, CPU::addressAbsolute));
        register(new Opcode(0xDE, DEC, 3, 7, CPU::addressAbsoluteX));
        
        // LAX (Illegal)
        register(new Opcode(0xA7, LAX, 2, 3, CPU::addressZeroPage));
        register(new Opcode(0xB7, LAX, 2, 4, CPU::addressZeroPageY));
        register(new Opcode(0xAF, LAX, 3, 4, CPU::addressAbsolute));
        register(new Opcode(0xBF, LAX, 3, 4, CPU::addressAbsoluteY));
        register(new Opcode(0xA3, LAX, 2, 6, CPU::addressIndirectX));
        register(new Opcode(0xB3, LAX, 2, 5, CPU::addressIndirectY));

        // SAX (Illegal)
        register(new Opcode(0x87, SAX, 2, 3, CPU::addressZeroPage));
        register(new Opcode(0x97, SAX, 2, 4, CPU::addressZeroPageY));
        register(new Opcode(0x8F, SAX, 3, 4, CPU::addressAbsolute));
        register(new Opcode(0x83, SAX, 2, 6, CPU::addressIndirectX));

        // SBC (Illegal/Duplicate)
        register(new Opcode(0xEB, SBC, 2, 2, CPU::addressImmediate));

        // DCP (Illegal)
        register(new Opcode(0xC7, DCP, 2, 5, CPU::addressZeroPage));
        register(new Opcode(0xD7, DCP, 2, 6, CPU::addressZeroPageX));
        register(new Opcode(0xCF, DCP, 3, 6, CPU::addressAbsolute));
        register(new Opcode(0xDF, DCP, 3, 7, CPU::addressAbsoluteX));
        register(new Opcode(0xDB, DCP, 3, 7, CPU::addressAbsoluteY));
        register(new Opcode(0xC3, DCP, 2, 8, CPU::addressIndirectX));
        register(new Opcode(0xD3, DCP, 2, 8, CPU::addressIndirectY));

        // ISB (Illegal)
        register(new Opcode(0xE7, ISB, 2, 5, CPU::addressZeroPage));
        register(new Opcode(0xF7, ISB, 2, 6, CPU::addressZeroPageX));
        register(new Opcode(0xEF, ISB, 3, 6, CPU::addressAbsolute));
        register(new Opcode(0xFF, ISB, 3, 7, CPU::addressAbsoluteX));
        register(new Opcode(0xFB, ISB, 3, 7, CPU::addressAbsoluteY));
        register(new Opcode(0xE3, ISB, 2, 8, CPU::addressIndirectX));
        register(new Opcode(0xF3, ISB, 2, 8, CPU::addressIndirectY));

        // RLA (Illegal)
        register(new Opcode(0x27, RLA, 2, 5, CPU::addressZeroPage));
        register(new Opcode(0x37, RLA, 2, 6, CPU::addressZeroPageX));
        register(new Opcode(0x2F, RLA, 3, 6, CPU::addressAbsolute));
        register(new Opcode(0x3F, RLA, 3, 7, CPU::addressAbsoluteX));
        register(new Opcode(0x3B, RLA, 3, 7, CPU::addressAbsoluteY));
        register(new Opcode(0x23, RLA, 2, 8, CPU::addressIndirectX));
        register(new Opcode(0x33, RLA, 2, 8, CPU::addressIndirectY));

        // RRA (Illegal)
        register(new Opcode(0x67, RRA, 2, 5, CPU::addressZeroPage));
        register(new Opcode(0x77, RRA, 2, 6, CPU::addressZeroPageX));
        register(new Opcode(0x6F, RRA, 3, 6, CPU::addressAbsolute));
        register(new Opcode(0x7F, RRA, 3, 7, CPU::addressAbsoluteX));
        register(new Opcode(0x7B, RRA, 3, 7, CPU::addressAbsoluteY));
        register(new Opcode(0x63, RRA, 2, 8, CPU::addressIndirectX));
        register(new Opcode(0x73, RRA, 2, 8, CPU::addressIndirectY));

        // SLO (Illegal)
        register(new Opcode(0x07, SLO, 2, 5, CPU::addressZeroPage));
        register(new Opcode(0x17, SLO, 2, 6, CPU::addressZeroPageX));
        register(new Opcode(0x0F, SLO, 3, 6, CPU::addressAbsolute));
        register(new Opcode(0x1F, SLO, 3, 7, CPU::addressAbsoluteX));
        register(new Opcode(0x1B, SLO, 3, 7, CPU::addressAbsoluteY));
        register(new Opcode(0x03, SLO, 2, 8, CPU::addressIndirectX));
        register(new Opcode(0x13, SLO, 2, 8, CPU::addressIndirectY));

        // SRE (Illegal)
        register(new Opcode(0x47, SRE, 2, 5, CPU::addressZeroPage));
        register(new Opcode(0x57, SRE, 2, 6, CPU::addressZeroPageX));
        register(new Opcode(0x4F, SRE, 3, 6, CPU::addressAbsolute));
        register(new Opcode(0x5F, SRE, 3, 7, CPU::addressAbsoluteX));
        register(new Opcode(0x5B, SRE, 3, 7, CPU::addressAbsoluteY));
        register(new Opcode(0x43, SRE, 2, 8, CPU::addressIndirectX));
        register(new Opcode(0x53, SRE, 2, 8, CPU::addressIndirectY));
    }
}
