package no.clueless.emulation.types;

import java.util.Objects;

public class UnsignedWord extends Number implements Comparable<UnsignedWord> {
    private final int value;

    /**
     * Constructor.
     *
     * @param value The value of the integer.
     * @throws IllegalArgumentException if the value is not between 0 and 65,535.
     */
    public UnsignedWord(int value) {
        if (value < 0 || value > 65535) {
            throw new IllegalArgumentException("value must be between 0 and 65535");
        }
        this.value = value;
    }

    public static UnsignedWord ONE = new UnsignedWord(1);

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

    public UnsignedWord addByte(UnsignedByte val) {
        if (val == null) {
            throw new IllegalArgumentException("val cannot be null");
        }
        return new UnsignedWord((this.intValue() + val.intValue()) & 0xFFFF);
    }

    public UnsignedWord add16(UnsignedWord val) {
        if (val == null) {
            throw new IllegalArgumentException("val cannot be null");
        }
        return new UnsignedWord((this.intValue() + val.intValue()) & 0xFFFF);
    }

    public UnsignedWord and(UnsignedWord val) {
        if (val == null) {
            throw new IllegalArgumentException("val cannot be null");
        }
        return new UnsignedWord(value & val.value);
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
        return new UnsignedWord((this.intValue() + signedOffset) & 0xFFFF);
    }

    @Override
    public int compareTo(UnsignedWord o) {
        return Integer.compare(value, o.value);
    }

    public UnsignedWord decrement() {
        return new UnsignedWord((value - 1) & 0xFFFF);
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        UnsignedWord that = (UnsignedWord) object;
        return value == that.value;
    }

    @Override
    public float floatValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    /**
     * Get the high byte of the UInt16.
     *
     * @return The high byte.
     */
    public UnsignedByte highByte() {
        return new UnsignedByte(value >> 8);
    }

    public UnsignedWord increment() {
        return new UnsignedWord((value + 1) & 0xFFFF);
    }

    @Override
    public int intValue() {
        return value;
    }

    public boolean isGreaterThan(UnsignedByte other) {
        return value > other.intValue();
    }

    @Override
    public long longValue() {
        return value;
    }

    /**
     * Get the low byte of the UInt16.
     *
     * @return The low byte.
     */
    public UnsignedByte lowByte() {
        return new UnsignedByte(value & 0xFF);
    }

    public UnsignedWord shiftLeft(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be non-negative");
        }
        return new UnsignedWord((value << n) & 0xFFFF);
    }

    public UnsignedWord shiftRight(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be non-negative");
        }
        return new UnsignedWord((value >> n) & 0xFFFF);
    }

    public UnsignedWord subtract8(UnsignedByte value) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        return new UnsignedWord((this.intValue() - value.intValue()) & 0xFFFF);
    }

    public UnsignedWord subtract16(UnsignedWord value) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        return new UnsignedWord((this.intValue() - value.intValue()) & 0xFFFF);
    }

    public boolean testBit(int bit) {
        if (bit < 0 || bit > 15) {
            throw new IllegalArgumentException("Bit must be between 0 and 15");
        }
        return (value & (1 << bit)) != 0;
    }

    /**
     * Returns true if and only if the high byte of this is equal to the high byte of val.
     */
    public boolean testHighByte(UnsignedWord val) {
        return (value & 0xFF00) == (val.value & 0xFF00);
    }

    /**
     * Returns true if and only if the low byte of this is equal to the low byte of val.
     */
    public boolean testLowByte(UnsignedWord val) {
        return (value & 0x00FF) == (val.value & 0x00FF);
    }

    @Override
    public String toString() {
        return "0x%02X".formatted(value & 0xFFFF);
    }

    /**
     * Truncates this 16-bit value down to its lower 8 bits.
     *
     * @return A new {@link UnsignedByte} instance containing the lower byte.
     */
    public UnsignedByte unsignedByteValue() {
        return new UnsignedByte(value & 0xFF);
    }
}
