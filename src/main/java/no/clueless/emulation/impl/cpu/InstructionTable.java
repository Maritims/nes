package no.clueless.emulation.impl.cpu;

import static no.clueless.emulation.impl.AddressingModes.*;
import static no.clueless.emulation.impl.cpu.Instruction.i;
import static no.clueless.emulation.impl.cpu.Opcode.*;

public class InstructionTable {
    /**
     * The layout of this table represents the 6502 instruction set, including all its illegal opcodes. A side effect of this layout is that we can access the table directly using the opcode read from the CPU.
     * <p>
     *     Source: <a href="https://www.masswerk.at/6502/6502_instruction_set.html">https://www.masswerk.at/6502/6502_instruction_set.html</a>
     * </p>
     * <p>The <code>cycles</code> argument per instruction is the number of CPU cycles it takes to execute the instruction given the corresponding addressing mode.</p>
     * <p>It is the responsibility of the implementation of {@link OpcodeFunction} to resolve any dynamicallly added cycle, like in the case of crossing a page.</p>
     * <p>Source: <a href="https://www.nesdev.org/wiki/Instruction_reference">https://www.nesdev.org/wiki/Instruction_reference</a></p>
     */
    public static final Instruction[] INSTRUCTIONS = new Instruction[]{
            // $0x
            i(BRK, IMP, 7), i(ORA, IZX, 6), i(JAM, IMP, 2), i(SLO, IZX, 8), i(NOP, ZP0, 3), i(ORA, ZP0, 3), i(ASL, ZP0, 5), i(SLO, ZP0, 5), i(PHP, IMP, 3), i(ORA, IMM, 2), i(ASL, IMP, 2), i(ANC, IMM, 2), i(NOP, ABS, 4), i(ORA, ABS, 4), i(ASL, ABS, 6), i(SLO, ABS, 6),
            i(BPL, REL, 2), i(ORA, IZY, 5), i(JAM, IMP, 2), i(SLO, IZY, 8), i(NOP, ZPX, 4), i(ORA, ZPX, 4), i(ASL, ZPX, 6), i(SLO, ZPX, 6), i(CLC, IMP, 2), i(ORA, ABY, 4), i(NOP, IMP, 2), i(SLO, ABY, 7), i(NOP, ABX, 4), i(ORA, ABX, 4), i(ASL, ABX, 7), i(SLO, ABX, 7),
            i(JSR, ABS, 6), i(AND, IZX, 6), i(JAM, IMP, 2), i(RLA, IZX, 8), i(BIT, ZP0, 3), i(AND, ZP0, 3), i(ROL, ZP0, 5), i(RLA, ZP0, 5), i(PLP, IMP, 4), i(AND, IMM, 2), i(ROL, IMP, 2), i(ANC, IMM, 2), i(BIT, ABS, 4), i(AND, ABS, 4), i(ROL, ABS, 6), i(RLA, ABS, 6),
            i(BMI, REL, 2), i(AND, IZY, 5), i(JAM, IMP, 2), i(RLA, IZY, 8), i(NOP, ZPX, 4), i(AND, ZPX, 4), i(ROL, ZPX, 6), i(RLA, ZPX, 6), i(SEC, IMP, 2), i(AND, ABY, 4), i(NOP, IMP, 2), i(RLA, ABY, 7), i(NOP, ABX, 4), i(AND, ABX, 4), i(ROL, ABX, 7), i(RLA, ABX, 7),
            i(RTI, IMP, 6), i(EOR, IZX, 6), i(JAM, IMP, 2), i(SRE, IZX, 8), i(NOP, ZP0, 3), i(EOR, ZP0, 3), i(LSR, ZP0, 5), i(SRE, ZP0, 5), i(PHA, IMP, 3), i(EOR, IMM, 2), i(LSR, IMP, 2), i(ALR, IMM, 2), i(JMP, ABS, 3), i(EOR, ABS, 4), i(LSR, ABS, 6), i(SRE, ABS, 6),
            i(BVC, REL, 2), i(EOR, IZY, 5), i(JAM, IMP, 2), i(SRE, IZY, 8), i(NOP, ZPX, 4), i(EOR, ZPX, 4), i(LSR, ZPX, 6), i(SRE, ZPX, 6), i(CLI, IMP, 2), i(EOR, ABY, 4), i(NOP, IMP, 2), i(SRE, ABY, 7), i(NOP, ABX, 4), i(EOR, ABX, 4), i(LSR, ABX, 7), i(SRE, ABX, 7),
            i(RTS, IMP, 6), i(ADC, IZX, 6), i(JAM, IMP, 2), i(RRA, IZX, 8), i(NOP, ZP0, 3), i(ADC, ZP0, 3), i(ROR, ZP0, 5), i(RRA, ZP0, 5), i(PLA, IMP, 4), i(ADC, IMM, 2), i(ROR, IMP, 2), i(ARR, IMM, 2), i(JMP, IND, 5), i(ADC, ABS, 4), i(ROR, ABS, 6), i(RRA, ABS, 6),
            i(BVS, REL, 2), i(ADC, IZY, 5), i(JAM, IMP, 2), i(RRA, IZY, 8), i(NOP, ZPX, 4), i(ADC, ZPX, 4), i(ROR, ZPX, 6), i(RRA, ZPX, 6), i(SEI, IMP, 2), i(ADC, ABY, 4), i(NOP, IMP, 2), i(RRA, ABY, 7), i(NOP, ABX, 4), i(ADC, ABX, 4), i(ROR, ABX, 7), i(RRA, ABX, 7),
            i(NOP, IMM, 2), i(STA, IZX, 6), i(NOP, IMM, 2), i(SAX, IZX, 6), i(STY, ZP0, 3), i(STA, ZP0, 3), i(STX, ZP0, 3), i(SAX, ZP0, 3), i(DEY, IMP, 2), i(NOP, IMM, 2), i(TXA, IMP, 2), i(ANE, IMM, 2), i(STY, ABS, 4), i(STA, ABS, 4), i(STX, ABS, 4), i(SAX, ABS, 4),
            i(BCC, REL, 2), i(STA, IZY, 6), i(JAM, IMP, 2), i(SHA, IZY, 6), i(STY, ZPX, 4), i(STA, ZPX, 4), i(STX, ZPY, 4), i(SAX, ZPY, 4), i(TYA, IMP, 2), i(STA, ABY, 5), i(TXS, IMP, 2), i(TAS, ABY, 5), i(SHY, ABX, 5), i(STA, ABX, 5), i(SHX, ABY, 5), i(SHA, ABY, 5),
            i(LDY, IMM, 2), i(LDA, IZX, 6), i(LDX, IMM, 2), i(LAX, IZX, 6), i(LDY, ZP0, 3), i(LDA, ZP0, 3), i(LDX, ZP0, 3), i(LAX, ZP0, 3), i(TAY, IMP, 2), i(LDA, IMM, 2), i(TAX, IMP, 2), i(LXA, IMM, 2), i(LDY, ABS, 4), i(LDA, ABS, 4), i(LDX, ABS, 4), i(LAX, ABS, 4),
            i(BCS, REL, 2), i(LDA, IZY, 5), i(JAM, IMP, 2), i(LAX, IZY, 5), i(LDY, ZPX, 4), i(LDA, ZPX, 4), i(LDX, ZPY, 4), i(LAX, ZPY, 4), i(CLV, IMP, 2), i(LDA, ABY, 4), i(TSX, IMP, 2), i(LAS, ABY, 4), i(LDY, ABX, 4), i(LDA, ABX, 4), i(LDX, ABY, 4), i(LAX, ABY, 4),
            i(CPY, IMM, 2), i(CMP, IZX, 6), i(NOP, IMM, 2), i(DCP, IZX, 8), i(CPY, ZP0, 3), i(CMP, ZP0, 3), i(DEC, ZP0, 5), i(DCP, ZP0, 5), i(INY, IMP, 2), i(CMP, IMM, 2), i(DEX, IMP, 2), i(SBX, IMM, 2), i(CPY, ABS, 4), i(CMP, ABS, 4), i(DEC, ABS, 6), i(DCP, ABS, 6),
            i(BNE, REL, 2), i(CMP, IZY, 5), i(JAM, IMP, 2), i(DCP, IZY, 8), i(NOP, ZPX, 4), i(CMP, ZPX, 4), i(DEC, ZPX, 6), i(DCP, ZPX, 6), i(CLD, IMP, 2), i(CMP, ABY, 4), i(NOP, IMP, 2), i(DCP, ABY, 7), i(NOP, ABX, 4), i(CMP, ABX, 4), i(DEC, ABX, 7), i(DCP, ABX, 7),
            i(CPX, IMM, 2), i(SBC, IZX, 6), i(NOP, IMM, 2), i(ISC, IZX, 8), i(CPX, ZP0, 3), i(SBC, ZP0, 3), i(INC, ZP0, 5), i(ISC, ZP0, 5), i(INX, IMP, 2), i(SBC, IMM, 2), i(NOP, IMP, 2), i(SBC, IMM, 2), i(CPX, ABS, 4), i(SBC, ABS, 4), i(INC, ABS, 6), i(ISC, ABS, 6),
            i(BEQ, REL, 2), i(SBC, IZY, 5), i(JAM, IMP, 2), i(ISC, IZY, 8), i(NOP, ZPX, 4), i(SBC, ZPX, 4), i(INC, ZPX, 6), i(ISC, ZPX, 6), i(SED, IMP, 2), i(SBC, ABY, 4), i(NOP, IMP, 2), i(ISC, ABY, 7), i(NOP, ABX, 4), i(SBC, ABX, 4), i(INC, ABX, 7), i(ISC, ABX, 7)
    };
}
