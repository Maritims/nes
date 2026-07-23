package no.clueless.emulation.cpu;

import no.clueless.emulation.cpu.operations.*;
import no.clueless.emulation.types.UnsignedWord;

public enum Mnemonic {
    // region ALU operations
    ADC("add with carry", AluOperations::adc),
    SBC("subtract with carry", AluOperations::sbc),
    AND("and (with accumulator)", AluOperations::and),
    ORA("or with accumulator", AluOperations::ora),
    EOR("exclusive or (with accumulator)", AluOperations::eor),
    BIT("bit test", AluOperations::bit),
    CMP("compare (with accumulator)", AluOperations::cmp),
    CPX("compare with X", AluOperations::cpx),
    CPY("compare with Y", AluOperations::cpy),
    DCP("decrement and compare", AluOperations::dcp),
    ISB("increment and subtract", AluOperations::isb),
    SLO("shift left and or", AluOperations::slo),
    SRE("shift right and eor", AluOperations::sre),
    // endregion
    // region Jump operations
    BRK("break / interrupt", JumpOperations::brk),
    JMP("jump", JumpOperations::jmp),
    JSR("jump subroutine", JumpOperations::jsr),
    RTI("return from interrupt", JumpOperations::rti),
    RTS("return from subroutine", JumpOperations::rts),
    // endregion
    // region Flag operations
    CLC("clear carry", FlagOperations::clc),
    CLD("clear decimal", FlagOperations::cld),
    CLI("clear interrupt disable", FlagOperations::cli),
    CLV("clear overflow", FlagOperations::clv),
    SEC("set carry", FlagOperations::sec),
    SED("set decimal", FlagOperations::sed),
    SEI("set interrupt disable", FlagOperations::sei),
    // endregion
    // region Memory operations
    LDA("load accumulator", MemoryOperations::lda),
    LDX("load X", MemoryOperations::ldx),
    LDY("load Y", MemoryOperations::ldy),
    LAX("load accumulator and X", MemoryOperations::lax),
    SAX("store accumulator and X", MemoryOperations::sax),
    STA("store accumulator", MemoryOperations::sta),
    STX("store X", MemoryOperations::stx),
    STY("store Y", MemoryOperations::sty),
    TAX("transfer accumulator to X", MemoryOperations::tax),
    TAY("transfer accumulator to Y", MemoryOperations::tay),
    TXA("transfer X to accumulator", MemoryOperations::txa),
    TYA("transfer Y to accumulator", MemoryOperations::tya),
    INC("increment", MemoryOperations::inc),
    DEC("decrement", MemoryOperations::dec),
    INX("increment X", MemoryOperations::inx),
    DEX("decrement X", MemoryOperations::dex),
    INY("increment Y", MemoryOperations::iny),
    DEY("decrement Y", MemoryOperations::dey),
    NOP("no operation", MemoryOperations::nop),
    // endregion
    // region Shift operations
    ASL("arithmetic shift left", ShiftOperations::asl),
    LSR("logical shift right", ShiftOperations::lsr),
    ROL("rotate left", ShiftOperations::rol),
    ROR("rotate right", ShiftOperations::ror),
    RLA("rotate left and and", ShiftOperations::rla),
    RRA("rotate right and add", ShiftOperations::rra),
    // endregion
    // region Stack operations
    PHA("push accumulator", StackOperations::pha),
    PHP("push processor status (SR)", StackOperations::php),
    PLA("pull accumulator", StackOperations::pla),
    PLP("pull processor status (SR)", StackOperations::plp),
    TSX("transfer stack pointer to X", StackOperations::tsx),
    TXS("transfer X to stack pointer", StackOperations::txs),
    // endregion
    // region Branch operations
    BCC("branch on carry clear", BranchOperations::bcc),
    BCS("branch on carry set", BranchOperations::bcs),
    BEQ("branch on equal (zero set)", BranchOperations::beq),
    BNE("branch on not equal (zero clear)", BranchOperations::bne),
    BMI("branch on minus (negative set)", BranchOperations::bmi),
    BPL("branch on plus (negative clear)", BranchOperations::bpl),
    BVC("branch on overflow clear", BranchOperations::bvc),
    BVS("branch on overflow set", BranchOperations::bvs);
    // endregion

    private final String name;
    private final InstructionOperation instructionOperation;

    Mnemonic(String name, InstructionOperation instructionOperation) {
        this.name = name;
        this.instructionOperation = instructionOperation;
    }

    public String getName() {
        return name;
    }

    public void execute(CPU cpu, UnsignedWord address) {
        instructionOperation.execute(cpu, address);
    }
}