package no.clueless.emulation.types;

import org.junit.jupiter.api.Test;

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
}