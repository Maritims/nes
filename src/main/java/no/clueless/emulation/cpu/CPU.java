package no.clueless.emulation.cpu;

import no.clueless.emulation.Bus;
import no.clueless.emulation.types.UInt16;
import no.clueless.emulation.types.UInt8;

import java.util.Set;
import java.util.function.Function;

/**
 * Represents the CPU.
 */
public class CPU {
    /**
     * The bus; the communication channel with the outside world.
     */
    private final Bus bus;

    private StackPointer   stackPointer;
    private ProgramCounter programCounter;
    private StatusRegister statusRegister;
    private Accumulator    accumulator;
    private X              x;
    private Y              y;

    /**
     * Constructor.
     *
     * @param bus The bus to use.
     * @throws IllegalArgumentException if bus is null.
     */
    public CPU(Bus bus) {
        if (bus == null) {
            throw new IllegalArgumentException("Bus cannot be null");
        }

        // TODO: Ensure all registers are initialized with their power-on values.
        this.bus            = bus;
        this.stackPointer   = new StackPointer(new UInt8(0xFD));
        this.statusRegister = new StatusRegister(Set.of());
        this.accumulator    = new Accumulator(new UInt8(0x00));
        this.x              = new X(new UInt8(0x00));
        this.y              = new Y(new UInt8(0x00));
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
     * The address is the byte immediately following the opcode.
     *
     * @return The address.
     */
    public UInt16 addressImmediate() {
        var address = this.programCounter.getValue();
        this.programCounter = this.programCounter.increment();
        return address;
    }

    /**
     * Reads a 16-bit absolute address from the current program counter position and advances the program counter by 2 bytes.
     *
     * @return The address.
     */
    public UInt16 addressAbsolute() {
        var address = read16(this.programCounter.getValue());
        this.programCounter = this.programCounter.increment().increment();
        return address;
    }

    /**
     * Reads a 16-bit absolute address from the current program counter position, adds the X register value, and advances the program counter by 2 bytes.
     *
     * @return The address.
     */
    public UInt16 addressAbsoluteX() {
        var address = read16(this.programCounter.getValue());
        this.programCounter = this.programCounter.increment().increment();
        return address.add8(x.getValue());
    }

    /**
     * Reads a 16-bit absolute address from the current program counter position, adds the Y register value, and advances the program counter by 2 bytes.
     *
     * @return The address.
     */
    public UInt16 addressAbsoluteY() {
        var address = read16(this.programCounter.getValue());
        this.programCounter = this.programCounter.increment().increment();
        return address.add8(y.getValue());
    }

    /**
     * Reads an 8-bit offset from the current program counter position and advances the program counter by 1 byte.
     *
     * @return The address.
     */
    public UInt16 addressZeroPage() {
        var offset = bus.read(this.programCounter.getValue());
        this.programCounter = this.programCounter.increment();
        return offset.toUInt16();
    }

    /**
     * Reads a 16-bit address from the current program counter position, adds the X register value, and advances the program counter by 2 bytes.
     *
     * @return The address.
     */
    public UInt16 addressZeroPageX() {
        var base = bus.read(this.programCounter.getValue());
        this.programCounter = this.programCounter.increment();
        return base.add(x.getValue()).toUInt16();
    }

    /**
     * Reads a 16-bit address from the current program counter position, adds the Y register value, and advances the program counter by 2 bytes.
     *
     * @return The address.
     */
    public UInt16 addressZeroPageY() {
        var base = bus.read(this.programCounter.getValue());
        this.programCounter = this.programCounter.increment();
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
        this.programCounter = this.programCounter.increment().increment();

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
        this.programCounter = this.programCounter.increment();

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
        this.programCounter = this.programCounter.increment();

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
        this.programCounter = this.programCounter.increment();
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

        this.statusRegister = this.statusRegister
                .updateFlag(Flag.Carry, hasCarry)
                .updateFlag(Flag.Overflow, hasOverflow)
                .updateNegativeAndZero(result8);

        this.accumulator = new Accumulator(result8);
    }

    public void ADC(UInt16 address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }

        var memoryData = bus.read(address);
        this.performArithmeticAddition(memoryData);
    }

    public void AND(UInt16 address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }

        var memoryData         = bus.read(address);
        var currentAccumulator = accumulator.getValue();
        var result8            = currentAccumulator.and(memoryData);

        this.statusRegister = this.statusRegister.updateNegativeAndZero(result8);
        this.accumulator    = new Accumulator(result8);
    }

    public void DEX() {
        var result8 = x.getValue().decrement();
        this.x              = new X(result8);
        this.statusRegister = this.statusRegister.updateNegativeAndZero(result8);
    }

    public void DEY() {
        var result8 = y.getValue().decrement();
        this.y              = new Y(result8);
        this.statusRegister = this.statusRegister.updateNegativeAndZero(result8);
    }

    public void INX() {
        var result8 = x.getValue().increment();
        this.x              = new X(result8);
        this.statusRegister = this.statusRegister.updateNegativeAndZero(result8);
    }

    public void INY() {
        var result8 = y.getValue().increment();
        this.y              = new Y(result8);
        this.statusRegister = this.statusRegister.updateNegativeAndZero(result8);
    }

    public void ORA(UInt16 address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }

        var memoryData         = bus.read(address);
        var currentAccumulator = accumulator.getValue();
        var result             = currentAccumulator.or(memoryData);

        this.statusRegister = this.statusRegister.updateNegativeAndZero(result);
        this.accumulator    = new Accumulator(result);
    }

    public void EOR(UInt16 address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }

        var memoryData         = bus.read(address);
        var currentAccumulator = accumulator.getValue();
        var result             = currentAccumulator.xor(memoryData);

        this.statusRegister = this.statusRegister.updateNegativeAndZero(result);
        this.accumulator    = new Accumulator(result);
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

    private <R extends Register<UInt8, R>> R loadFromRegister(UInt16 address, R register) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        if (register == null) {
            throw new IllegalArgumentException("register cannot be null");
        }

        var data = bus.read(address);
        this.statusRegister.updateNegativeAndZero(data);
        return register.create(data);
    }

    private void storeRegister(UInt16 address, Register<UInt8, ?> register) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        if (register == null) {
            throw new IllegalArgumentException("register cannot be null");
        }
        bus.write(address, register.getValue());
    }

    private <R extends Register<UInt8, R>> R transfer(UInt8 value, Function<UInt8, R> registerFactory) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        if (registerFactory == null) {
            throw new IllegalArgumentException("registerFactory cannot be null");
        }

        this.statusRegister.updateNegativeAndZero(value);
        return registerFactory.apply(value);
    }

    public void step() {
        var rawOpcode = bus.read(programCounter.getValue());
        this.programCounter = this.programCounter.increment();

        var opcode = OpcodeRegistry.get(rawOpcode);
        if (opcode == null) {
            throw new IllegalStateException("Unknown opcode: 0x%02X".formatted(rawOpcode.value()));
        }

        var address = opcode.addressResolver().apply(this);

        switch (opcode.instruction()) {
            case ADC -> ADC(address);
            case AND -> AND(address);
            case ASL -> {
            }
            case BCC -> {
            }
            case BCS -> {
            }
            case BEQ -> {
            }
            case BIT -> {
            }
            case BMI -> {
            }
            case BNE -> {
            }
            case BPL -> {
            }
            case BRK -> {
            }
            case BVC -> {
            }
            case BVS -> {
            }
            case CLC -> this.statusRegister = this.statusRegister.updateFlag(Flag.Carry, false);
            case CLD -> this.statusRegister = this.statusRegister.updateFlag(Flag.Decimal, false);
            case CLI -> this.statusRegister = this.statusRegister.updateFlag(Flag.InterruptDisable, false);
            case CLV -> this.statusRegister = this.statusRegister.updateFlag(Flag.Overflow, false);
            case CMP -> {
            }
            case CPX -> {
            }
            case CPY -> {
            }
            case DEC -> {
            }
            case DEX -> DEX();
            case DEY -> DEY();
            case EOR -> EOR(address);
            case INC -> {
            }
            case INX -> INX();
            case INY -> INY();
            case JMP -> {
            }
            case JSR -> {
            }
            case LDA -> this.accumulator = loadFromRegister(address, this.accumulator);
            case LDX -> this.x = loadFromRegister(address, this.x);
            case LDY -> this.y = loadFromRegister(address, this.y);
            case LSR -> {
            }
            case NOP -> {
                /* No operation */
            }
            case ORA -> ORA(address);
            case PHA -> {
            }
            case PHP -> {
            }
            case PLA -> {
            }
            case PLP -> {
            }
            case ROL -> {
            }
            case ROR -> {
            }
            case RTI -> {
            }
            case RTS -> {
            }
            case SBC -> SBC(address);
            case SEC -> this.statusRegister = this.statusRegister.updateFlag(Flag.Carry, true);
            case SED -> this.statusRegister = this.statusRegister.updateFlag(Flag.Decimal, true);
            case SEI -> this.statusRegister = this.statusRegister.updateFlag(Flag.InterruptDisable, true);
            case STA -> storeRegister(address, this.accumulator);
            case STX -> storeRegister(address, this.x);
            case STY -> storeRegister(address, this.y);
            case TAX -> this.x = transfer(this.accumulator.getValue(), X::new);
            case TAY -> this.y = transfer(this.accumulator.getValue(), Y::new);
            case TSX -> this.x = transfer(this.stackPointer.getValue(), X::new);
            case TXA -> this.accumulator = transfer(this.x.getValue(), Accumulator::new);
            case TXS -> this.stackPointer = new StackPointer(this.x.getValue());
            case TYA -> this.accumulator = transfer(this.y.getValue(), Accumulator::new);
        }
    }
}
