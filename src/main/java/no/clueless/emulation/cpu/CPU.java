package no.clueless.emulation.cpu;

import no.clueless.emulation.Bus;
import no.clueless.emulation.types.UnsignedWord;
import no.clueless.emulation.types.UnsignedByte;

import java.util.function.Consumer;

/**
 * Represents the CPU.
 */
public class CPU {
    public static int PC_ADDRESS_AT_POWER_ON    = 0xFFFC;
    public static int STACK_POINTER_AT_POWER_ON = 0xFD;

    /**
     * The bus; the communication channel with the outside world.
     */
    private final Bus  bus;
    private       long totalCycles = 0;

    private       StackPointer   stackPointer;
    private       UnsignedWord   programCounter;
    private final StatusRegister statusRegister;
    private       UnsignedByte   accumulator;
    private       UnsignedByte   x;
    private       UnsignedByte   y;

    /**
     * Constructor.
     *
     * @param bus            The bus to use.
     * @param accumulator    The accumulator to use.
     * @param x              The X register to use.
     * @param y              The Y register to use.
     * @param statusRegister The status register to use.
     * @throws IllegalArgumentException if bus, accumulator, x, y, or statusRegister is null.
     */
    CPU(Bus bus, UnsignedByte accumulator, UnsignedByte x, UnsignedByte y, StatusRegister statusRegister) {
        if (bus == null) {
            throw new IllegalArgumentException("bus cannot be null");
        }
        if (accumulator == null) {
            throw new IllegalArgumentException("accumulator cannot be null");
        }
        if (x == null) {
            throw new IllegalArgumentException("x cannot be null");
        }
        if (y == null) {
            throw new IllegalArgumentException("y cannot be null");
        }
        if (statusRegister == null) {
            throw new IllegalArgumentException("statusRegister cannot be null");
        }

        this.bus            = bus;
        this.programCounter = new UnsignedWord(0x0000);
        this.accumulator    = accumulator;
        this.x              = x;
        this.y              = y;
        this.statusRegister = statusRegister;

        this.reset();
    }

    /**
     * Default constructor.
     *
     * @param bus The bus to use.
     * @throws IllegalArgumentException if bus is null.
     */
    public CPU(Bus bus) {
        this(bus, UnsignedByte.ZERO, UnsignedByte.ZERO, UnsignedByte.ZERO, new StatusRegister());
    }

    public Bus getBus() {
        return bus;
    }

    public long getTotalCycles() {
        return totalCycles;
    }

    public void consumeCycles(long cycles) {
        this.totalCycles += cycles;
    }

    public StackPointer getStackPointer() {
        return stackPointer;
    }

    public UnsignedWord getProgramCounter() {
        return programCounter;
    }

    public UnsignedWord getAndIncrementProgramCounter() {
        var pc = programCounter;
        programCounter = programCounter.increment();
        return pc;
    }

    public StatusRegister getStatusRegister() {
        return statusRegister;
    }

    public UnsignedByte getAccumulator() {
        return accumulator;
    }

    public UnsignedByte getX() {
        return x;
    }

    public UnsignedByte getY() {
        return y;
    }

    public void setProgramCounter(UnsignedWord pc) {
        this.programCounter = pc;
    }

    public void setAccumulator(UnsignedByte accumulator) {
        this.accumulator = accumulator;
    }

    public void setX(UnsignedByte x) {
        this.x = x;
    }

    public void setY(UnsignedByte y) {
        this.y = y;
    }

    /**
     * Reboots the system and resets the CPU to its power-on state.
     */
    public void reset() {
        var lowByte  = bus.read(0xFFFC);
        var highByte = bus.read(0xFFFD);

        this.programCounter = UnsignedWord.fromInts(lowByte, highByte);
        this.stackPointer   = new StackPointer(new UnsignedByte(0xFD), 0x0100);

        this.statusRegister.clearAllFlags();
        this.statusRegister.setFlag(Flag.InterruptDisable, Flag.Five);
    }

    /**
     * Pushes an 8-bit value onto the stack.
     *
     * @param value The value to push.
     * @throws IllegalArgumentException if value is null.
     */
    public void push8(UnsignedByte value) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }

        bus.write(stackPointer.toAddress().intValue(), value.intValue());
        stackPointer.decrement();
    }

    /**
     * Pulls an 8-bit value from the stack.
     *
     * @return The value pulled from the stack.
     */
    public UnsignedByte pull8() {
        stackPointer.increment();
        var intValue = bus.read(stackPointer.toAddress().intValue());
        return new UnsignedByte(intValue);
    }

    /**
     * Pushes a 16-bit value onto the stack.
     *
     * @param value The value to push.
     * @throws IllegalArgumentException if value is null.
     */
    public void push16(UnsignedWord value) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }

        push8(value.highByte());
        push8(value.lowByte());
    }

    /**
     * Pulls a 16-bit value from the stack.
     *
     * @return The value pulled from the stack.
     */
    public UnsignedWord pull16() {
        var lowByte  = pull8();
        var highByte = pull8();
        return UnsignedWord.fromBytes(lowByte, highByte);
    }

    /**
     * Updates a register with a new value and updates the status register.
     *
     * @param value    The new value.
     * @param register A consumer that updates a register.
     */
    public void transfer(UnsignedByte value, Consumer<UnsignedByte> register) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        if (register == null) {
            throw new IllegalArgumentException("registerFactory cannot be null");
        }

        this.statusRegister.updateNegativeAndZero(value);
        register.accept(value);
    }

    public void step() {
        var rawOpcode = bus.read(programCounter.intValue());
        programCounter = programCounter.increment();

        var opcode = OpcodeRegistry.get(rawOpcode);
        if (opcode == null) {
            throw new IllegalStateException("Unknown opcodeFunction: 0x%02X".formatted(rawOpcode));
        }

        this.consumeCycles(opcode.cycles());

        var address = opcode.addressingMode() == null ? null : opcode.addressingMode().resolve(this, bus).address();
        opcode.mnemonic().execute(this, address == null ? null : new UnsignedWord(address));
    }
}
