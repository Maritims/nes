package no.clueless.emulation.impl;

import static no.clueless.emulation.impl.AddressingModes.*;
import static no.clueless.emulation.impl.Instruction.i;
import static no.clueless.emulation.impl.Opcode.*;

public class InstructionTable {
    /**
     * The layout of this table represents the 6502 instruction set including all its illegal opcodes. A side effect of this layout is that we can access the table directly using the opcode read from the CPU.
     * <p>
     * Source: <a href="https://www.masswerk.at/6502/6502_instruction_set.html">https://www.masswerk.at/6502/6502_instruction_set.html</a>
     */
    public static final Instruction[] INSTRUCTIONS = new Instruction[]{
            i(BRK, IMP, 0), i(ORA, IZX, 5), i(JAM, IMP, 0), i(SLO, IZX, 0), i(NOP, ZP0, 0), i(ORA, ZP0, 1), i(ASL, ZP0, 0), i(SLO, ZP0, 0), i(PHP, IMP, 0), i(ORA, IMM, 0), i(ASL, IMP, 0), i(ANC, IMM, 0), i(NOP, ABS, 0), i(ORA, ABS, 0), i(ASL, ABS, 0), i(SLO, ABS, 0),
            i(BPL, REL, 0), i(ORA, IZY, 0), i(JAM, IMP, 0), i(SLO, IZY, 0), i(NOP, ZPX, 0), i(ORA, ZPX, 0), i(ASL, ZPX, 0), i(SLO, ZPX, 0), i(CLC, IMP, 0), i(ORA, ABY, 0), i(NOP, IMP, 0), i(SLO, ABY, 0), i(NOP, ABX, 0), i(ORA, ABX, 0), i(ASL, ABX, 0), i(SLO, ABX, 0),
            i(JSR, ABS, 0), i(AND, IZX, 0), i(JAM, IMP, 0), i(RLA, IZX, 0), i(BIT, ZP0, 1), i(AND, ZP0, 0), i(ROL, ZP0, 0), i(RLA, ZP0, 0), i(PLP, IMP, 0), i(AND, IMM, 0), i(ROL, IMP, 0), i(ANC, IMM, 0), i(BIT, ABS, 2), i(AND, ABS, 0), i(ROL, ABS, 0), i(RLA, ABS, 0),
            i(BMI, REL, 0), i(AND, IZY, 0), i(JAM, IMP, 0), i(RLA, IZY, 0), i(NOP, ZPX, 0), i(AND, ZPX, 0), i(ROL, ZPX, 0), i(RLA, ZPX, 0), i(SEC, IMP, 0), i(AND, ABY, 0), i(NOP, IMP, 0), i(RLA, ABY, 0), i(NOP, ABX, 0), i(AND, ABX, 0), i(ROL, ABX, 0), i(RLA, ABX, 0),
            i(RTI, IMP, 0), i(EOR, IZX, 0), i(JAM, IMP, 0), i(SRE, IZX, 0), i(NOP, ZP0, 0), i(EOR, ZP0, 0), i(LSR, ZP0, 0), i(SRE, ZP0, 0), i(PHA, IMP, 0), i(EOR, IMM, 0), i(LSR, IMP, 0), i(ALR, IMM, 0), i(JMP, ABS, 0), i(EOR, ABS, 0), i(LSR, ABS, 0), i(SRE, ABS, 0),
            i(BVC, REL, 0), i(EOR, IZY, 0), i(JAM, IMP, 0), i(SRE, IZY, 0), i(NOP, ZPX, 0), i(EOR, ZPX, 0), i(LSR, ZPX, 0), i(SRE, ZPX, 0), i(CLI, IMP, 0), i(EOR, ABY, 0), i(NOP, IMP, 0), i(SRE, ABY, 0), i(NOP, ABX, 0), i(EOR, ABX, 0), i(LSR, ABX, 0), i(SRE, ABX, 0),
            i(RTS, IMP, 0), i(ADC, IZX, 0), i(JAM, IMP, 0), i(RRA, IZX, 0), i(NOP, ZP0, 0), i(ADC, ZP0, 0), i(ROR, ZP0, 0), i(RRA, ZP0, 0), i(PLA, IMP, 0), i(ADC, IMM, 0), i(ROR, IMP, 0), i(ARR, IMM, 0), i(JMP, IND, 0), i(ADC, ABS, 0), i(ROR, ABS, 0), i(RRA, ABS, 0),
            i(BVS, REL, 0), i(ADC, IZY, 0), i(JAM, IMP, 0), i(RRA, IZY, 0), i(NOP, ZPX, 0), i(ADC, ZPX, 0), i(ROR, ZPX, 0), i(RRA, ZPX, 0), i(SEI, IMP, 0), i(ADC, ABY, 0), i(NOP, IMP, 0), i(RRA, ABY, 0), i(NOP, ABX, 0), i(ADC, ABX, 0), i(ROR, ABX, 0), i(RRA, ABX, 0),
            i(NOP, IMM, 0), i(STA, IZX, 0), i(NOP, IMM, 0), i(SAX, IZX, 0), i(STY, ZP0, 0), i(STA, ZP0, 0), i(STX, ZP0, 0), i(SAX, ZP0, 0), i(DEY, IMP, 0), i(NOP, IMM, 0), i(TXA, IMP, 0), i(ANE, IMM, 0), i(STY, ABS, 0), i(STA, ABS, 0), i(STX, ABS, 0), i(SAX, ABS, 0),
            i(BCC, REL, 0), i(STA, IZY, 0), i(JAM, IMP, 0), i(SHA, IZY, 0), i(STY, ZPX, 0), i(STA, ZPX, 0), i(STX, ZPY, 0), i(SAX, ZPY, 0), i(TYA, IMP, 0), i(STA, ABY, 0), i(TXS, IMP, 0), i(TAS, ABY, 0), i(SHY, ABX, 0), i(STA, ABX, 0), i(SHX, ABY, 0), i(SHA, ABY, 0),
            i(LDY, IMM, 0), i(LDA, IZX, 0), i(LDX, IMM, 0), i(LAX, IZX, 0), i(LDY, ZP0, 0), i(LDA, ZP0, 0), i(LDX, ZP0, 0), i(LAX, ZP0, 0), i(TAY, IMP, 0), i(LDA, IMM, 0), i(TAX, IMP, 0), i(LXA, IMM, 0), i(LDY, ABS, 0), i(LDA, ABS, 0), i(LDX, ABS, 0), i(LAX, ABS, 0),
            i(BCS, REL, 0), i(LDA, IZY, 0), i(JAM, IMP, 0), i(LAX, IZY, 0), i(LDY, ZPX, 0), i(LDA, ZPX, 0), i(LDX, ZPY, 0), i(LAX, ZPY, 0), i(CLV, IMP, 0), i(LDA, ABY, 0), i(TSX, IMP, 0), i(LAS, ABY, 0), i(LDY, ABX, 0), i(LDA, ABX, 0), i(LDX, ABY, 0), i(LAX, ABY, 0),
            i(CPY, IMM, 0), i(CMP, IZX, 4), i(NOP, IMM, 0), i(DCP, IZX, 0), i(CPY, ZP0, 0), i(CMP, ZP0, 1), i(DEC, ZP0, 0), i(DCP, ZP0, 0), i(INY, IMP, 0), i(CMP, IMM, 0), i(DEX, IMP, 0), i(SBX, IMM, 0), i(CPY, ABS, 0), i(CMP, ABS, 2), i(DEC, ABS, 0), i(DCP, ABS, 0),
            i(BNE, REL, 0), i(CMP, IZY, 5), i(JAM, IMP, 0), i(DCP, IZY, 0), i(NOP, ZPX, 0), i(CMP, ZPX, 2), i(DEC, ZPX, 0), i(DCP, ZPX, 0), i(CLD, IMP, 0), i(CMP, ABY, 0), i(NOP, IMP, 0), i(DCP, ABY, 0), i(NOP, ABX, 0), i(CMP, ABX, 0), i(DEC, ABX, 0), i(DCP, ABX, 0),
            i(CPX, IMM, 0), i(SBC, IZX, 0), i(NOP, IMM, 0), i(ISC, IZX, 0), i(CPX, ZP0, 0), i(SBC, ZP0, 0), i(INC, ZP0, 0), i(ISC, ZP0, 0), i(INX, IMP, 0), i(SBC, IMM, 0), i(NOP, IMP, 0), i(SBC, IMM, 0), i(CPX, ABS, 0), i(SBC, ABS, 0), i(INC, ABS, 0), i(ISC, ABS, 0),
            i(BEQ, REL, 0), i(SBC, IZY, 0), i(JAM, IMP, 0), i(ISC, IZY, 0), i(NOP, ZPX, 0), i(SBC, ZPX, 0), i(INC, ZPX, 0), i(ISC, ZPX, 0), i(SED, IMP, 0), i(SBC, ABY, 0), i(NOP, IMP, 0), i(ISC, ABY, 0), i(NOP, ABX, 0), i(SBC, ABX, 0), i(INC, ABX, 0), i(ISC, ABX, 0)
    };
}
