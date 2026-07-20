package no.clueless.emulation.types;

import org.junit.jupiter.api.Test;

import java.util.function.IntFunction;

import static org.junit.jupiter.api.Assertions.*;

class UInt8Test {

    @Test
    void constructor_should_throw_exception_on_overflow() {
        assertThrows(IllegalArgumentException.class, () -> new UInt8(0xFF + 1));
    }

    @Test
    void constructor_should_throw_exception_on_underflow() {
        assertThrows(IllegalArgumentException.class, () -> new UInt8(-1));
    }

    @Test
    void constructor_should_accept_valid_values() {
        assertDoesNotThrow(() -> new UInt8(0x00));
        assertDoesNotThrow(() -> new UInt8(0xFF));
    }

    @Test
    void increment_should_wrap_around_on_overflow() {
        var value       = new UInt8(0xFF);
        var incremented = value.increment();
        assertEquals(0x00, incremented.value(), "Incremented value should be 0x00");
    }

    @Test
    void decrement_should_wrap_around_on_underflow() {
        var value       = new UInt8(0x00);
        var decremented = value.decrement();
        assertEquals(0xFF, decremented.value(), "Decremented value should be 0xFF");
    }

    @Test
    void toUInt16_should_return_correct_value() {
        var value    = new UInt8(0x12);
        var expected = new UInt16(0x12);
        assertEquals(expected, value.toUInt16(), "Widened UInt16 should retain the original value");
    }

    @Test
    void add_should_wrap_around_on_overflow() {
        var value       = new UInt8(0xFF);
        var incremented = value.add(new UInt8(1));
        assertEquals(0x00, incremented.value(), "Incremented value should be 0x00");
    }

    @Test
    void add_should_throw_exception_on_null_argument() {
        assertThrows(IllegalArgumentException.class, () -> new UInt8(0x00).add(null));
    }

    @Test
    void isBitSet_should_return_true_for_set_bits() {
        var value = new UInt8(0b10101010); // 170 in decimal
        assertTrue(value.isBitSet(1), "Bit 1 should be set");
        assertTrue(value.isBitSet(3), "Bit 3 should be set");
        assertTrue(value.isBitSet(5), "Bit 5 should be set");
        assertTrue(value.isBitSet(7), "Bit 7 should be set");
    }

    @Test
    void isBitSet_should_return_false_for_unset_bits() {
        var value = new UInt8(0b10101010); // 170 in decimal
        assertFalse(value.isBitSet(0), "Bit 0 should not be set");
        assertFalse(value.isBitSet(2), "Bit 2 should not be set");
        assertFalse(value.isBitSet(4), "Bit 4 should not be set");
        assertFalse(value.isBitSet(6), "Bit 6 should not be set");
    }

    @Test
    void isBitSet_should_throw_exception_for_negative_bit_indices() {
        var value = new UInt8(0xFF); // 255 in decimal
        assertThrows(IllegalArgumentException.class, () -> value.isBitSet(-1), "Should throw exception for negative bit index");
    }

    @Test
    void isBitSet_should_throw_exception_for_out_of_bound_bit_indices() {
        var value = new UInt8(0x00); // 0 in decimal
        assertThrows(IllegalArgumentException.class, () -> value.isBitSet(8), "Should throw exception for bit index > 7");
    }

    @Test
    void factory_should_create_new_instance_with_valid_value() {
        IntFunction<UInt8> factory = UInt8.ZERO.factory();
        UInt8              created = factory.apply(100);
        assertEquals(100, created.value(), "Factory should create a UInt8 with the correct value");
    }

    @Test
    void factory_should_throw_exception_on_invalid_value() {
        IntFunction<UInt8> factory = UInt8.ZERO.factory();
        assertThrows(IllegalArgumentException.class, () -> factory.apply(-1), "Factory should throw for negative value");
        assertThrows(IllegalArgumentException.class, () -> factory.apply(256), "Factory should throw for value > 255");
    }

    @Test
    void subtract_should_wrap_around_on_underflow() {
        var value      = new UInt8(0x00);
        var subtracted = value.subtract(new UInt8(1));
        assertEquals(0xFF, subtracted.value(), "Subtracted value should wrap to 0xFF on underflow");
    }

    @Test
    void subtract_should_return_correct_value_on_valid_input() {
        var value      = new UInt8(0x10); // 16 in decimal
        var subtracted = value.subtract(new UInt8(0x05)); // Subtract 5
        assertEquals(0x0B, subtracted.value(), "Subtracted value should be correct for valid input");
    }

    @Test
    void subtract_should_throw_exception_on_null_argument() {
        assertThrows(IllegalArgumentException.class, () -> new UInt8(0x00).subtract(null), "Subtract method should throw on null argument");
    }

    @Test
    void isGreaterThanOrEqualTo_should_return_true_for_equal_values() {
        var value1 = new UInt8(0x10); // 16
        var value2 = new UInt8(0x10); // 16
        assertTrue(value1.isGreaterThanOrEqualTo(value2), "Equal values should return true for isGreaterThanOrEqualTo");
    }

    @Test
    void isGreaterThanOrEqualTo_should_return_true_for_larger_values() {
        var value1 = new UInt8(0x20); // 32
        var value2 = new UInt8(0x10); // 16
        assertTrue(value1.isGreaterThanOrEqualTo(value2), "Larger value1 should return true for isGreaterThanOrEqualTo");
    }

    @Test
    void isGreaterThanOrEqualTo_should_return_false_for_smaller_values() {
        var value1 = new UInt8(0x10); // 16
        var value2 = new UInt8(0x20); // 32
        assertFalse(value1.isGreaterThanOrEqualTo(value2), "Smaller value1 should return false for isGreaterThanOrEqualTo");
    }

    @Test
    void isGreaterThanOrEqualTo_should_throw_exception_on_null_argument() {
        var value = new UInt8(0x10); // 16
        assertThrows(IllegalArgumentException.class, () -> value.isGreaterThanOrEqualTo(null), "Passing null should throw an exception");
    }

    @Test
    void shiftLeft_should_shift_bits_left_by_one_position() {
        // arrange
        var value    = new UInt8(0b00001111); // 15 in decimal
        var expected = new UInt8(0b00011110); // 30 in decimal

        // act
        var result = value.shiftLeft(1);

        // assert
        assertEquals(expected, result, "Shifting bits left by 1 should double the value.");
    }

    @Test
    void shiftLeft_should_shift_bits_left_by_several_positions() {
        // arrange
        var value    = new UInt8(0b00000001); // 1 in decimal
        var expected = new UInt8(0b01000000); // 64 in decimal

        // act
        var result = value.shiftLeft(6);

        // assert
        assertEquals(expected, result, "Shifting bits left by multiple positions should work as expected.");
    }

    @Test
    void shiftLeft_should_return_zero_when_shifting_zero() {
        // arrange
        var value = UInt8.ZERO;

        // act
        var result = value.shiftLeft(5);

        // assert
        assertEquals(UInt8.ZERO, result, "Shifting zero by any number of positions should always return zero.");
    }

    @Test
    void shiftLeft_should_throw_with_negative_shift_value() {
        // arrange
        var value = new UInt8(0b00001111); // 15 in decimal

        // assert
        assertThrows(IllegalArgumentException.class,
                () -> value.shiftLeft(-1),
                "Shifting by a negative value should throw an IllegalArgumentException.");
    }

    @Test
    void shiftLeft_should_wrap_to_8_bits() {
        // arrange
        var value = new UInt8(0x80);

        // act
        var result = value.shiftLeft(1);

        // assert
        assertEquals(new UInt8(0x00), result);
    }

    @Test
    void shiftLeft_should_keep_only_lower_8_bits() {
        // arrange
        var value = new UInt8(0xFF);

        // act
        var result = value.shiftLeft(1);

        // assert
        assertEquals(new UInt8(254), result);
    }
}