package no.clueless.emulation.types;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UIntTest {

    @Test
    void and_should_perform_bitwise_and_with_non_zero_values() {
        // arrange
        var value1   = new UInt8(0b10101010); // 170 in decimal
        var value2   = new UInt8(0b11001100); // 204 in decimal
        var expected = new UInt8(0b10001000); // 136 in decimal

        // act
        var actual = value1.and(value2);

        // assert
        assertEquals(expected, actual, "Bitwise AND should correctly combine non-zero UInt8 values.");
    }

    @Test
    void and_should_perform_bitwise_and_with_zero_value() {
        // arrange
        var value1   = new UInt8(0b10101010); // 170 in decimal
        var value2   = UInt8.ZERO;           // 0 in decimal
        var expected = UInt8.ZERO;

        // act
        var actual = value1.and(value2);

        // assert
        assertEquals(expected, actual, "Bitwise AND with zero should always return zero.");
    }

    /**
     * Test description:
     * This method verifies that the `and` operation between two UInt8 values returns the same value
     * when the second value has all bits set to 1 (acts as a mask).
     */
    @Test
    void and_should_perform_bitwise_and_with_max_value() {
        UInt8 value1 = new UInt8(0b10101010); // 170 in decimal
        UInt8 value2 = UInt8.MAX_VALUE;       // 255 in decimal
        UInt8 result = value1.and(value2);

        assertEquals(value1.value(), result.value(), "Bitwise AND with UInt8.MAX_VALUE should return the original value.");
    }

    @Test
    void and_should_throw_with_null_value() {
        var value1 = new UInt8(0b10101010); // 170 in decimal

        assertThrows(IllegalArgumentException.class,
                () -> value1.and(null),
                "Bitwise AND with a null value should throw an IllegalArgumentException.");
    }

    @Test
    void and_should_perform_bitwise_and_with_only_zeroes() {
        // arrange
        var value1   = UInt8.ZERO;
        var value2   = UInt8.ZERO;
        var expected = UInt8.ZERO;

        // act
        var result = value1.and(value2);

        // assert
        assertEquals(expected, result, "Bitwise AND of two zeros should return zero.");
    }

    @Test
    void and_should_perform_bitwise_and_with_only_single_bits() {
        // arrange
        var value1   = new UInt8(0b00000001); // 1 in decimal
        var value2   = new UInt8(0b00000001); // 1 in decimal
        var expected = UInt8.ONE;

        // act
        var result = value1.and(value2);

        // assert
        assertEquals(expected, result, "Bitwise AND of single-bit UInt8 values should return the single bit set.");
    }

    @Test
    void or_should_perform_bitwise_or_with_non_zero_values() {
        // arrange
        var value1   = new UInt8(0b10101010); // 170 in decimal
        var value2   = new UInt8(0b11001100); // 204 in decimal
        var expected = new UInt8(0b11101110); // 238 in decimal

        // act
        var actual = value1.or(value2);

        // assert
        assertEquals(expected, actual, "Bitwise OR should correctly combine non-zero UInt8 values.");
    }

    @Test
    void or_should_perform_bitwise_or_with_zero_value() {
        // arrange
        var value1 = new UInt8(0b10101010); // 170 in decimal
        var value2 = UInt8.ZERO;           // 0 in decimal

        // act
        var actual = value1.or(value2);

        // assert
        assertEquals(value1, actual, "Bitwise OR with zero should return the original value.");
    }

    @Test
    void or_should_perform_bitwise_or_with_max_value() {
        // arrange
        UInt8 value1   = new UInt8(0b10101010); // 170 in decimal
        UInt8 value2   = UInt8.MAX_VALUE;       // 255 in decimal
        UInt8 expected = UInt8.MAX_VALUE;

        // act
        var actual = value1.or(value2);

        // assert
        assertEquals(expected, actual, "Bitwise OR with UInt8.MAX_VALUE should return MAX_VALUE.");
    }

    @Test
    void or_should_perform_bitwise_or_with_only_zeroes() {
        // arrange
        var value1   = UInt8.ZERO;
        var value2   = UInt8.ZERO;
        var expected = UInt8.ZERO;

        // act
        var actual = value1.or(value2);

        // assert
        assertEquals(expected, actual, "Bitwise OR of two zeros should return zero.");
    }

    @Test
    void or_should_perform_bitwise_or_with_only_single_bits() {
        // arrange
        var value1   = new UInt8(0b00000001); // 1 in decimal
        var value2   = new UInt8(0b00000001); // 1 in decimal
        var expected = UInt8.ONE;

        // act
        var actual = value1.or(value2);

        // assert
        assertEquals(expected, actual, "Bitwise OR of single-bit UInt8 values should return the single bit set.");
    }

    @Test
    void or_should_throw_with_null_value() {
        var value1 = new UInt8(0b10101010); // 170 in decimal

        assertThrows(IllegalArgumentException.class,
                () -> value1.or(null),
                "Bitwise OR with a null value should throw an IllegalArgumentException.");
    }

    @Test
    void xor_should_perform_bitwise_xor_with_non_zero_values() {
        // arrange
        var value1   = new UInt8(0b10101010); // 170 in decimal
        var value2   = new UInt8(0b11001100); // 204 in decimal
        var expected = new UInt8(0b01100110); // 102 in decimal

        // act
        var actual = value1.xor(value2);

        // assert
        assertEquals(expected, actual, "Bitwise XOR should correctly combine non-zero UInt8 values.");
    }

    @Test
    void xor_should_perform_bitwise_xor_with_zero_value() {
        // arrange
        var value1   = new UInt8(0b10101010); // 170 in decimal
        var value2   = UInt8.ZERO;           // 0 in decimal

        // act
        var actual = value1.xor(value2);

        // assert
        assertEquals(value1, actual, "Bitwise XOR with zero should return the original value.");
    }

    @Test
    void xor_should_perform_bitwise_xor_with_max_value() {
        // arrange
        UInt8 value1   = new UInt8(0b10101010); // 170 in decimal
        UInt8 value2   = UInt8.MAX_VALUE;       // 255 in decimal
        UInt8 expected = new UInt8(0b01010101); // 85 in decimal

        // act
        var actual = value1.xor(value2);

        // assert
        assertEquals(expected, actual, "Bitwise XOR with UInt8.MAX_VALUE should return the inverted value.");
    }

    @Test
    void xor_should_perform_bitwise_xor_with_only_zeroes() {
        // arrange
        var value1   = UInt8.ZERO;
        var value2   = UInt8.ZERO;
        var expected = UInt8.ZERO;

        // act
        var actual = value1.xor(value2);

        // assert
        assertEquals(expected, actual, "Bitwise XOR of two zeros should return zero.");
    }

    @Test
    void xor_should_perform_bitwise_xor_with_only_single_bits() {
        // arrange
        var value1   = new UInt8(0b00000001); // 1 in decimal
        var value2   = new UInt8(0b00000001); // 1 in decimal
        var expected = UInt8.ZERO;

        // act
        var actual = value1.xor(value2);

        // assert
        assertEquals(expected, actual, "Bitwise XOR of single-bit UInt8 values should return zero.");
    }

    @Test
    void xor_should_throw_with_null_value() {
        var value1 = new UInt8(0b10101010); // 170 in decimal

        assertThrows(IllegalArgumentException.class,
                () -> value1.xor(null),
                "Bitwise XOR with a null value should throw an IllegalArgumentException.");
    }
}