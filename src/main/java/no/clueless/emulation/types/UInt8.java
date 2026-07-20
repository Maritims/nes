package no.clueless.emulation.types;

import java.util.function.IntFunction;

/**
 * Represents an unsigned 8-bit integer.
 *
 * @param value The value of the integer.
 */
public record UInt8(int value) implements UInt<UInt8> {
    /**
     * Constructor.
     *
     * @param value The value of the integer.
     * @throws IllegalArgumentException if the value is not between 0 and 255.
     */
    public UInt8 {
        if (value < 0 || value > 255) {
            throw new IllegalArgumentException("Value must be between 0 and 255");
        }
    }

    public static final UInt8 ZERO      = new UInt8(0);
    public static final UInt8 ONE       = new UInt8(1);
    public static final UInt8 MAX_VALUE = new UInt8(0xFF);

    @Override
    public IntFunction<UInt8> factory() {
        return UInt8::new;
    }

    @Override
    public UInt8 increment() {
        return new UInt8((value + 1) & 0xFF);
    }

    @Override
    public UInt8 decrement() {
        return new UInt8((value - 1) & 0xFF);
    }

    @Override
    public boolean isBitSet(int bit) {
        if (bit < 0 || bit > 7) {
            throw new IllegalArgumentException("Bit must be between 0 and 7");
        }
        return (value & (1 << bit)) != 0;
    }

    @Override
    public UInt8 shiftLeft(int bits) {
        if (bits < 0) {
            throw new IllegalArgumentException("bits must be non-negative");
        }
        return new UInt8((value << bits) & 0xFF);
    }

    @Override
    public UInt8 shiftRight(int bits) {
        if (bits < 0) {
            throw new IllegalArgumentException("bits must be non-negative");
        }
        return new UInt8((value >> bits) & 0xFF);
    }

    public UInt16 toUInt16() {
        return new UInt16(value);
    }

    public UInt8 add(UInt8 value) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        return new UInt8((this.value() + value.value()) & 0xFF);
    }

    public UInt8 subtract(UInt8 value) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        return new UInt8((this.value() - value.value()) & 0xFF);
    }

    public boolean isGreaterThanOrEqualTo(UInt8 other) {
        if (other == null) {
            throw new IllegalArgumentException("other cannot be null");
        }
        return value >= other.value();
    }
}
