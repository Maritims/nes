package no.clueless.emulation.cpu;

import no.clueless.emulation.Bus;
import no.clueless.emulation.types.UnsignedWord;
import no.clueless.emulation.types.UnsignedByte;

import java.util.function.Consumer;

/**
 * Represents the CPU.
 */
public class CPU {
    /**
     * The bus; the communication channel with the outside world.
     */
    private final Bus     bus;
    private       long    totalCycles          = 0;
    private       boolean decimalModeSupported = true;

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

    public void setProgramCounter(UnsignedWord pc) {
        this.programCounter = pc;
    }

    public StatusRegister getStatusRegister() {
        return statusRegister;
    }

    public UnsignedByte getAccumulator() {
        return accumulator;
    }

    public void setAccumulator(UnsignedByte accumulator) {
        this.accumulator = accumulator;
    }

    public UnsignedByte getX() {
        return x;
    }

    public void setX(UnsignedByte x) {
        this.x = x;
    }

    public UnsignedByte getY() {
        return y;
    }

    public void setY(UnsignedByte y) {
        this.y = y;
    }

    public void setDecimalModeSupported(boolean supported) {
        this.decimalModeSupported = supported;
    }

    /**
     * Reboots the system and resets the CPU to its power-on state.
     */
    public void reset() {
        var lowByte  = bus.read(new UnsignedWord(0xFFFC));
        var highByte = bus.read(new UnsignedWord(0xFFFD));

        this.programCounter = UnsignedWord.fromBytes(lowByte, highByte);
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

        bus.write(stackPointer.toAddress(), value);
        stackPointer.decrement();
    }

    /**
     * Pulls an 8-bit value from the stack.
     *
     * @return The value pulled from the stack.
     */
    public UnsignedByte pull8() {
        stackPointer.increment();
        return bus.read(stackPointer.toAddress());
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

    public void compare(UnsignedByte registerValue, UnsignedByte memoryValue) {
        if (registerValue == null || memoryValue == null) {
            throw new IllegalArgumentException("values cannot be null");
        }
        var hasCarry = registerValue.compareTo(memoryValue) >= 0;
        var tmp      = registerValue.subtract(memoryValue);

        this.statusRegister.updateFlag(Flag.Carry, hasCarry);
        this.statusRegister.updateNegativeAndZero(tmp);
    }

    private void performArithmeticAddition(UnsignedByte memoryData) {
        if (memoryData == null) {
            throw new IllegalArgumentException("memoryData cannot be null");
        }

        var carryIn = statusRegister.hasFlag(Flag.Carry) ? 1 : 0;

        if (decimalModeSupported && statusRegister.hasFlag(Flag.Decimal)) {
            // Decimal mode addition
            int low = accumulator.and(new UnsignedByte(0x0F)).intValue() + (memoryData.intValue() & 0x0F) + carryIn;
            if (low > 9) low += 6;
            int high = (accumulator.intValue() >> 4) + (memoryData.intValue() >> 4) + (low > 15 ? 1 : 0);

            var result8 = new UnsignedByte(((high << 4) | (low & 0x0F)) & 0xFF);
            this.statusRegister.updateNegativeAndZero(result8);

            // Overflow is still calculated based on binary rules for 6502 (but results are often ignored in decimal mode)
            int binarySum   = accumulator.intValue() + memoryData.intValue() + carryIn;
            var hasOverflow = ((accumulator.intValue() ^ binarySum) & (memoryData.intValue() ^ binarySum) & 0x80) != 0;
            this.statusRegister.updateFlag(Flag.Overflow, hasOverflow);

            if (high > 9) high += 6;
            this.statusRegister.updateFlag(Flag.Carry, high > 15);
            this.accumulator = new UnsignedByte((high << 4 | (low & 0x0F)) & 0xFF);
        } else {
            // Binary mode addition
            var sum16 = accumulator.unsignedWordValue()
                    .add16(memoryData.unsignedWordValue())
                    .add16(new UnsignedWord(carryIn));

            var result8 = sum16.unsignedByteValue();

            var accumulatorXorResult = accumulator.xor(result8);
            var memoryXorResult      = memoryData.xor(result8);
            var overflow             = accumulatorXorResult.and(memoryXorResult);

            var hasCarry    = sum16.isGreaterThan(UnsignedByte.MAX_VALUE);
            var hasOverflow = overflow.testBit(7);

            this.statusRegister.updateFlag(Flag.Carry, hasCarry);
            this.statusRegister.updateFlag(Flag.Overflow, hasOverflow);
            this.statusRegister.updateNegativeAndZero(result8);

            this.accumulator = result8;
        }
    }

    public void RLA(UnsignedWord address) {
        var value = rotateLeft(bus.read(address));
        bus.write(address, value);
        AND(value);
    }

    public void RRA(UnsignedWord address) {
        var value = rotateRight(bus.read(address));
        bus.write(address, value);
        performArithmeticAddition(value);
    }

    public void SLO(UnsignedWord address) {
        var value = shiftLeft(bus.read(address));
        bus.write(address, value);
        ORA(value);
    }

    public void SRE(UnsignedWord address) {
        var value = shiftRight(bus.read(address));
        bus.write(address, value);
        EOR(value);
    }

    public UnsignedByte rotateLeft(UnsignedByte value) {
        var carryIn  = statusRegister.hasFlag(Flag.Carry) ? 1 : 0;
        var carryOut = value.testBit(7);
        var result   = new UnsignedByte(((value.intValue() << 1) | carryIn) & 0xFF);
        statusRegister.updateFlag(Flag.Carry, carryOut);
        statusRegister.updateNegativeAndZero(result);
        return result;
    }

    public UnsignedByte rotateRight(UnsignedByte value) {
        var carryIn  = statusRegister.hasFlag(Flag.Carry) ? 0x80 : 0;
        var carryOut = value.testBit(0);
        var result   = new UnsignedByte(((value.intValue() >> 1) | carryIn) & 0xFF);
        statusRegister.updateFlag(Flag.Carry, carryOut);
        statusRegister.updateNegativeAndZero(result);
        return result;
    }

    public UnsignedByte shiftLeft(UnsignedByte value) {
        var carryOut = value.testBit(7);
        var result   = new UnsignedByte((value.intValue() << 1) & 0xFF);
        statusRegister.updateFlag(Flag.Carry, carryOut);
        statusRegister.updateNegativeAndZero(result);
        return result;
    }

    public UnsignedByte shiftRight(UnsignedByte value) {
        var carryOut = value.testBit(0);
        var result   = new UnsignedByte((value.intValue() >> 1) & 0xFF);
        statusRegister.updateFlag(Flag.Carry, carryOut);
        statusRegister.updateNegativeAndZero(result);
        return result;
    }

    private void AND(UnsignedByte value) {
        accumulator = accumulator.and(value);
        statusRegister.updateNegativeAndZero(accumulator);
    }

    private void ORA(UnsignedByte value) {
        accumulator = accumulator.or(value);
        statusRegister.updateNegativeAndZero(accumulator);
    }

    private void EOR(UnsignedByte value) {
        accumulator = accumulator.xor(value);
        statusRegister.updateNegativeAndZero(accumulator);
    }

    public void NOP() {
        // Do nothing
    }

    public void step() {
        var rawOpcode = bus.read(programCounter);
        programCounter = programCounter.increment();

        var opcode = OpcodeRegistry.get(rawOpcode);
        if (opcode == null) {
            throw new IllegalStateException("Unknown opcode: 0x%02X".formatted(rawOpcode.intValue()));
        }

        this.consumeCycles(opcode.cycles());

        var address = opcode.addressingMode() == null ? null : opcode.addressingMode().resolve(this, bus).address();
        opcode.mnemonic().execute(this, address);
    }
}
