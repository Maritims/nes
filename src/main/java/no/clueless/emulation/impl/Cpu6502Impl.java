package no.clueless.emulation.impl;

import no.clueless.emulation.Bus;
import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.cpu.Opcode;

import static no.clueless.emulation.cpu.CPU.PC_ADDRESS_AT_POWER_ON;
import static no.clueless.emulation.cpu.CPU.STACK_POINTER_AT_POWER_ON;

public class Cpu6502Impl implements Cpu6502 {
    private final int MASK_8BIT  = 0xFF;
    private final int MASK_16BIT = 0xFFFF;

    private int a      = 0x00;
    private int x      = 0x00;
    private int y      = 0x00;
    private int sp     = 0xFF;
    private int pc     = 0x0000;
    private int status = 0x00;

    private Bus    bus;
    private int    cycles = 0;
    private Opcode opcode;

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
    public int getY() {
        return y & MASK_8BIT;
    }

    @Override
    public int getStackPointer() {
        return sp & MASK_8BIT;
    }

    @Override
    public void pushToStack(int... values) {
        for (var value : values) {
            write(0x0100 + sp--, value);
        }
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
        return status & MASK_8BIT;
    }

    @Override
    public void connectToBus(Bus bus) {
        this.bus = bus;
    }

    @Override
    public void addCycles(int cycles) {
        this.cycles += cycles;
    }

    @Override
    public void clock() {
        var opcode = read(pc++);

        // Always set the unused flag.
        setFlag(Flag.UNUSED, true);

        var instruction = InstructionTable.getInstruction(opcode);
        this.cycles = instruction.cycles();

        // Always set the unused flag.
        setFlag(Flag.UNUSED, true);
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

        // A reset consumes 8 clock cycles.
        cycles = 8;
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
