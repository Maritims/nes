package no.clueless.emulation.impl.cpu;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.function.*;
import no.clueless.emulation.util.ResolvedAddress;

public enum Opcode {
    // region ALU operations
    ADC(AddWithCarry.ADC),
    SBC(SubtractWithCarry.SBC),
    AND(BitwiseOperation.AND),
    ORA(BitwiseOperation.ORA),
    EOR(BitwiseOperation.EOR),
    BIT(new BitTest()),
    CMP(Compare.CMP),
    CPX(new Compare(Cpu6502::getX)),
    CPY(new Compare(Cpu6502::getY)),
    DCP(Decrement.DEC.andThen(Compare.CMP)),
    ISC(Increment.INC.andThen(SubtractWithCarry.SBC)),
    SLO(ArithmeticShiftLeft.ASL.andThen(BitwiseOperation.ORA)),
    SRE(LogicalShiftRight.LSR.andThen(BitwiseOperation.EOR)),
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
    LDA(LoadRegisterFromMemory.LDA),
    LDX(LoadRegisterFromMemory.LDX),
    LDY(LoadRegisterFromMemory.LDY),
    LAX(LoadRegisterFromMemory.LDA.andThen(LoadRegisterFromMemory.LDX, true)),
    SAX(new StoreRegisterInMemory(cpu -> (cpu.getAccumulator() & cpu.getX()) & 0xFF)),
    STA(new StoreRegisterInMemory(Cpu6502::getAccumulator)),
    STX(new StoreRegisterInMemory(Cpu6502::getX)),
    STY(new StoreRegisterInMemory(Cpu6502::getY)),
    TAX(new TransferRegister(Cpu6502::getAccumulator, Cpu6502::setX)),
    TAY(new TransferRegister(Cpu6502::getAccumulator, Cpu6502::setY)),
    TXA(new TransferRegister(Cpu6502::getX, Cpu6502::setAccumulator)),
    TYA(new TransferRegister(Cpu6502::getY, Cpu6502::setAccumulator)),
    INC(Increment.INC),
    DEC(Decrement.DEC),
    INX(new IncrementRegister(Cpu6502::getX, Cpu6502::setX)),
    DEX(new DecrementRegister(Cpu6502::getX, Cpu6502::setX)),
    INY(new IncrementRegister(Cpu6502::getY, Cpu6502::setY)),
    DEY(new DecrementRegister(Cpu6502::getY, Cpu6502::setY)),
    NOP(new NoOperation()),
    // endregion
    // region Shift operations
    ASL(ArithmeticShiftLeft.ASL),
    LSR(LogicalShiftRight.LSR),
    ROL(Rotate.ROL),
    ROR(Rotate.ROR),
    RLA(Rotate.ROL.andThen(BitwiseOperation.AND)),
    RRA(Rotate.ROR.andThen(AddWithCarry.ADC)),
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
    public int resolve(Cpu6502 cpu, ResolvedAddress address) {
        return function.execute(cpu, address);
    }
}