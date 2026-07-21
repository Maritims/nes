package no.clueless.emulation.types;

import org.junit.jupiter.api.Test;

import java.util.function.IntFunction;

import static org.junit.jupiter.api.Assertions.*;

class UnsignedByteTest {

    @Test
    void constructor_should_throw_exception_on_overflow() {
        assertThrows(IllegalArgumentException.class, () -> new UnsignedByte(0xFF + 1));
    }

    @Test
    void constructor_should_throw_exception_on_underflow() {
        assertThrows(IllegalArgumentException.class, () -> new UnsignedByte(-1));
    }

    @Test
    void constructor_should_accept_valid_values() {
        assertDoesNotThrow(() -> new UnsignedByte(0x00));
        assertDoesNotThrow(() -> new UnsignedByte(0xFF));
    }

    @Test
    void add_should_return_correct_value() {
        var value       = new UnsignedByte(0x12);
        var incremented = value.add(new UnsignedByte(0x34));
        assertEquals(new UnsignedByte(0x46), incremented, "Incremented value should be 0x46");
    }

    @Test
    void and_should_perform_bitwise_and_with_non_zero_values() {
        var value1   = new UnsignedByte(0b10101010);
        var value2   = new UnsignedByte(0b11001100);
        var expected = new UnsignedByte(0b10001000);
        var result   = value1.and(value2);
        assertEquals(expected, result, "Bitwise AND should correctly combine non-zero UInt8 values.");
    }

    @Test
    void increment_should_wrap_around_on_overflow() {
        var value       = new UnsignedByte(0xFF);
        var incremented = value.increment();
        assertEquals(UnsignedByte.ZERO, incremented, "Incremented value should be 0x00");
    }

    @Test
    void decrement_should_wrap_around_on_underflow() {
        var value       = new UnsignedByte(0x00);
        var decremented = value.decrement();
        assertEquals(UnsignedByte.MAX_VALUE, decremented, "Decremented value should be 0xFF");
    }

    @Test
    void toUInt16_should_return_correct_value() {
        var value    = new UnsignedByte(0x12);
        var expected = new UnsignedWord(0x12);
        assertEquals(expected, value.unsignedWordValue(), "Widened UInt16 should retain the original value");
    }

    @Test
    void add_should_wrap_around_on_overflow() {
        var value       = new UnsignedByte(0xFF);
        var incremented = value.add(new UnsignedByte(1));
        assertEquals(UnsignedByte.ZERO, incremented, "Incremented value should be 0x00");
    }

    @Test
    void add_should_throw_exception_on_null_argument() {
        assertThrows(IllegalArgumentException.class, () -> new UnsignedByte(0x00).add(null));
    }

    @Test
    void isBitSet_should_return_true_for_set_bits() {
        var value = new UnsignedByte(0b10101010); // 170 in decimal
        assertTrue(value.testBit(1), "Bit 1 should be set");
        assertTrue(value.testBit(3), "Bit 3 should be set");
        assertTrue(value.testBit(5), "Bit 5 should be set");
        assertTrue(value.testBit(7), "Bit 7 should be set");
    }

    @Test
    void isBitSet_should_return_false_for_unset_bits() {
        var value = new UnsignedByte(0b10101010); // 170 in decimal
        assertFalse(value.testBit(0), "Bit 0 should not be set");
        assertFalse(value.testBit(2), "Bit 2 should not be set");
        assertFalse(value.testBit(4), "Bit 4 should not be set");
        assertFalse(value.testBit(6), "Bit 6 should not be set");
    }

    @Test
    void isBitSet_should_throw_exception_for_negative_bit_indices() {
        var value = new UnsignedByte(0xFF); // 255 in decimal
        assertThrows(IllegalArgumentException.class, () -> value.testBit(-1), "Should throw exception for negative bit index");
    }

    @Test
    void isBitSet_should_throw_exception_for_out_of_bound_bit_indices() {
        var value = new UnsignedByte(0x00); // 0 in decimal
        assertThrows(IllegalArgumentException.class, () -> value.testBit(8), "Should throw exception for bit index > 7");
    }

    @Test
    void factory_should_create_new_instance_with_valid_value() {
        IntFunction<UnsignedByte> factory = UnsignedByte.ZERO.factory();
        UnsignedByte              created = factory.apply(100);
        assertEquals(new UnsignedByte(100), created, "Factory should create a UInt8 with the correct value");
    }

    @Test
    void factory_should_throw_exception_on_invalid_value() {
        IntFunction<UnsignedByte> factory = UnsignedByte.ZERO.factory();
        assertThrows(IllegalArgumentException.class, () -> factory.apply(-1), "Factory should throw for negative value");
        assertThrows(IllegalArgumentException.class, () -> factory.apply(256), "Factory should throw for value > 255");
    }

    @Test
    void subtract_should_wrap_around_on_underflow() {
        var value      = new UnsignedByte(0x00);
        var subtracted = value.subtract(new UnsignedByte(1));
        assertEquals(UnsignedByte.MAX_VALUE, subtracted, "Subtracted value should wrap to 0xFF on underflow");
    }

    @Test
    void subtract_should_return_correct_value_on_valid_input() {
        var value      = new UnsignedByte(0x10); // 16 in decimal
        var subtracted = value.subtract(new UnsignedByte(0x05)); // Subtract 5
        assertEquals(new UnsignedByte(0x0B), subtracted, "Subtracted value should be correct for valid input");
    }

    @Test
    void subtract_should_throw_exception_on_null_argument() {
        assertThrows(IllegalArgumentException.class, () -> new UnsignedByte(0x00).subtract(null), "Subtract method should throw on null argument");
    }

    @Test
    void compareTo_should_return_0_for_equal_values() {
        var value1 = new UnsignedByte(0x10); // 16
        var value2 = new UnsignedByte(0x10); // 16
        assertEquals(0, value1.compareTo(value2));
    }

    @Test
    void compareTo_should_return_greater_than_0_for_larger_values() {
        var value1 = new UnsignedByte(0x20); // 32
        var value2 = new UnsignedByte(0x10); // 16
        var actual = value1.compareTo(value2);
        assertTrue(actual > 0, () -> "Larger value1 should return greater than 0 for compareTo, but the result was %d".formatted(actual));
    }

    @Test
    void compareTo_should_return_less_than_0_for_smaller_values() {
        var value1 = new UnsignedByte(0x10); // 16
        var value2 = new UnsignedByte(0x20); // 32
        var actual = value1.compareTo(value2);
        assertTrue(actual < 0, () -> "Smaller value1 should return less than 0 for compareTo, but the result was %d".formatted(actual));
    }

    @Test
    void compareTo_should_throw_exception_on_null_argument() {
        var value = new UnsignedByte(0x10); // 16
        assertThrows(NullPointerException.class, () -> value.compareTo(null), "Passing null should throw an exception");
    }

    @Test
    void shiftLeft_should_shift_bits_left_by_one_position() {
        // arrange
        var value    = new UnsignedByte(0b00001111); // 15 in decimal
        var expected = new UnsignedByte(0b00011110); // 30 in decimal

        // act
        var result = value.shiftLeft(1);

        // assert
        assertEquals(expected, result, "Shifting bits left by 1 should double the value.");
    }

    @Test
    void shiftLeft_should_shift_bits_left_by_several_positions() {
        // arrange
        var value    = new UnsignedByte(0b00000001); // 1 in decimal
        var expected = new UnsignedByte(0b01000000); // 64 in decimal

        // act
        var result = value.shiftLeft(6);

        // assert
        assertEquals(expected, result, "Shifting bits left by multiple positions should work as expected.");
    }

    @Test
    void shiftLeft_should_return_zero_when_shifting_zero() {
        // arrange
        var value = UnsignedByte.ZERO;

        // act
        var result = value.shiftLeft(5);

        // assert
        assertEquals(UnsignedByte.ZERO, result, "Shifting zero by any number of positions should always return zero.");
    }

    @Test
    void shiftLeft_should_throw_with_negative_shift_value() {
        // arrange
        var value = new UnsignedByte(0b00001111); // 15 in decimal

        // assert
        assertThrows(IllegalArgumentException.class,
                () -> value.shiftLeft(-1),
                "Shifting by a negative value should throw an IllegalArgumentException.");
    }

    @Test
    void shiftLeft_should_wrap_to_8_bits() {
        // arrange
        var value = new UnsignedByte(0x80);

        // act
        var result = value.shiftLeft(1);

        // assert
        assertEquals(new UnsignedByte(0x00), result);
    }

    @Test
    void shiftLeft_should_keep_only_lower_8_bits() {
        // arrange
        var value = new UnsignedByte(0xFF);

        // act
        var result = value.shiftLeft(1);

        // assert
        assertEquals(new UnsignedByte(254), result);
    }
}