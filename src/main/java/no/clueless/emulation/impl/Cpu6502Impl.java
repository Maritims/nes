package no.clueless.emulation.impl;

import no.clueless.emulation.Bus;
import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.util.Disassembler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cpu6502Impl implements Cpu6502 {
    private static final Logger log        = LoggerFactory.getLogger(Cpu6502Impl.class);
    private final        int    MASK_8BIT  = 0xFF;
    private final        int    MASK_16BIT = 0xFFFF;

    private final boolean isDecimalModeEnabled;
    private       int     clockCount = 0;
    private       int     a          = 0x00;
    private       int     x          = 0x00;
    private       int     y          = 0x00;
    private       int     sp         = 0xFF;
    private       int     pc         = 0x0000;
    private       int     status     = 0x00;

    private Bus bus;
    private int cycles = 0;

    public Cpu6502Impl(boolean isDecimalModeEnabled) {
        this.isDecimalModeEnabled = isDecimalModeEnabled;
    }

    public void setFlag(Flag flag, boolean value) {
        if (value) {
            status |= flag.getValue();
        } else {
            status &= ~flag.getValue();
        }
    }

    @Override
    public boolean hasFlag(Flag flag) {
        return (status & flag.getValue()) != 0;
    }

    @Override
    public boolean isDecimalModeEnabled() {
        return isDecimalModeEnabled;
    }

    @Override
    public boolean isInstructionComplete() {
        return cycles == 0;
    }

    @Override
    public int getCycles() {
        return cycles;
    }

    @Override
    public int getClockCount() {
        return clockCount;
    }

    @Override
    public int getAccumulator() {
        return a & MASK_8BIT;
    }

    public void setAccumulator(int value) {
        a = value & MASK_8BIT;
    }

    @Override
    public int getX() {
        return x & MASK_8BIT;
    }

    @Override
    public void setX(int value) {
        x = value & MASK_8BIT;
    }

    @Override
    public int getY() {
        return y & MASK_8BIT;
    }

    @Override
    public void setY(int value) {
        y = value & MASK_8BIT;
    }

    @Override
    public int getStackPointer() {
        return sp & MASK_8BIT;
    }

    @Override
    public int getAndIncrementStackPointer() {
        return sp++;
    }

    @Override
    public int pullFromStack() {
        sp = (sp + 1) & MASK_8BIT;
        return read(0x0100 | sp);
    }

    @Override
    public void pushToStack(int value) {
        write(0x0100 | sp, value);
        sp = (sp - 1) & MASK_8BIT;
    }

    @Override
    public void setStackPointer(int value) {
        sp = value & MASK_8BIT;
    }

    @Override
    public int getProgramCounter() {
        return pc & MASK_16BIT;
    }

    @Override
    public int getAndIncrementProgramCounter() {
        var pc = this.pc & MASK_16BIT;
        this.pc = pc + 1;
        return pc;
    }

    @Override
    public void setProgramCounter(int value) {
        this.pc = value & MASK_16BIT;
    }

    @Override
    public int getStatusRegister() {
        return (status | Cpu6502.Flag.UNUSED.getValue()) & MASK_8BIT;
    }

    @Override
    public void setStatusRegister(int value) {
        this.status = (value | Flag.UNUSED.getValue()) & ~Flag.BREAK.getValue() & MASK_8BIT;
    }

    @Override
    public void connectToBus(Bus bus) {
        this.bus = bus;
    }

    @Override
    public void clock() {
        // Is the CPU available for work?
        if (isInstructionComplete()) {
            var originalPc = pc;
            var opcode     = read(pc);
            pc++;

            // Always set the unused flag.
            setFlag(Flag.UNUSED, true);

            var instruction = InstructionTable.INSTRUCTIONS[opcode];
            if (instruction.cycles() == 0) {
                throw new IllegalStateException("Instruction " + instruction.opcode() + " has no cycles defined when using addressing mode " + instruction.addressingMode());
            }

            this.cycles = instruction.cycles();

            var resolvedAddress = instruction.addressingMode().resolve(this, bus);

            //log.info("{}", Disassembler.disassemble(originalPc, instruction.opcode(), instruction.addressingMode(), resolvedAddress.address()));

            // Add any additional cycles from the instruction itself.
            var opcodeCycles = instruction.opcode().resolve(this, resolvedAddress);
            if (opcodeCycles > 0) {
                //log.info("Opcode {} added {} extra cycles", instruction.opcode(), opcodeCycles);
                this.cycles += opcodeCycles;
            }

            // Always set the unused flag.
            setFlag(Flag.UNUSED, true);
        }

        clockCount++;
        cycles--;
    }

    @Override
    public void reset() {
        var lowByte  = read(PC_ADDRESS_AT_POWER_ON);
        var highByte = read(PC_ADDRESS_AT_POWER_ON + 1);

        pc = (highByte << 8) | (lowByte & 0xFF);

        a  = 0;
        x  = 0;
        y  = 0;
        sp = STACK_POINTER_AT_POWER_ON;

        // Always set the unused flag.
        status = Flag.INTERRUPT_DISABLE.getValue() | Flag.UNUSED.getValue();

        // A reset consumes 7 clock cycles.
        clockCount = 7;
    }

    @Override
    public int read(int address) {
        address &= MASK_16BIT;
        return bus.read(address);
    }

    @Override
    public void write(int address, int data) {
        address &= MASK_16BIT;
        data &= MASK_8BIT;
        bus.write(address, data);
    }
}
