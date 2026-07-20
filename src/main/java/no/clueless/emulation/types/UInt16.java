package no.clueless.emulation.types;

import java.util.function.IntFunction;

/**
 * Represents an unsigned 16-bit integer.
 *
 * @param value The value of the integer.
 */
public record UInt16(int value) implements UInt<UInt16> {
    /**
     * Constructor.
     *
     * @param value The value of the integer.
     * @throws IllegalArgumentException if the value is not between 0 and 65,535.
     */
    public UInt16 {
        if (value < 0 || value > 65535) {
            throw new IllegalArgumentException("Value must be between 0 and 65535");
        }
    }

    /**
     * Creates a UInt16 from a low and high byte with a Little-Endian layout.
     *
     * @param low  The low byte.
     * @param high The high byte.
     * @return A UInt16 instance.
     * @throws IllegalArgumentException if low or high is null.
     */
    public static UInt16 fromBytes(UInt8 low, UInt8 high) {
        if (low == null) {
            throw new IllegalArgumentException("low cannot be null");
        }
        if (high == null) {
            throw new IllegalArgumentException("high cannot be null");
        }
        return new UInt16((high.value() << 8) | low.value());
    }

    @Override
    public IntFunction<UInt16> factory() {
        return UInt16::new;
    }

    @Override
    public UInt16 increment() {
        return new UInt16((value + 1) & 0xFFFF);
    }

    @Override
    public UInt16 decrement() {
        return new UInt16((value - 1) & 0xFFFF);
    }

    @Override
    public boolean isBitSet(int bit) {
        if (bit < 0 || bit > 15) {
            throw new IllegalArgumentException("Bit must be between 0 and 15");
        }
        return (value & (1 << bit)) != 0;
    }

    public UInt16 add8(UInt8 value) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        return new UInt16((this.value() + value.value()) & 0xFFFF);
    }

    public UInt16 add16(UInt16 value) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        return new UInt16((this.value() + value.value()) & 0xFFFF);
    }

    /**
     * Adds a signed 8-bit two's-complement offset (interpreted as -128 to 127) to this 16-bit value, wrapping naturally at 16 bits.
     *
     * @param offset The unsigned UInt8 offset representing the signed offset.
     * @return The result of the addition.
     * @throws IllegalArgumentException if offset is null.
     */
    public UInt16 addSignedOffset(UInt8 offset) {
        if (offset == null) {
            throw new IllegalArgumentException("offset cannot be null");
        }
        var signedOffset = (byte) offset.value();
        return new UInt16((this.value() + signedOffset) & 0xFFFF);
    }

    /**
     * Truncates this 16-bit value down to its lower 8 bits.
     *
     * @return A new {@link UInt8} instance containing the lower byte.
     */
    public UInt8 toUInt8() {
        return new UInt8(value & 0xFF);
    }

    public boolean isGreaterThan(UInt8 other) {
        return value > other.value();
    }
}
