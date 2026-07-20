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
}