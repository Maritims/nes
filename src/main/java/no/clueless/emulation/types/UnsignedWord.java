package no.clueless.emulation.types;

import java.util.function.IntFunction;

/**
 * Represents an unsigned 16-bit integer.
 *
 * @param value The value of the integer.
 */
public record UnsignedWord(int value) implements UInt<UnsignedWord> {
    /**
     * Constructor.
     *
     * @param value The value of the integer.
     * @throws IllegalArgumentException if the value is not between 0 and 65,535.
     */
    public UnsignedWord {
        if (value < 0 || value > 65535) {
            throw new IllegalArgumentException("Value must be between 0 and 65535");
        }
    }

    public static UnsignedWord ZERO      = new UnsignedWord(0);
    public static UnsignedWord ONE       = new UnsignedWord(1);
    public static UnsignedWord MAX_VALUE = new UnsignedWord(0xFFFF);

    /**
     * Creates a UInt16 from a low and high byte with a Little-Endian layout.
     *
     * @param low  The low byte.
     * @param high The high byte.
     * @return A UInt16 instance.
     * @throws IllegalArgumentException if low or high is null.
     */
    public static UnsignedWord fromBytes(UnsignedByte low, UnsignedByte high) {
        if (low == null) {
            throw new IllegalArgumentException("low cannot be null");
        }
        if (high == null) {
            throw new IllegalArgumentException("high cannot be null");
        }
        return new UnsignedWord((high.intValue() << 8) | low.intValue());
    }

    /**
     * Get the high byte of the UInt16.
     *
     * @return The high byte.
     */
    public UnsignedByte highByte() {
        return new UnsignedByte(value >> 8);
    }

    /**
     * Get the low byte of the UInt16.
     *
     * @return The low byte.
     */
    public UnsignedByte lowByte() {
        return new UnsignedByte(value & 0xFF);
    }

    @Override
    public IntFunction<UnsignedWord> factory() {
        return UnsignedWord::new;
    }

    @Override
    public UnsignedWord increment() {
        return new UnsignedWord((value + 1) & 0xFFFF);
    }

    @Override
    public UnsignedWord decrement() {
        return new UnsignedWord((value - 1) & 0xFFFF);
    }

    @Override
    public boolean isBitSet(int bit) {
        if (bit < 0 || bit > 15) {
            throw new IllegalArgumentException("Bit must be between 0 and 15");
        }
        return (value & (1 << bit)) != 0;
    }

    @Override
    public UnsignedWord shiftLeft(int bits) {
        if (bits < 0) {
            throw new IllegalArgumentException("bits must be non-negative");
        }
        return new UnsignedWord((value << bits) & 0xFFFF);
    }

    @Override
    public UnsignedWord shiftRight(int bits) {
        if (bits < 0) {
            throw new IllegalArgumentException("bits must be non-negative");
        }
        return new UnsignedWord((value >> bits) & 0xFFFF);
    }

    public UnsignedWord add8(UnsignedByte value) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        return new UnsignedWord((this.value() + value.intValue()) & 0xFFFF);
    }

    public UnsignedWord add16(UnsignedWord value) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        return new UnsignedWord((this.value() + value.value()) & 0xFFFF);
    }

    public UnsignedWord subtract8(UnsignedByte value) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        return new UnsignedWord((this.value() - value.intValue()) & 0xFFFF);
    }

    public UnsignedWord subtract16(UnsignedWord value) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        return new UnsignedWord((this.value() - value.value()) & 0xFFFF);
    }

    /**
     * Adds a signed 8-bit two's-complement offset (interpreted as -128 to 127) to this 16-bit value, wrapping naturally at 16 bits.
     *
     * @param offset The unsigned UInt8 offset representing the signed offset.
     * @return The result of the addition.
     * @throws IllegalArgumentException if offset is null.
     */
    public UnsignedWord addSignedOffset(UnsignedByte offset) {
        if (offset == null) {
            throw new IllegalArgumentException("offset cannot be null");
        }
        var signedOffset = offset.byteValue();
        return new UnsignedWord((this.value() + signedOffset) & 0xFFFF);
    }

    /**
     * Truncates this 16-bit value down to its lower 8 bits.
     *
     * @return A new {@link UnsignedByte} instance containing the lower byte.
     */
    public UnsignedByte toUInt8() {
        return new UnsignedByte(value & 0xFF);
    }

    public boolean isGreaterThan(UnsignedByte other) {
        return value > other.intValue();
    }
}
