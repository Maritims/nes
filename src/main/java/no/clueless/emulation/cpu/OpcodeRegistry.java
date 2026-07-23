package no.clueless.emulation.cpu;

import no.clueless.emulation.types.UnsignedByte;

import static no.clueless.emulation.cpu.AddressingMode.*;
import static no.clueless.emulation.cpu.Mnemonic.*;

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
        register(new Opcode(0x00, BRK, 1, IMMEDIATE));

        register(new Opcode(0x24, Mnemonic.BIT, 3, ZERO_PAGE));
        register(new Opcode(0x2C, Mnemonic.BIT, 4, ABSOLUTE));

        // --- Branch Operations ---
        register(new Opcode(0x90, Mnemonic.BCC, 2, RELATIVE)); // Branch if Carry Clear
        register(new Opcode(0xB0, Mnemonic.BCS, 2, RELATIVE)); // Branch if Carry Set
        register(new Opcode(0xF0, Mnemonic.BEQ, 2, RELATIVE)); // Branch if Equal (Zero Set)
        register(new Opcode(0xD0, Mnemonic.BNE, 2, RELATIVE)); // Branch if Not Equal (Zero Clear)
        register(new Opcode(0x30, Mnemonic.BMI, 2, RELATIVE)); // Branch if Minus (Negative Set)
        register(new Opcode(0x10, Mnemonic.BPL, 2, RELATIVE)); // Branch if Plus (Negative Clear)
        register(new Opcode(0x50, Mnemonic.BVC, 2, RELATIVE)); // Branch if Overflow Clear
        register(new Opcode(0x70, Mnemonic.BVS, 2, RELATIVE)); // Branch if Overflow Set

        // LDA (Load Accumulator)
        register(new Opcode(0xA9, LDA, 2, IMMEDIATE));
        register(new Opcode(0xA5, LDA, 3, ZERO_PAGE));
        register(new Opcode(0xB5, LDA, 4, ZERO_PAGE_X));
        register(new Opcode(0xAD, LDA, 4, ABSOLUTE));
        register(new Opcode(0xBD, LDA, 4, ABSOLUTE_X));
        register(new Opcode(0xB9, LDA, 4, ABSOLUTE_Y));
        register(new Opcode(0xA1, LDA, 6, INDIRECT_X));
        register(new Opcode(0xB1, LDA, 5, INDIRECT_Y));

        // LDX (Load X)
        register(new Opcode(0xA2, LDX, 2, IMMEDIATE));
        register(new Opcode(0xA6, LDX, 3, ZERO_PAGE));
        register(new Opcode(0xB6, LDX, 4, ZERO_PAGE_Y));
        register(new Opcode(0xAE, LDX, 4, ABSOLUTE));
        register(new Opcode(0xBE, LDX, 4, ABSOLUTE_Y));

        // LDY (Load Y)
        register(new Opcode(0xA0, LDY, 2, IMMEDIATE));
        register(new Opcode(0xA4, LDY, 3, ZERO_PAGE));
        register(new Opcode(0xB4, LDY, 4, ZERO_PAGE_X));
        register(new Opcode(0xAC, LDY, 4, ABSOLUTE));
        register(new Opcode(0xBC, LDY, 4, ABSOLUTE_X));

        // STA (Store Accumulator)
        register(new Opcode(0x85, STA, 3, ZERO_PAGE));
        register(new Opcode(0x95, STA, 4, ZERO_PAGE_X));
        register(new Opcode(0x8D, STA, 4, ABSOLUTE));
        register(new Opcode(0x9D, STA, 5, ABSOLUTE_X));
        register(new Opcode(0x99, STA, 5, ABSOLUTE_Y));
        register(new Opcode(0x81, STA, 6, INDIRECT_X));
        register(new Opcode(0x91, STA, 6, INDIRECT_Y));

        // STX (Store X)
        register(new Opcode(0x86, STX, 3, ZERO_PAGE));
        register(new Opcode(0x96, STX, 4, ZERO_PAGE_Y));
        register(new Opcode(0x8E, STX, 4, ABSOLUTE));

        // STY (Store Y)
        register(new Opcode(0x84, STY, 3, ZERO_PAGE));
        register(new Opcode(0x94, STY, 4, ZERO_PAGE_X));
        register(new Opcode(0x8C, STY, 4, ABSOLUTE));

        // ADC (Add with Carry)
        register(new Opcode(0x69, ADC, 2, IMMEDIATE));
        register(new Opcode(0x65, ADC, 3, ZERO_PAGE));
        register(new Opcode(0x75, ADC, 4, ZERO_PAGE_X));
        register(new Opcode(0x6D, ADC, 4, ABSOLUTE));
        register(new Opcode(0x7D, ADC, 4, ABSOLUTE_X));
        register(new Opcode(0x79, ADC, 4, ABSOLUTE_Y));
        register(new Opcode(0x61, ADC, 6, INDIRECT_X));
        register(new Opcode(0x71, ADC, 5, INDIRECT_Y));

        // SBC (Subtract with Carry)
        register(new Opcode(0xE9, SBC, 2, IMMEDIATE));
        register(new Opcode(0xE5, SBC, 3, ZERO_PAGE));
        register(new Opcode(0xF5, SBC, 4, ZERO_PAGE_X));
        register(new Opcode(0xED, SBC, 4, ABSOLUTE));
        register(new Opcode(0xFD, SBC, 4, ABSOLUTE_X));
        register(new Opcode(0xF9, SBC, 4, ABSOLUTE_Y));
        register(new Opcode(0xE1, SBC, 6, INDIRECT_X));
        register(new Opcode(0xF1, SBC, 5, INDIRECT_Y));

        // JMP
        register(new Opcode(0x4C, JMP, 3, ABSOLUTE));
        register(new Opcode(0x6C, JMP, 5, INDIRECT));

        // JSR
        register(new Opcode(0x20, JSR, 6, ABSOLUTE));

        // RTS
        register(new Opcode(0x60, RTS, 6, null));

        // RTI
        register(new Opcode(0x40, RTI, 6, null));

        // SEC, SED, SEI, CLC, CLD, CLI, CLV
        register(new Opcode(0x38, SEC, 2, null));
        register(new Opcode(0xF8, SED, 2, null));
        register(new Opcode(0x78, SEI, 2, null));
        register(new Opcode(0x18, CLC, 2, null));
        register(new Opcode(0xD8, CLD, 2, null));
        register(new Opcode(0x58, CLI, 2, null));
        register(new Opcode(0xB8, CLV, 2, null));

        // PHA, PHP, PLA, PLP
        register(new Opcode(0x48, PHA, 3, null));
        register(new Opcode(0x08, PHP, 3, null));
        register(new Opcode(0x68, PLA, 4, null));
        register(new Opcode(0x28, PLP, 4, null));

        // TAX, TAY, TSX, TXA, TXS, TYA
        register(new Opcode(0xAA, TAX, 2, null));
        register(new Opcode(0xA8, TAY, 2, null));
        register(new Opcode(0xBA, TSX, 2, null));
        register(new Opcode(0x8A, TXA, 2, null));
        register(new Opcode(0x9A, TXS, 2, null));
        register(new Opcode(0x98, TYA, 2, null));

        // INX, INY, DEX, DEY
        register(new Opcode(0xE8, INX, 2, null));
        register(new Opcode(0xC8, INY, 2, null));
        register(new Opcode(0xCA, DEX, 2, null));
        register(new Opcode(0x88, DEY, 2, null));

        // NOP
        register(new Opcode(0xEA, NOP, 2, null));
        // Illegal NOPs
        register(new Opcode(0x04, NOP, 3, ZERO_PAGE));
        register(new Opcode(0x44, NOP, 3, ZERO_PAGE));
        register(new Opcode(0x64, NOP, 3, ZERO_PAGE));
        register(new Opcode(0x0C, NOP, 4, ABSOLUTE));
        register(new Opcode(0x1C, NOP, 4, ABSOLUTE_X));
        register(new Opcode(0x3C, NOP, 4, ABSOLUTE_X));
        register(new Opcode(0x5C, NOP, 4, ABSOLUTE_X));
        register(new Opcode(0x7C, NOP, 4, ABSOLUTE_X));
        register(new Opcode(0xDC, NOP, 4, ABSOLUTE_X));
        register(new Opcode(0xFC, NOP, 4, ABSOLUTE_X));
        register(new Opcode(0x80, NOP, 2, IMMEDIATE));
        register(new Opcode(0x82, NOP, 2, IMMEDIATE));
        register(new Opcode(0x89, NOP, 2, IMMEDIATE));
        register(new Opcode(0xC2, NOP, 2, IMMEDIATE));
        register(new Opcode(0xE2, NOP, 2, IMMEDIATE));
        register(new Opcode(0x14, NOP, 4, ZERO_PAGE_X));
        register(new Opcode(0x34, NOP, 4, ZERO_PAGE_X));
        register(new Opcode(0x54, NOP, 4, ZERO_PAGE_X));
        register(new Opcode(0x74, NOP, 4, ZERO_PAGE_X));
        register(new Opcode(0xD4, NOP, 4, ZERO_PAGE_X));
        register(new Opcode(0xF4, NOP, 4, ZERO_PAGE_X));
        register(new Opcode(0x1A, NOP, 2, null));
        register(new Opcode(0x3A, NOP, 2, null));
        register(new Opcode(0x5A, NOP, 2, null));
        register(new Opcode(0x7A, NOP, 2, null));
        register(new Opcode(0xDA, NOP, 2, null));
        register(new Opcode(0xFA, NOP, 2, null));

        // AND
        register(new Opcode(0x29, AND, 2, IMMEDIATE));
        register(new Opcode(0x25, AND, 3, ZERO_PAGE));
        register(new Opcode(0x35, AND, 4, ZERO_PAGE_X));
        register(new Opcode(0x2D, AND, 4, ABSOLUTE));
        register(new Opcode(0x3D, AND, 4, ABSOLUTE_X));
        register(new Opcode(0x39, AND, 4, ABSOLUTE_Y));
        register(new Opcode(0x21, AND, 6, INDIRECT_X));
        register(new Opcode(0x31, AND, 5, INDIRECT_Y));

        // ORA
        register(new Opcode(0x09, ORA, 2, IMMEDIATE));
        register(new Opcode(0x05, ORA, 3, ZERO_PAGE));
        register(new Opcode(0x15, ORA, 4, ZERO_PAGE_X));
        register(new Opcode(0x0D, ORA, 4, ABSOLUTE));
        register(new Opcode(0x1D, ORA, 4, ABSOLUTE_X));
        register(new Opcode(0x19, ORA, 4, ABSOLUTE_Y));
        register(new Opcode(0x01, ORA, 6, INDIRECT_X));
        register(new Opcode(0x11, ORA, 5, INDIRECT_Y));

        // EOR
        register(new Opcode(0x49, EOR, 2, IMMEDIATE));
        register(new Opcode(0x45, EOR, 3, ZERO_PAGE));
        register(new Opcode(0x55, EOR, 4, ZERO_PAGE_X));
        register(new Opcode(0x4D, EOR, 4, ABSOLUTE));
        register(new Opcode(0x5D, EOR, 4, ABSOLUTE_X));
        register(new Opcode(0x59, EOR, 4, ABSOLUTE_Y));
        register(new Opcode(0x41, EOR, 6, INDIRECT_X));
        register(new Opcode(0x51, EOR, 5, INDIRECT_Y));

        // CMP
        register(new Opcode(0xC9, CMP, 2, IMMEDIATE));
        register(new Opcode(0xC5, CMP, 3, ZERO_PAGE));
        register(new Opcode(0xD5, CMP, 4, ZERO_PAGE_X));
        register(new Opcode(0xCD, CMP, 4, ABSOLUTE));
        register(new Opcode(0xDD, CMP, 4, ABSOLUTE_X));
        register(new Opcode(0xD9, CMP, 4, ABSOLUTE_Y));
        register(new Opcode(0xC1, CMP, 6, INDIRECT_X));
        register(new Opcode(0xD1, CMP, 5, INDIRECT_Y));

        // CPX
        register(new Opcode(0xE0, CPX, 2, IMMEDIATE));
        register(new Opcode(0xE4, CPX, 3, ZERO_PAGE));
        register(new Opcode(0xEC, CPX, 4, ABSOLUTE));

        // CPY
        register(new Opcode(0xC0, CPY, 2, IMMEDIATE));
        register(new Opcode(0xC4, CPY, 3, ZERO_PAGE));
        register(new Opcode(0xCC, CPY, 4, ABSOLUTE));

        // ASL
        register(new Opcode(0x0A, ASL, 2, null));
        register(new Opcode(0x06, ASL, 5, ZERO_PAGE));
        register(new Opcode(0x16, ASL, 6, ZERO_PAGE_X));
        register(new Opcode(0x0E, ASL, 6, ABSOLUTE));
        register(new Opcode(0x1E, ASL, 7, ABSOLUTE_X));

        // LSR
        register(new Opcode(0x4A, LSR, 2, null));
        register(new Opcode(0x46, LSR, 5, ZERO_PAGE));
        register(new Opcode(0x56, LSR, 6, ZERO_PAGE_X));
        register(new Opcode(0x4E, LSR, 6, ABSOLUTE));
        register(new Opcode(0x5E, LSR, 7, ABSOLUTE_X));

        // ROL
        register(new Opcode(0x2A, ROL, 2, null));
        register(new Opcode(0x26, ROL, 5, ZERO_PAGE));
        register(new Opcode(0x36, ROL, 6, ZERO_PAGE_X));
        register(new Opcode(0x2E, ROL, 6, ABSOLUTE));
        register(new Opcode(0x3E, ROL, 7, ABSOLUTE_X));

        // ROR
        register(new Opcode(0x6A, ROR, 2, null));
        register(new Opcode(0x66, ROR, 5, ZERO_PAGE));
        register(new Opcode(0x76, ROR, 6, ZERO_PAGE_X));
        register(new Opcode(0x6E, ROR, 6, ABSOLUTE));
        register(new Opcode(0x7E, ROR, 7, ABSOLUTE_X));

        // INC
        register(new Opcode(0xE6, INC, 5, ZERO_PAGE));
        register(new Opcode(0xF6, INC, 6, ZERO_PAGE_X));
        register(new Opcode(0xEE, INC, 6, ABSOLUTE));
        register(new Opcode(0xFE, INC, 7, ABSOLUTE_X));

        // DEC
        register(new Opcode(0xC6, DEC, 5, ZERO_PAGE));
        register(new Opcode(0xD6, DEC, 6, ZERO_PAGE_X));
        register(new Opcode(0xCE, DEC, 6, ABSOLUTE));
        register(new Opcode(0xDE, DEC, 7, ABSOLUTE_X));
        
        // LAX (Illegal)
        register(new Opcode(0xA7, LAX, 3, ZERO_PAGE));
        register(new Opcode(0xB7, LAX, 4, ZERO_PAGE_Y));
        register(new Opcode(0xAF, LAX, 4, ABSOLUTE));
        register(new Opcode(0xBF, LAX, 4, ABSOLUTE_Y));
        register(new Opcode(0xA3, LAX, 6, INDIRECT_X));
        register(new Opcode(0xB3, LAX, 5, INDIRECT_Y));

        // SAX (Illegal)
        register(new Opcode(0x87, SAX, 3, ZERO_PAGE));
        register(new Opcode(0x97, SAX, 4, ZERO_PAGE_Y));
        register(new Opcode(0x8F, SAX, 4, ABSOLUTE));
        register(new Opcode(0x83, SAX, 6, INDIRECT_X));

        // SBC (Illegal/Duplicate)
        register(new Opcode(0xEB, SBC, 2, IMMEDIATE));

        // DCP (Illegal)
        register(new Opcode(0xC7, DCP, 5, ZERO_PAGE));
        register(new Opcode(0xD7, DCP, 6, ZERO_PAGE_X));
        register(new Opcode(0xCF, DCP, 6, ABSOLUTE));
        register(new Opcode(0xDF, DCP, 7, ABSOLUTE_X));
        register(new Opcode(0xDB, DCP, 7, ABSOLUTE_Y));
        register(new Opcode(0xC3, DCP, 8, INDIRECT_X));
        register(new Opcode(0xD3, DCP, 8, INDIRECT_Y));

        // ISB (Illegal)
        register(new Opcode(0xE7, ISB, 5, ZERO_PAGE));
        register(new Opcode(0xF7, ISB, 6, ZERO_PAGE_X));
        register(new Opcode(0xEF, ISB, 6, ABSOLUTE));
        register(new Opcode(0xFF, ISB, 7, ABSOLUTE_X));
        register(new Opcode(0xFB, ISB, 7, ABSOLUTE_Y));
        register(new Opcode(0xE3, ISB, 8, INDIRECT_X));
        register(new Opcode(0xF3, ISB, 8, INDIRECT_Y));

        // RLA (Illegal)
        register(new Opcode(0x27, RLA, 5, ZERO_PAGE));
        register(new Opcode(0x37, RLA, 6, ZERO_PAGE_X));
        register(new Opcode(0x2F, RLA, 6, ABSOLUTE));
        register(new Opcode(0x3F, RLA, 7, ABSOLUTE_X));
        register(new Opcode(0x3B, RLA, 7, ABSOLUTE_Y));
        register(new Opcode(0x23, RLA, 8, INDIRECT_X));
        register(new Opcode(0x33, RLA, 8, INDIRECT_Y));

        // RRA (Illegal)
        register(new Opcode(0x67, RRA, 5, ZERO_PAGE));
        register(new Opcode(0x77, RRA, 6, ZERO_PAGE_X));
        register(new Opcode(0x6F, RRA, 6, ABSOLUTE));
        register(new Opcode(0x7F, RRA, 7, ABSOLUTE_X));
        register(new Opcode(0x7B, RRA, 7, ABSOLUTE_Y));
        register(new Opcode(0x63, RRA, 8, INDIRECT_X));
        register(new Opcode(0x73, RRA, 8, INDIRECT_Y));

        // SLO (Illegal)
        register(new Opcode(0x07, SLO, 5, ZERO_PAGE));
        register(new Opcode(0x17, SLO, 6, ZERO_PAGE_X));
        register(new Opcode(0x0F, SLO, 6, ABSOLUTE));
        register(new Opcode(0x1F, SLO, 7, ABSOLUTE_X));
        register(new Opcode(0x1B, SLO, 7, ABSOLUTE_Y));
        register(new Opcode(0x03, SLO, 8, INDIRECT_X));
        register(new Opcode(0x13, SLO, 8, INDIRECT_Y));

        // SRE (Illegal)
        register(new Opcode(0x47, SRE, 5, ZERO_PAGE));
        register(new Opcode(0x57, SRE, 6, ZERO_PAGE_X));
        register(new Opcode(0x4F, SRE, 6, ABSOLUTE));
        register(new Opcode(0x5F, SRE, 7, ABSOLUTE_X));
        register(new Opcode(0x5B, SRE, 7, ABSOLUTE_Y));
        register(new Opcode(0x43, SRE, 8, INDIRECT_X));
        register(new Opcode(0x53, SRE, 8, INDIRECT_Y));
    }
}
