package no.clueless.emulation.types;

import java.util.Objects;

public class UnsignedByte extends Number implements Comparable<UnsignedByte> {
    private final int value;

    public UnsignedByte(int value) {
        if (value < 0 || value > 255) {
            throw new IllegalArgumentException("value must be between 0 and 255");
        }
        this.value = value;
    }

    public UnsignedByte(byte b) {
        this(b & 0xFF);
    }

    public static final UnsignedByte ZERO      = new UnsignedByte(0);
    public static final UnsignedByte ONE       = new UnsignedByte(1);
    public static final UnsignedByte MAX_VALUE = new UnsignedByte(0xFF);

    /**
     * Returns an UnsignedByte whose val is (this + val).
     */
    public UnsignedByte addByte(UnsignedByte val) {
        if (val == null) {
            throw new IllegalArgumentException("val cannot be null");
        }
        return new UnsignedByte((this.value + val.value) & 0xFF);
    }

    public UnsignedByte and(UnsignedByte val) {
        if (val == null) {
            throw new IllegalArgumentException("val cannot be null");
        }
        return new UnsignedByte(value & val.value);
    }

    public byte byteValue() {
        return (byte) value;
    }

    /**
     * Compares this UnsignedByte with the specified UnsignedByte.
     */
    @Override
    public int compareTo(UnsignedByte o) {
        return Integer.compare(value, o.value);
    }

    /**
     * Returns an UnsignedByte whose value is (this - 1).
     */
    public UnsignedByte decrement() {
        return subtract(ONE);
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public float floatValue() {
        return value;
    }

    /**
     * Returns an UnsignedByte whose value is (this + 1).
     */
    public UnsignedByte increment() {
        return addByte(ONE);
    }

    @Override
    public int intValue() {
        return value;
    }

    @Override
    public long longValue() {
        return value;
    }

    public UnsignedByte or(UnsignedByte val) {
        if (val == null) {
            throw new IllegalArgumentException("val cannot be null");
        }
        return new UnsignedByte(value | val.value);
    }

    /**
     * Returns an UnsignedByte whose value is (this value << n).
     */
    public UnsignedByte shiftLeft(int n) {
        if (n < 0 || n > 7) {
            throw new IllegalArgumentException("n must be between 0 and 7");
        }
        return new UnsignedByte((value << n) & 0xFF);
    }

    /**
     * Returns an UnsignedByte whose value is (this value >> n).
     */
    public UnsignedByte shiftRight(int n) {
        if (n < 0 || n > 7) {
            throw new IllegalArgumentException("n must be between 0 and 7");
        }
        return new UnsignedByte((value >> n) & 0xFF);
    }

    /**
     * Returns an UnsignedByte whose value is (this - val).
     */
    public UnsignedByte subtract(UnsignedByte val) {
        if (val == null) {
            throw new IllegalArgumentException("val cannot be null");
        }
        return new UnsignedByte((this.value - val.value) & 0xFF);
    }

    /**
     * Returns true if and only if the designated bit is set.
     */
    public boolean testBit(int bit) {
        if (bit < 0 || bit > 7) {
            throw new IllegalArgumentException("Bit must be between 0 and 7");
        }
        return (value & (1 << bit)) != 0;
    }

    public UnsignedWord unsignedWordValue() {
        return new UnsignedWord(value);
    }

    public UnsignedByte xor(UnsignedByte val) {
        if (val == null) {
            throw new IllegalArgumentException("val cannot be null");
        }
        return new UnsignedByte(value ^ val.value);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        UnsignedByte that = (UnsignedByte) object;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return String.format("%02X", value);
    }
}
