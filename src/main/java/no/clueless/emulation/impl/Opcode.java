package no.clueless.emulation.impl;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.function.*;

public enum Opcode {
    // region ALU operations
    ADC(new AddWithCarry()),
    SBC(new SubtractWithCarry()),
    AND(new BitwiseAND()),
    ORA(new BitwiseOR()),
    EOR(new BitwiseExclusiveOR()),
    BIT(new BitTest()),
    CMP(new Compare(Cpu6502::getAccumulator)),
    CPX(new Compare(Cpu6502::getX)),
    CPY(new Compare(Cpu6502::getY)),
    DCP(new DecrementAndCompare()),
    ISB(new IncrementAndSubtract()),
    SLO(new ShiftLeftAndOr()),
    SRE(new ShiftRightAndEor()),
    // endregion
    // region Jump operations
    BRK(new Break()),
    JMP(new Jump()),
    JSR(new JumpToSubroutine()),
    RTI(new ReturnFromInterrupt()),
    RTS(new ReturnFromSubroutine()),
    // endregion
    // region Flag operations
    CLC(new ClearFlag(Cpu6502.Flag.CARRY)),
    CLD(new ClearFlag(Cpu6502.Flag.DECIMAL_MODE)),
    CLI(new ClearFlag(Cpu6502.Flag.INTERRUPT_DISABLE)),
    CLV(new ClearFlag(Cpu6502.Flag.OVERFLOW)),
    SEC(new SetFlag(Cpu6502.Flag.CARRY)),
    SED(new SetFlag(Cpu6502.Flag.DECIMAL_MODE)),
    SEI(new SetFlag(Cpu6502.Flag.INTERRUPT_DISABLE)),
    // endregion
    // region Memory operations
    LDA(new LoadRegisterFromMemory(Cpu6502::setAccumulator)),
    LDX(new LoadRegisterFromMemory(Cpu6502::setX)),
    LDY(new LoadRegisterFromMemory(Cpu6502::setY)),
    LAX(new LoadRegisterFromMemory((cpu, address) -> {
        var memoryData = cpu.read(address);
        cpu.setAccumulator(memoryData);
        cpu.setX(memoryData);
    })),
    SAX(new StoreRegisterInMemory(cpu -> (cpu.getAccumulator() & cpu.getX()) & 0xFF)),
    STA(new StoreRegisterInMemory(Cpu6502::getAccumulator)),
    STX(new StoreRegisterInMemory(Cpu6502::getX)),
    STY(new StoreRegisterInMemory(Cpu6502::getY)),
    TAX(new TransferRegister(Cpu6502::getAccumulator, Cpu6502::setX)),
    TAY(new TransferRegister(Cpu6502::getAccumulator, Cpu6502::setY)),
    TXA(new TransferRegister(Cpu6502::getX, Cpu6502::setAccumulator)),
    TYA(new TransferRegister(Cpu6502::getY, Cpu6502::setAccumulator)),
    INC(new Increment()),
    DEC(new Decrement()),
    INX(new IncrementRegister(Cpu6502::setX)),
    DEX(new DecrementRegister(Cpu6502::setX)),
    INY(new IncrementRegister(Cpu6502::setY)),
    DEY(new DecrementRegister(Cpu6502::setY)),
    NOP(new NoOperation()),
    // endregion
    // region Shift operations
    ASL(new ArithmeticShiftLeft()),
    LSR(new LogicalShiftRight()),
    ROL(new RotateLeft()),
    ROR(new RotateRight()),
    RLA(new RotateLeftAndBitwiseAND()),
    RRA(new RotateRightAndBitwiseAND()),
    // endregion
    // region Stack operations
    PHA(new PushAccumulator()),
    PHP(new PushProcessorStatus()),
    PLA(new PullAccumulator()),
    PLP(new PullProcessorStatus()),
    TSX(new TransferRegister(Cpu6502::getStackPointer, Cpu6502::setX)),
    TXS(new TransferXToStackPointer()),
    // endregion
    // region Branch operations
    BCC(new Branch(cpu6502 -> !cpu6502.hasFlag(Cpu6502.Flag.CARRY))),
    BCS(new Branch(cpu6502 -> cpu6502.hasFlag(Cpu6502.Flag.CARRY))),
    BEQ(new Branch(cpu6502 -> cpu6502.hasFlag(Cpu6502.Flag.ZERO))),
    BNE(new Branch(cpu6502 -> !cpu6502.hasFlag(Cpu6502.Flag.ZERO))),
    BMI(new Branch(cpu6502 -> cpu6502.hasFlag(Cpu6502.Flag.NEGATIVE))),
    BPL(new Branch(cpu6502 -> !cpu6502.hasFlag(Cpu6502.Flag.NEGATIVE))),
    BVC(new Branch(cpu6502 -> !cpu6502.hasFlag(Cpu6502.Flag.OVERFLOW))),
    BVS(new Branch(cpu6502 -> cpu6502.hasFlag(Cpu6502.Flag.OVERFLOW))),
    // endregion
    /**
     * Dummy opcode for bytes which do not represent an opcode.
     */
    XXX(null),
    JAM(null),
    ANC(null),
    ALR(null),
    ARR(null),
    ANE(null),
    SHA(null),
    TAS(null),
    SHY(null),
    SHX(null),
    LXA(null),
    LAS(null),
    SBX(null),
    ISC(null)
    ;

    private final OpcodeFunction function;

    Opcode(OpcodeFunction function) {
        this.function = function;
    }

    public OpcodeFunction getFunction() {
        return function;
    }

    /**
     * Resolves the opcode at the given address.
     *
     * @param cpu     The CPU.
     * @param address A 16-bit address.
     */
    public int resolve(Cpu6502 cpu, int address) {
        return function.execute(cpu, address);
    }
}