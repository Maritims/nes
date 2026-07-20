package no.clueless.emulation.cpu;

import no.clueless.emulation.Bus;
import no.clueless.emulation.types.UInt16;
import no.clueless.emulation.types.UInt8;

import java.util.EnumSet;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Represents the CPU.
 */
public class CPU {
    /**
     * The bus; the communication channel with the outside world.
     */
    private final Bus  bus;
    private       long totalCycles = 0;

    private       StackPointer   stackPointer;
    private final ProgramCounter programCounter;
    private final StatusRegister statusRegister;
    private final Accumulator    accumulator;
    private final X              x;
    private final Y              y;

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
    CPU(Bus bus, Accumulator accumulator, X x, Y y, StatusRegister statusRegister) {
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
        this.programCounter = new ProgramCounter();
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
        this(bus, new Accumulator(new UInt8(0x00)), new X(new UInt8(0x00)), new Y(new UInt8(0x00)), new StatusRegister());
    }

    public long getTotalCycles() {
        return totalCycles;
    }

    public void consumeCycles(long cycles) {
        this.totalCycles += cycles;
    }

    /**
     * Reboots the system and resets the CPU to its power-on state.
     */
    public void reset() {
        var lowByte     = bus.read(new UInt16(0xFFFC));
        var highByte    = bus.read(new UInt16(0xFFFD));
        var resetVector = UInt16.fromBytes(lowByte, highByte);

        this.programCounter.updateValue(resetVector);
        this.stackPointer = new StackPointer(new UInt8(0xFD), 0x0100);

        this.statusRegister.clearFlag(EnumSet.allOf(Flag.class));
        this.statusRegister.setFlag(Flag.InterruptDisable, Flag.Five);
    }

    /**
     * Reads a 16-bit value from the bus.
     *
     * @param address The address to read from.
     * @return The 16-bit value read.
     * @throws IllegalArgumentException if address is null.
     */
    private UInt16 read16(UInt16 address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }

        var low  = bus.read(address);
        var high = bus.read(address.increment());
        return UInt16.fromBytes(low, high);
    }

    /**
     * Pushes an 8-bit value onto the stack.
     *
     * @param value The value to push.
     * @throws IllegalArgumentException if value is null.
     */
    private void push8(UInt8 value) {
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
    private UInt8 pull8() {
        stackPointer.increment();
        return bus.read(stackPointer.toAddress());
    }

    /**
     * Pushes a 16-bit value onto the stack.
     *
     * @param value The value to push.
     * @throws IllegalArgumentException if value is null.
     */
    private void push16(UInt16 value) {
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
    private UInt16 pull16() {
        var lowByte  = pull8();
        var highByte = pull8();
        return UInt16.fromBytes(lowByte, highByte);
    }

    /**
     * Reads a 8-bit value from the bus and updates the status register.
     *
     * @param address  The address to read from.
     * @param register A consumer that updates a register.
     * @throws IllegalArgumentException if address or register is null.
     */
    private void loadRegisterFromMemory(UInt16 address, Consumer<UInt8> register) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }

        var data = bus.read(address);
        this.statusRegister.updateNegativeAndZero(data);
        register.accept(data);
    }

    /**
     * Reads an 8-bit value from the bus and updates the status register.
     *
     * @param address  The address to write to.
     * @param register The register to read from.
     * @throws IllegalArgumentException if address or register is null.
     */
    private void storeRegisterInMemory(UInt16 address, Supplier<UInt8> register) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        if (register == null) {
            throw new IllegalArgumentException("register cannot be null");
        }
        bus.write(address, register.get());
    }

    /**
     * Updates a register with a new value and updates the status register.
     *
     * @param value    The new value.
     * @param register A consumer that updates a register.
     */
    private void transfer(UInt8 value, Consumer<UInt8> register) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        if (register == null) {
            throw new IllegalArgumentException("registerFactory cannot be null");
        }

        this.statusRegister.updateNegativeAndZero(value);
        register.accept(value);
    }

    /**
     * Compares memory data with a register value and updates the status register.
     *
     * @param address       16-bit address to read from.
     * @param registerValue 8-bit register value to compare with.
     * @throws IllegalArgumentException if address or registerValue is null.
     */
    private void compare(UInt16 address, UInt8 registerValue) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        if (registerValue == null) {
            throw new IllegalArgumentException("registerValue cannot be null");
        }

        var memoryData = bus.read(address);
        var hasCarry   = registerValue.isGreaterThanOrEqualTo(memoryData);
        var tmp        = registerValue.subtract(memoryData);

        this.statusRegister.updateFlag(Flag.Carry, hasCarry);
        this.statusRegister.updateNegativeAndZero(tmp);
    }

    /**
     * Performs a branch if the condition is true.
     *
     * @param address   16-bit address to branch to.
     * @param condition true if the branch should be performed, false otherwise.
     * @throws IllegalArgumentException if address is null.
     */
    private void branch(UInt16 address, boolean condition) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        if (!condition) {
            return;
        }

        totalCycles++;

        if (!programCounter.getValue().highByte().equals(address.highByte())) {
            totalCycles++;
        }

        this.programCounter.updateValue(address);
    }

    /**
     * The address is the byte immediately following the opcode.
     *
     * @return The address.
     */
    public UInt16 addressImmediate() {
        var address = this.programCounter.getValue();
        this.programCounter.increment();
        return address;
    }

    /**
     * Reads a 16-bit absolute address from the current program counter position and advances the program counter by 2 bytes.
     *
     * @return The address.
     */
    public UInt16 addressAbsolute() {
        var address = read16(this.programCounter.getValue());
        this.programCounter.increment();
        this.programCounter.increment();
        return address;
    }

    /**
     * Reads a 16-bit absolute address from the current program counter position, adds the X register value, and advances the program counter by 2 bytes.
     *
     * @return The address.
     */
    public UInt16 addressAbsoluteX() {
        var address = read16(this.programCounter.getValue());
        this.programCounter.increment();
        this.programCounter.increment();
        return address.add8(x.getValue());
    }

    /**
     * Reads a 16-bit absolute address from the current program counter position, adds the Y register value, and advances the program counter by 2 bytes.
     *
     * @return The address.
     */
    public UInt16 addressAbsoluteY() {
        var address = read16(this.programCounter.getValue());
        this.programCounter.increment();
        this.programCounter.increment();
        return address.add8(y.getValue());
    }

    /**
     * Reads an 8-bit offset from the current program counter position and advances the program counter by 1 byte.
     *
     * @return The address.
     */
    public UInt16 addressZeroPage() {
        var offset = bus.read(this.programCounter.getValue());
        this.programCounter.increment();
        return offset.toUInt16();
    }

    /**
     * Reads a 16-bit address from the current program counter position, adds the X register value, and advances the program counter by 2 bytes.
     *
     * @return The address.
     */
    public UInt16 addressZeroPageX() {
        var base = bus.read(this.programCounter.getValue());
        this.programCounter.increment();
        return base.add(x.getValue()).toUInt16();
    }

    /**
     * Reads a 16-bit address from the current program counter position, adds the Y register value, and advances the program counter by 2 bytes.
     *
     * @return The address.
     */
    public UInt16 addressZeroPageY() {
        var base = bus.read(this.programCounter.getValue());
        this.programCounter.increment();
        return base.add(y.getValue()).toUInt16();
    }

    /**
     * Reads a 16-bit addres that's used as a vector to another location in memory.
     * <p>Includes a hardware bug where a vector at $XXFF incorrectly fetches its high byte from $XX00.</p>
     *
     * @return The address.
     */
    public UInt16 addressIndirect() {
        var vector = read16(this.programCounter.getValue());
        this.programCounter.increment();
        this.programCounter.increment();

        var low = bus.read(vector);
        // Emulate the hardware bug: force the high-byte vector lookup to stay on the same page
        var highAddress = (vector.value() & 0xFF00) | ((vector.value() + 1) & 0x00FF);
        var high        = bus.read(new UInt16(highAddress));

        return UInt16.fromBytes(low, high);
    }

    /**
     * Reads an 8-bit address from memory and adds the X register value to construct a pointer to the location to read.
     *
     * @return The address.
     */
    public UInt16 addressIndirectX() {
        var base = bus.read(this.programCounter.getValue());
        this.programCounter.increment();

        var pointerLow  = base.add(x.getValue());
        var pointerHigh = pointerLow.increment();

        var low  = bus.read(pointerLow.toUInt16());
        var high = bus.read(pointerHigh.toUInt16());

        return UInt16.fromBytes(low, high);
    }

    /**
     * Reads an 8-bit address from memory to use a pointer to the location to read, and adds the Y register value to construct the final address.
     *
     * @return The address.
     */
    public UInt16 addressIndirectY() {
        var pointerLow = bus.read(this.programCounter.getValue());
        this.programCounter.increment();

        var pointerHigh = pointerLow.increment();

        var low  = bus.read(pointerLow.toUInt16());
        var high = bus.read(pointerHigh.toUInt16());
        var base = UInt16.fromBytes(low, high);

        return base.add8(y.getValue());
    }

    /**
     * Reads a signed 8-bit offset and advances the program counter by 1 byte.
     *
     * @return The address.
     */
    public UInt16 addressRelative() {
        var offset = bus.read(this.programCounter.getValue());
        this.programCounter.increment();
        return this.programCounter.getValue().addSignedOffset(offset);
    }

    private void performArithmeticAddition(UInt8 memoryData) {
        if (memoryData == null) {
            throw new IllegalArgumentException("memoryData cannot be null");
        }

        var currentAccumulator = accumulator.getValue();
        var carryIn            = statusRegister.hasFlag(Flag.Carry) ? UInt8.ONE : UInt8.ZERO;

        var sum16 = currentAccumulator.toUInt16()
                .add16(memoryData.toUInt16())
                .add16(carryIn.toUInt16());

        var result8 = sum16.toUInt8();

        var accumulatorXorResult = currentAccumulator.xor(result8);
        var memoryXorResult      = memoryData.xor(result8);
        var overflow             = accumulatorXorResult.and(memoryXorResult);

        var hasCarry    = sum16.isGreaterThan(UInt8.MAX_VALUE);
        var hasOverflow = overflow.isBitSet(7);

        this.statusRegister.updateFlag(Flag.Carry, hasCarry);
        this.statusRegister.updateFlag(Flag.Overflow, hasOverflow);
        this.statusRegister.updateNegativeAndZero(result8);

        this.accumulator.updateValue(result8);
    }

    // region Flag instructions

    /**
     * Clears the {@link Flag#Carry} flag.
     */
    public void CLC() {
        this.statusRegister.clearFlag(Flag.Carry);
    }

    /**
     * Clears the {@link Flag#Decimal} flag.
     */
    public void CLD() {
        this.statusRegister.clearFlag(Flag.Decimal);
    }

    /**
     * Clears the {@link Flag#InterruptDisable} flag.
     */
    public void CLI() {
        this.statusRegister.clearFlag(Flag.InterruptDisable);
    }

    /**
     * Clears the {@link Flag#Overflow} flag.
     */
    public void CLV() {
        this.statusRegister.clearFlag(Flag.Overflow);
    }

    /**
     * Sets the {@link Flag#Carry} flag.
     */
    public void SEC() {
        this.statusRegister.setFlag(Flag.Carry);
    }

    /**
     * Sets the {@link Flag#Decimal} flag.
     */
    public void SED() {
        this.statusRegister.setFlag(Flag.Decimal);
    }

    /**
     * Sets the {@link Flag#InterruptDisable} flag.
     */
    public void SEI() {
        this.statusRegister.setFlag(Flag.InterruptDisable);
    }
    // endregion

    // region Bitwise instructions

    /**
     * <code>A = A & memory</code>
     * <p>This ANDs a memory value and the accumulator, bit by bit. If both input bits are 1, the resulting bit is 1. Otherwise it is 0.</p>
     *
     * @param address The address of the memory location to perform the operation on.
     * @throws IllegalArgumentException if address is null.
     */
    public void AND(UInt16 address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }

        var memoryData         = bus.read(address);
        var currentAccumulator = accumulator.getValue();
        var result8            = currentAccumulator.and(memoryData);

        this.statusRegister.updateNegativeAndZero(result8);
        this.accumulator.updateValue(result8);
    }

    /**
     * <code>value = value << 1</code>
     * <p>ASL shifts all the bits of a memory value or the accumulator one position to the left, moving the value of each bit into the next bit. Bit 7 is shifted into the carry flag, and 0 is shifted into bit 0. This is equivalent to multiplying an unsigned value by 2, with carry indicating overflow.</p>
     *
     * @param address The address of the memory location to perform the operation on, or null if the accumulator should be operated on.
     */
    public void ASL(UInt16 address) {
        UInt8 value;
        if (address == null) {
            value = accumulator.getValue();
            if (value == null) {
                throw new IllegalStateException("accumulator value is null");
            }
        } else {
            value = bus.read(address);
            if (value == null) {
                throw new IllegalStateException("memory value at address 0x%02X is null".formatted(address.value()));
            }
        }

        var highBitSet   = value.isBitSet(7);
        var shiftedValue = value.shiftLeft(1);

        this.statusRegister.updateFlag(Flag.Carry, highBitSet);
        this.statusRegister.updateNegativeAndZero(shiftedValue);

        if (address == null) {
            this.accumulator.updateValue(shiftedValue);
        } else {
            // Real 6502 behaviour: Write the original value to the bus first.
            bus.write(address, value);
            bus.write(address, shiftedValue);
        }
    }

    /**
     * <code>A = A ^ memory</code>
     *
     * @param address The address of the memory location to perform the operation on.
     * @throws IllegalArgumentException if address is null.
     */
    public void EOR(UInt16 address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }

        var memoryData         = bus.read(address);
        var currentAccumulator = accumulator.getValue();
        var result             = currentAccumulator.xor(memoryData);

        this.statusRegister.updateNegativeAndZero(result);
        this.accumulator.updateValue(result);
    }

    /**
     * <code>value = value >> 1</code>
     *
     * @param address The address of the memory location to perform the operation on, or null if the accumulator should be operated on.
     * @throws IllegalArgumentException if address is null.
     */
    public void LSR(UInt16 address) {
        var value        = address == null ? accumulator.getValue() : bus.read(address);
        var lowBitSet    = value.isBitSet(0);
        var shiftedValue = value.shiftRight(1);

        this.statusRegister.updateFlag(Flag.Carry, lowBitSet);
        this.statusRegister.updateNegativeAndZero(shiftedValue);

        if (address == null) {
            this.accumulator.updateValue(shiftedValue);
        } else {
            // Real 6502 behaviour: Write the original value to the bus first.
            bus.write(address, value);
            bus.write(address, shiftedValue);
        }
    }

    /**
     * <code>A = A | memory</code>
     *
     * @param address The address of the memory location to perform the operation on.
     * @throws IllegalArgumentException if address is null.
     */
    public void ORA(UInt16 address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }

        var memoryData         = bus.read(address);
        var currentAccumulator = accumulator.getValue();
        var result             = currentAccumulator.or(memoryData);

        this.statusRegister.updateNegativeAndZero(result);
        this.accumulator.updateValue(result);
    }

    /**
     * <code>value = value << 1 through C</code>
     *
     * @param address The address of the memory location to perform the operation on, or null if the accumulator should be operated on.
     */
    public void ROL(UInt16 address) {
        var value        = address == null ? accumulator.getValue() : bus.read(address);
        var oldCarryBit  = this.statusRegister.hasFlag(Flag.Carry) ? 1 : 0;
        var newCarry     = value.isBitSet(7);
        var rotatedValue = value.shiftLeft(1).or(new UInt8(oldCarryBit));

        this.statusRegister.updateFlag(Flag.Carry, newCarry);
        this.statusRegister.updateNegativeAndZero(rotatedValue);

        if (address == null) {
            this.accumulator.updateValue(rotatedValue);
        } else {
            // Real 6502 behaviour: Write the original value to the bus first.
            bus.write(address, value);
            bus.write(address, rotatedValue);
        }
    }

    /**
     * <code>value = value >> 1 through C</code>
     *
     * @param address The address of the memory location to perform the operation on, or null if the accumulator should be operated on.
     */
    public void ROR(UInt16 address) {
        var value        = address == null ? accumulator.getValue() : bus.read(address);
        var oldCarryBit  = this.statusRegister.hasFlag(Flag.Carry) ? 1 : 0;
        var newCarry     = value.isBitSet(0);
        var rotatedValue = value.shiftRight(1).or(new UInt8(oldCarryBit).shiftLeft(7));

        this.statusRegister.updateFlag(Flag.Carry, newCarry);
        this.statusRegister.updateNegativeAndZero(rotatedValue);

        if (address == null) {
            this.accumulator.updateValue(rotatedValue);
        } else {
            // Real 6502 behaviour: Write the original value to the bus first.
            bus.write(address, value);
            bus.write(address, rotatedValue);
        }
    }
    // endregion

    // region Increment and decrement registers instructions

    /**
     * <code>X = X - 1</code>
     */
    public void DEX() {
        x.decrement();
        this.statusRegister.updateNegativeAndZero(x.getValue());
    }

    /**
     * <code>Y = Y - 1</code>
     */
    public void DEY() {
        y.decrement();
        this.statusRegister.updateNegativeAndZero(y.getValue());
    }

    /**
     * <code>X = X + 1</code>
     */
    public void INX() {
        x.increment();
        this.statusRegister.updateNegativeAndZero(x.getValue());
    }

    /**
     * <code>Y = Y + 1</code>
     */
    public void INY() {
        y.increment();
        this.statusRegister.updateNegativeAndZero(y.getValue());
    }
    // endregion

    // region Load, store and transfer instructions

    /**
     * <code>A = memory</code>
     *
     * @param address The address of the memory location to load the value from.
     * @throws IllegalArgumentException if address is null.
     */
    public void LDA(UInt16 address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        loadRegisterFromMemory(address, this.accumulator::updateValue);
    }

    /**
     * <code>X = memory</code>
     *
     * @param address The address of the memory location to load the value from.
     * @throws IllegalArgumentException if address is null.
     */
    public void LDX(UInt16 address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        loadRegisterFromMemory(address, this.x::updateValue);
    }

    /**
     * <code>Y = memory</code>
     *
     * @param address The address of the memory location to load the value from.
     * @throws IllegalArgumentException if address is null.
     */
    public void LDY(UInt16 address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        loadRegisterFromMemory(address, this.y::updateValue);
    }

    /**
     * <code>memory = A</code>
     *
     * @param address The address of the memory location to store the value in.
     * @throws IllegalArgumentException if address is null.
     */
    public void STA(UInt16 address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        storeRegisterInMemory(address, this.accumulator::getValue);
    }

    /**
     * <code>memory = X</code>
     *
     * @param address The address of the memory location to store the value in.
     * @throws IllegalArgumentException if address is null.
     */
    public void STX(UInt16 address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        storeRegisterInMemory(address, this.x::getValue);
    }

    /**
     * <code>memory = Y</code>
     *
     * @param address The address of the memory location to store the value in.
     * @throws IllegalArgumentException if address is null.
     */
    public void STY(UInt16 address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        storeRegisterInMemory(address, this.y::getValue);
    }

    /**
     * <code>X = accumulator</code>
     */
    public void TAX() {
        transfer(this.accumulator.getValue(), this.x::updateValue);
    }

    /**
     * <code>Y = accumulator</code>
     */
    public void TAY() {
        transfer(this.accumulator.getValue(), this.y::updateValue);
    }

    /**
     * <code>X = stack pointer</code>
     */
    public void TSX() {
        transfer(this.stackPointer.getValue(), this.x::updateValue);
    }

    /**
     * <code>Accumulator = X</code>
     */
    public void TXA() {
        transfer(this.x.getValue(), this.accumulator::updateValue);
    }

    /**
     * <code>Stack pointer = X</code>
     */
    public void TXS() {
        stackPointer.updateValue(x.getValue());
    }

    /**
     * <code>Accumulator = Y</code>
     */
    public void TYA() {
        transfer(this.y.getValue(), this.accumulator::updateValue);
    }
    // endregion

    // region Diagnostic instructions

    /**
     * <code>A - memory</code>
     *
     * @param address The address of the memory location to compare the accumulator with.
     * @throws IllegalArgumentException if address is null.
     */
    public void CMP(UInt16 address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        compare(address, accumulator.getValue());
    }

    /**
     * <code>X - memory</code>
     *
     * @param address The address of the memory location to compare the X register with.
     * @throws IllegalArgumentException if address is null.
     */
    public void CPX(UInt16 address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        compare(address, x.getValue());
    }

    /**
     * <code>Y - memory</code>
     *
     * @param address The address of the memory location to compare the Y register with.
     * @throws IllegalArgumentException if address is null.
     */
    public void CPY(UInt16 address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        compare(address, y.getValue());
    }

    /**
     * <code>A & memory</code>
     *
     * @param address The address of the memory location to perform the operation on.
     * @throws IllegalArgumentException if address is null.
     */
    public void BIT(UInt16 address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }

        var memoryData = bus.read(address);

        var andResult = accumulator.getValue().and(memoryData);
        var isZero    = andResult.equals(UInt8.ZERO);

        var bit7 = memoryData.isBitSet(7);
        var bit6 = memoryData.isBitSet(6);

        this.statusRegister.updateFlag(Flag.Zero, isZero);
        this.statusRegister.updateFlag(Flag.Negative, bit7);
        this.statusRegister.updateFlag(Flag.Overflow, bit6);
    }
    // endregion

    /**
     * <code>memory = memory - 1</code>
     *
     * @param address The address of the memory location to decrement.
     * @throws IllegalArgumentException if address is null.
     */
    public void DEC(UInt16 address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }

        var originalValue = bus.read(address);
        var decremented   = originalValue.decrement();
        this.statusRegister.updateNegativeAndZero(decremented);
        bus.write(address, originalValue);
        bus.write(address, decremented);
    }

    /**
     * <code>memory = memory + 1</code>
     *
     * @param address The address of the memory location to increment.
     * @throws IllegalArgumentException if address is null.
     */
    public void INC(UInt16 address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }

        var originalValue = bus.read(address);
        var incremented   = originalValue.increment();
        this.statusRegister.updateNegativeAndZero(incremented);
        bus.write(address, originalValue);
        bus.write(address, incremented);
    }

    public void ADC(UInt16 address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }

        var memoryData = bus.read(address);
        this.performArithmeticAddition(memoryData);
    }

    public void SBC(UInt16 address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }

        var memoryData = bus.read(address);

        // Invert the bits of the memory operand (ones' complement)
        // This naturally transforms the subtraction problem into an addition problem
        var invertedMemoryData = memoryData.xor(UInt8.MAX_VALUE);

        this.performArithmeticAddition(invertedMemoryData);
    }

    public void BRK() {
        var returnAddress = programCounter.getValue().add8(UInt8.ONE);
        push16(returnAddress);

        var statusRegisterAsByte = statusRegister.toByte()
                .or(new UInt8(Flag.Break.getMask()))
                .or(new UInt8(Flag.Five.getMask()));
        push8(statusRegisterAsByte);

        statusRegister.updateFlag(Flag.InterruptDisable, true);

        var lowByte  = bus.read(new UInt16(0xFFFE));
        var highByte = bus.read(new UInt16(0xFFFF));

        programCounter.updateValue(UInt16.fromBytes(lowByte, highByte));
    }

    public void RTI() {
        var status = pull8();
        this.statusRegister.update(StatusRegister.fromByte(status));
        var returnAddress = pull16();
        programCounter.updateValue(returnAddress);
    }

    public void JSR(UInt16 address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }

        var addressToPush = programCounter.getValue().subtract16(UInt16.ONE);
        push16(addressToPush);
        programCounter.updateValue(address);
    }

    public void RTS() {
        var poppedAddress     = pull16();
        var returnDestination = poppedAddress.add16(UInt16.ONE);
        programCounter.updateValue(returnDestination);
    }

    public void JMP(UInt16 address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        programCounter.updateValue(address);
    }

    public void step() {
        var rawOpcode = bus.read(programCounter.getValue());
        this.programCounter.increment();

        var opcode = OpcodeRegistry.get(rawOpcode);
        if (opcode == null) {
            throw new IllegalStateException("Unknown opcode: 0x%02X".formatted(rawOpcode.value()));
        }

        var address = opcode.addressResolver().apply(this);

        switch (opcode.instruction()) {
            case CLC -> CLC();
            case CLD -> CLD();
            case CLI -> CLI();
            case CLV -> CLV();
            case SEC -> SEC();
            case SED -> SED();
            case SEI -> SEI();
            case AND -> AND(address);
            case ASL -> ASL(address);
            case EOR -> EOR(address);
            case LSR -> LSR(address);
            case ORA -> ORA(address);
            case ROL -> ROL(address);
            case ROR -> ROR(address);
            case DEX -> DEX();
            case DEY -> DEY();
            case INX -> INX();
            case INY -> INY();
            case LDA -> LDA(address);
            case LDX -> LDX(address);
            case LDY -> LDY(address);
            case STA -> STA(address);
            case STX -> STX(address);
            case STY -> STY(address);
            case TAX -> TAX();
            case TAY -> TAY();
            case TSX -> TSX();
            case TXA -> TXA();
            case TXS -> TXS();
            case TYA -> TYA();
            case CMP -> CMP(address);
            case CPX -> CPX(address);
            case CPY -> CPY(address);
            case BIT -> BIT(address);
            case DEC -> DEC(address);
            case INC -> INC(address);
            case ADC -> ADC(address);
            case SBC -> SBC(address);
            case BCC -> branch(address, !this.statusRegister.hasFlag(Flag.Carry));
            case BCS -> branch(address, this.statusRegister.hasFlag(Flag.Carry));
            case BEQ -> branch(address, this.statusRegister.hasFlag(Flag.Zero));
            case BMI -> branch(address, this.statusRegister.hasFlag(Flag.Negative));
            case BNE -> branch(address, !this.statusRegister.hasFlag(Flag.Zero));
            case BPL -> branch(address, !this.statusRegister.hasFlag(Flag.Negative));
            case BVC -> branch(address, !this.statusRegister.hasFlag(Flag.Overflow));
            case BVS -> branch(address, this.statusRegister.hasFlag(Flag.Overflow));
            case BRK -> BRK();
            case RTI -> RTI();
            case JSR -> JSR(address);
            case RTS -> RTS();
            case JMP -> JMP(address);
            case NOP -> {
                /* No operation */
            }
            case PHA -> push8(accumulator.getValue());
            case PHP -> push8(statusRegister.toByte());
            case PLA -> {
                var pulledValue = pull8();
                accumulator.updateValue(pulledValue);
                statusRegister.updateNegativeAndZero(pulledValue);
            }
            case PLP -> statusRegister.update(StatusRegister.fromByte(pull8()));
        }
    }
}
