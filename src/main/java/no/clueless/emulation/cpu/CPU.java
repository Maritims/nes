package no.clueless.emulation.cpu;

import no.clueless.emulation.Bus;
import no.clueless.emulation.types.UnsignedWord;
import no.clueless.emulation.types.UnsignedByte;

import java.util.function.Consumer;
import java.util.function.Supplier;

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

    public long getTotalCycles() {
        return totalCycles;
    }

    public void consumeCycles(long cycles) {
        this.totalCycles += cycles;
    }

    public UnsignedWord getProgramCounter() {
        return programCounter;
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

    public StatusRegister getStatusRegister() {
        return statusRegister;
    }

    public StackPointer getStackPointer() {
        return stackPointer;
    }

    public void setProgramCounter(UnsignedWord pc) {
        this.programCounter = pc;
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
     * Reads a 16-bit value from the bus.
     *
     * @param address The address to read from.
     * @return The 16-bit value read.
     * @throws IllegalArgumentException if address is null.
     */
    private UnsignedWord read16(UnsignedWord address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }

        var low  = bus.read(address);
        var high = bus.read(address.increment());
        return UnsignedWord.fromBytes(low, high);
    }

    /**
     * Pushes an 8-bit value onto the stack.
     *
     * @param value The value to push.
     * @throws IllegalArgumentException if value is null.
     */
    private void push8(UnsignedByte value) {
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
    private UnsignedByte pull8() {
        stackPointer.increment();
        return bus.read(stackPointer.toAddress());
    }

    /**
     * Pushes a 16-bit value onto the stack.
     *
     * @param value The value to push.
     * @throws IllegalArgumentException if value is null.
     */
    private void push16(UnsignedWord value) {
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
    private UnsignedWord pull16() {
        var lowByte  = pull8();
        var highByte = pull8();
        return UnsignedWord.fromBytes(lowByte, highByte);
    }

    /**
     * Reads an 8-bit value from the bus and updates the status register.
     *
     * @param address  The address to read from.
     * @param register A consumer that updates a register.
     * @throws IllegalArgumentException if address or register is null.
     */
    private void loadRegisterFromMemory(UnsignedWord address, Consumer<UnsignedByte> register) {
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
    private void storeRegisterInMemory(UnsignedWord address, Supplier<UnsignedByte> register) {
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
    private void transfer(UnsignedByte value, Consumer<UnsignedByte> register) {
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
    private void compare(UnsignedWord address, UnsignedByte registerValue) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        var memoryData = bus.read(address);
        compare(registerValue, memoryData);
    }

    private void compare(UnsignedByte registerValue, UnsignedByte memoryValue) {
        if (registerValue == null || memoryValue == null) {
            throw new IllegalArgumentException("values cannot be null");
        }
        var hasCarry = registerValue.compareTo(memoryValue) >= 0;
        var tmp      = registerValue.subtract(memoryValue);

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
    private void branch(UnsignedWord address, boolean condition) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        if (!condition) {
            return;
        }

        this.consumeCycles(1);

        if (isPageCrossed(programCounter, address)) {
            this.consumeCycles(1);
        }

        this.programCounter = address;
    }

    /**
     * The address is the byte immediately following the opcode.
     *
     * @return The address.
     */
    public UnsignedWord addressImmediate() {
        var address = programCounter;
        programCounter = programCounter.increment();
        return address;
    }

    /**
     * Reads a 16-bit absolute address from the current program counter position and advances the program counter by 2 bytes.
     *
     * @return The address.
     */
    public UnsignedWord addressAbsolute() {
        var address = read16(programCounter);
        programCounter = programCounter.increment().increment();
        return address;
    }

    private boolean isPageCrossed(UnsignedWord a, UnsignedWord b) {
        return (a.intValue() & 0xFF00) != (b.intValue() & 0xFF00);
    }

    /**
     * Reads a 16-bit absolute address from the current program counter position, adds the X register value, and advances the program counter by 2 bytes.
     *
     * @return The address.
     */
    public UnsignedWord addressAbsoluteX() {
        var base = read16(programCounter);
        programCounter = programCounter.increment().increment();

        var address = base.add8(x);
        if (isPageCrossed(base, address)) {
            this.consumeCycles(1);
        }

        return address;
    }

    /**
     * Reads a 16-bit absolute address from the current program counter position, adds the Y register value, and advances the program counter by 2 bytes.
     *
     * @return The address.
     */
    public UnsignedWord addressAbsoluteY() {
        var base = read16(programCounter);
        programCounter = programCounter.increment().increment();

        var address = base.add8(y);
        if (isPageCrossed(base, address)) {
            this.consumeCycles(1);
        }

        return address;
    }

    /**
     * Reads an 8-bit offset from the current program counter position and advances the program counter by 1 byte.
     *
     * @return The address.
     */
    public UnsignedWord addressZeroPage() {
        var offset = bus.read(programCounter);
        programCounter = programCounter.increment();
        return offset.unsignedWordValue();
    }

    /**
     * Reads a 16-bit address from the current program counter position, adds the X register value, and advances the program counter by 2 bytes.
     *
     * @return The address.
     */
    public UnsignedWord addressZeroPageX() {
        var base = bus.read(programCounter);
        programCounter = programCounter.increment();
        return base.add(x).unsignedWordValue();
    }

    /**
     * Reads a 16-bit address from the current program counter position, adds the Y register value, and advances the program counter by 2 bytes.
     *
     * @return The address.
     */
    public UnsignedWord addressZeroPageY() {
        var base = bus.read(programCounter);
        programCounter = programCounter.increment();
        return base.add(y).unsignedWordValue();
    }

    /**
     * Reads a 16-bit addres that's used as a vector to another location in memory.
     * <p>Includes a hardware bug where a vector at $XXFF incorrectly fetches its high byte from $XX00.</p>
     *
     * @return The address.
     */
    public UnsignedWord addressIndirect() {
        var vector = read16(programCounter);
        programCounter = programCounter.increment().increment();

        var low = bus.read(vector);
        // Emulate the hardware bug: force the high-byte vector lookup to stay on the same page
        var highAddress = (vector.intValue() & 0xFF00) | ((vector.intValue() + 1) & 0x00FF);
        var high        = bus.read(new UnsignedWord(highAddress));

        return UnsignedWord.fromBytes(low, high);
    }

    /**
     * Reads an 8-bit address from memory and adds the X register value to construct a pointer to the location to read.
     *
     * @return The address.
     */
    public UnsignedWord addressIndirectX() {
        var base = bus.read(programCounter);
        programCounter = programCounter.increment();

        var pointerLow  = base.add(x);
        var pointerHigh = pointerLow.increment();

        var low  = bus.read(pointerLow.unsignedWordValue());
        var high = bus.read(pointerHigh.unsignedWordValue());

        return UnsignedWord.fromBytes(low, high);
    }

    /**
     * Reads an 8-bit address from memory to use a pointer to the location to read and adds the Y register value to construct the final address.
     *
     * @return The address.
     */
    public UnsignedWord addressIndirectY() {
        var pointerLow = bus.read(programCounter);
        programCounter = programCounter.increment();

        var pointerHigh = pointerLow.increment();

        var low  = bus.read(pointerLow.unsignedWordValue());
        var high = bus.read(pointerHigh.unsignedWordValue());
        var base = UnsignedWord.fromBytes(low, high);

        var address = base.add8(y);
        if (isPageCrossed(base, address)) {
            this.consumeCycles(1);
        }

        return address;
    }

    /**
     * Reads a signed 8-bit offset and advances the program counter by 1 byte.
     *
     * @return The address.
     */
    public UnsignedWord addressRelative() {
        var offset = bus.read(programCounter);
        programCounter = programCounter.increment();
        return programCounter.addSignedOffset(offset);
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
     * <p>This ANDs a memory value and the accumulator, bit by bit. If both input bits are 1, the resulting bit is 1. Otherwise, it is 0.</p>
     *
     * @param address The address of the memory location to perform the operation on.
     * @throws IllegalArgumentException if address is null.
     */
    public void AND(UnsignedWord address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        AND(bus.read(address));
    }

    /**
     * <code>value = value << 1</code>
     * <p>ASL shifts all the bits of a memory value or the accumulator one position to the left, moving the value of each bit into the next bit. Bit 7 is shifted into the carry flag, and 0 is shifted into bit 0. This is equivalent to multiplying an unsigned value by 2, with carry indicating overflow.</p>
     *
     * @param address The address of the memory location to perform the operation on, or null if the accumulator should be operated on.
     */
    public void ASL(UnsignedWord address) {
        if (address == null) {
            accumulator = shiftLeft(accumulator);
        } else {
            var original = bus.read(address);
            var shifted  = shiftLeft(original);
            bus.write(address, original);
            bus.write(address, shifted);
        }
    }

    /**
     * <code>A = A ^ memory</code>
     *
     * @param address The address of the memory location to perform the operation on.
     * @throws IllegalArgumentException if address is null.
     */
    public void EOR(UnsignedWord address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        EOR(bus.read(address));
    }

    /**
     * <code>value = value >> 1</code>
     *
     * @param address The address of the memory location to perform the operation on, or null if the accumulator should be operated on.
     * @throws IllegalArgumentException if address is null.
     */
    public void LSR(UnsignedWord address) {
        if (address == null) {
            accumulator = shiftRight(accumulator);
        } else {
            var original = bus.read(address);
            var shifted  = shiftRight(original);
            bus.write(address, original);
            bus.write(address, shifted);
        }
    }

    /**
     * <code>A = A | memory</code>
     *
     * @param address The address of the memory location to perform the operation on.
     * @throws IllegalArgumentException if address is null.
     */
    public void ORA(UnsignedWord address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        ORA(bus.read(address));
    }

    /**
     * <code>value = value << 1 through C</code>
     *
     * @param address The address of the memory location to perform the operation on, or null if the accumulator should be operated on.
     */
    public void ROL(UnsignedWord address) {
        if (address == null) {
            accumulator = rotateLeft(accumulator);
        } else {
            var original = bus.read(address);
            var rotated  = rotateLeft(original);
            bus.write(address, original);
            bus.write(address, rotated);
        }
    }

    /**
     * <code>value = value >> 1 through C</code>
     *
     * @param address The address of the memory location to perform the operation on, or null if the accumulator should be operated on.
     */
    public void ROR(UnsignedWord address) {
        if (address == null) {
            accumulator = rotateRight(accumulator);
        } else {
            var original = bus.read(address);
            var rotated  = rotateRight(original);
            bus.write(address, original);
            bus.write(address, rotated);
        }
    }
    // endregion

    // region Increment and decrement registers instructions

    /**
     * <code>X = X - 1</code>
     */
    public void DEX() {
        x = x.decrement();
        this.statusRegister.updateNegativeAndZero(x);
    }

    /**
     * <code>Y = Y - 1</code>
     */
    public void DEY() {
        y = y.decrement();
        this.statusRegister.updateNegativeAndZero(y);
    }

    /**
     * <code>X = X + 1</code>
     */
    public void INX() {
        x = x.increment();
        this.statusRegister.updateNegativeAndZero(x);
    }

    /**
     * <code>Y = Y + 1</code>
     */
    public void INY() {
        y = y.increment();
        this.statusRegister.updateNegativeAndZero(y);
    }
    // endregion

    // region Load, store and transfer instructions

    /**
     * <code>A = memory</code>
     *
     * @param address The address of the memory location to load the value from.
     * @throws IllegalArgumentException if address is null.
     */
    public void LDA(UnsignedWord address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        loadRegisterFromMemory(address, (foo) -> this.accumulator = foo);
    }

    /**
     * <code>X = memory</code>
     *
     * @param address The address of the memory location to load the value from.
     * @throws IllegalArgumentException if address is null.
     */
    public void LDX(UnsignedWord address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        loadRegisterFromMemory(address, (foo) -> this.x = foo);
    }

    /**
     * <code>Y = memory</code>
     *
     * @param address The address of the memory location to load the value from.
     * @throws IllegalArgumentException if address is null.
     */
    public void LDY(UnsignedWord address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        loadRegisterFromMemory(address, (foo) -> this.y = foo);
    }

    /**
     * <code>memory = A</code>
     *
     * @param address The address of the memory location to store the value in.
     * @throws IllegalArgumentException if address is null.
     */
    public void STA(UnsignedWord address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        storeRegisterInMemory(address, () -> this.accumulator);
    }

    /**
     * <code>memory = X</code>
     *
     * @param address The address of the memory location to store the value in.
     * @throws IllegalArgumentException if address is null.
     */
    public void STX(UnsignedWord address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        storeRegisterInMemory(address, () -> x);
    }

    /**
     * <code>memory = Y</code>
     *
     * @param address The address of the memory location to store the value in.
     * @throws IllegalArgumentException if address is null.
     */
    public void STY(UnsignedWord address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        storeRegisterInMemory(address, () -> this.y);
    }

    /**
     * <code>X = accumulator</code>
     */
    public void TAX() {
        transfer(this.accumulator, (foo) -> this.x = foo);
    }

    /**
     * <code>Y = accumulator</code>
     */
    public void TAY() {
        transfer(this.accumulator, (foo) -> this.y = foo);
    }

    /**
     * <code>X = stack pointer</code>
     */
    public void TSX() {
        transfer(this.stackPointer.getValue(), (foo) -> this.x = foo);
    }

    /**
     * <code>Accumulator = X</code>
     */
    public void TXA() {
        transfer(this.x, (foo) -> this.accumulator = foo);
    }

    /**
     * <code>Stack pointer = X</code>
     */
    public void TXS() {
        stackPointer.updateValue(x);
    }

    /**
     * <code>Accumulator = Y</code>
     */
    public void TYA() {
        transfer(this.y, (foo) -> this.accumulator = foo);
    }
    // endregion

    // region Diagnostic instructions

    /**
     * <code>A - memory</code>
     *
     * @param address The address of the memory location to compare the accumulator with.
     * @throws IllegalArgumentException if address is null.
     */
    public void CMP(UnsignedWord address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        compare(address, accumulator);
    }

    /**
     * <code>X - memory</code>
     *
     * @param address The address of the memory location to compare the X register with.
     * @throws IllegalArgumentException if address is null.
     */
    public void CPX(UnsignedWord address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        compare(address, x);
    }

    /**
     * <code>Y - memory</code>
     *
     * @param address The address of the memory location to compare the Y register with.
     * @throws IllegalArgumentException if address is null.
     */
    public void CPY(UnsignedWord address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        compare(address, y);
    }

    /**
     * <code>A & memory</code>
     *
     * @param address The address of the memory location to perform the operation on.
     * @throws IllegalArgumentException if address is null.
     */
    public void BIT(UnsignedWord address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }

        var memoryData = bus.read(address);

        var andResult = accumulator.and(memoryData);
        var isZero    = andResult.equals(UnsignedByte.ZERO);

        var bit7 = memoryData.testBit(7);
        var bit6 = memoryData.testBit(6);

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
    public void DEC(UnsignedWord address) {
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
    public void INC(UnsignedWord address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }

        var originalValue = bus.read(address);
        var incremented   = originalValue.increment();
        this.statusRegister.updateNegativeAndZero(incremented);
        bus.write(address, originalValue);
        bus.write(address, incremented);
    }

    public void ADC(UnsignedWord address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }

        var memoryData = bus.read(address);
        this.performArithmeticAddition(memoryData);
    }

    public void SBC(UnsignedWord address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }

        var memoryData = bus.read(address);

        if (decimalModeSupported && statusRegister.hasFlag(Flag.Decimal)) {
            // Decimal mode subtraction
            var carryIn = statusRegister.hasFlag(Flag.Carry) ? 1 : 0;

            int low = (accumulator.intValue() & 0x0F) - (memoryData.intValue() & 0x0F) - (1 - carryIn);
            if (low < 0) low -= 6;
            int high = (accumulator.intValue() >> 4) - (memoryData.intValue() >> 4) - (low < 0 ? 1 : 0);
            if (high < 0) high -= 6;

            // Flags are calculated based on the binary subtraction result on 6502
            int binaryResult = accumulator.intValue() - memoryData.intValue() - (1 - carryIn);
            this.statusRegister.updateFlag(Flag.Carry, binaryResult >= 0);
            this.statusRegister.updateFlag(Flag.Overflow, ((accumulator.intValue() ^ memoryData.intValue()) & (accumulator.intValue() ^ binaryResult) & 0x80) != 0);
            this.statusRegister.updateNegativeAndZero(new UnsignedByte(binaryResult & 0xFF));

            this.accumulator = new UnsignedByte((high << 4 | (low & 0x0F)) & 0xFF);
        } else {
            // Binary mode subtraction
            // Invert the bits of the memory operand (ones' complement)
            // This naturally transforms the subtraction problem into an addition problem
            var invertedMemoryData = memoryData.xor(UnsignedByte.MAX_VALUE);
            this.performArithmeticAddition(invertedMemoryData);
        }
    }

    public void BRK() {
        var returnAddress = programCounter.add16(new UnsignedWord(1));
        push16(returnAddress);

        var statusRegisterAsByte = statusRegister.unsignedByteValue().or(new UnsignedByte(Flag.Break.getMask()));
        push8(statusRegisterAsByte);

        statusRegister.updateFlag(Flag.InterruptDisable, true);

        var lowByte  = bus.read(new UnsignedWord(0xFFFE));
        var highByte = bus.read(new UnsignedWord(0xFFFF));

        programCounter = UnsignedWord.fromBytes(lowByte, highByte);
    }

    public void RTI() {
        var status = pull8();
        this.statusRegister.update(StatusRegister.fromByte(status));
        programCounter = pull16();
    }

    public void JSR(UnsignedWord address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }

        var addressToPush = programCounter.subtract16(UnsignedWord.ONE);
        push16(addressToPush);
        programCounter = address;
    }

    public void RTS() {
        var poppedAddress     = pull16();
        programCounter = poppedAddress.add16(UnsignedWord.ONE);
    }

    public void JMP(UnsignedWord address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        programCounter = address;
    }

    public void PHA() {
        push8(accumulator);
    }

    public void PHP() {
        // PHP pushes the status register with bit 4 (Break) set to 1
        var status = statusRegister.unsignedByteValue().or(new UnsignedByte(Flag.Break.getMask()));
        push8(status);
    }

    public void PLA() {
        accumulator = pull8();
        statusRegister.updateNegativeAndZero(accumulator);
    }

    public void LAX(UnsignedWord address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        accumulator = bus.read(address);
        x           = accumulator;
        statusRegister.updateNegativeAndZero(accumulator);
    }

    public void SAX(UnsignedWord address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        bus.write(address, accumulator.and(x));
    }

    public void DCP(UnsignedWord address) {
        var value = bus.read(address).decrement();
        bus.write(address, value);
        compare(accumulator, value);
    }

    public void ISB(UnsignedWord address) {
        var value = bus.read(address).increment();
        bus.write(address, value);
        var invertedValue = value.xor(UnsignedByte.MAX_VALUE);
        performArithmeticAddition(invertedValue);
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

    private UnsignedByte rotateLeft(UnsignedByte value) {
        var carryIn  = statusRegister.hasFlag(Flag.Carry) ? 1 : 0;
        var carryOut = value.testBit(7);
        var result   = new UnsignedByte(((value.intValue() << 1) | carryIn) & 0xFF);
        statusRegister.updateFlag(Flag.Carry, carryOut);
        statusRegister.updateNegativeAndZero(result);
        return result;
    }

    private UnsignedByte rotateRight(UnsignedByte value) {
        var carryIn  = statusRegister.hasFlag(Flag.Carry) ? 0x80 : 0;
        var carryOut = value.testBit(0);
        var result   = new UnsignedByte(((value.intValue() >> 1) | carryIn) & 0xFF);
        statusRegister.updateFlag(Flag.Carry, carryOut);
        statusRegister.updateNegativeAndZero(result);
        return result;
    }

    private UnsignedByte shiftLeft(UnsignedByte value) {
        var carryOut = value.testBit(7);
        var result   = new UnsignedByte((value.intValue() << 1) & 0xFF);
        statusRegister.updateFlag(Flag.Carry, carryOut);
        statusRegister.updateNegativeAndZero(result);
        return result;
    }

    private UnsignedByte shiftRight(UnsignedByte value) {
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

    public void PLP() {
        statusRegister.update(StatusRegister.fromByte(pull8()));
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
            case NOP -> NOP();
            case PHA -> PHA();
            case PHP -> PHP();
            case PLA -> PLA();
            case PLP -> PLP();
            case LAX -> LAX(address);
            case SAX -> SAX(address);
            case DCP -> DCP(address);
            case ISB -> ISB(address);
            case RLA -> RLA(address);
            case RRA -> RRA(address);
            case SLO -> SLO(address);
            case SRE -> SRE(address);
        }
    }
}
