package no.clueless.emulation.types;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UInt16Test {

    @Test
    void constructor_should_throw_exception_on_overflow() {
        assertThrows(IllegalArgumentException.class, () -> new UInt16(0xFFFF + 1));
    }

    @Test
    void constructor_should_throw_exception_on_underflow() {
        assertThrows(IllegalArgumentException.class, () -> new UInt16(-1));
    }

    @Test
    void constructor_should_accept_valid_values() {
        assertDoesNotThrow(() -> new UInt16(0x0000));
        assertDoesNotThrow(() -> new UInt16(0xFFFF));
    }

    @Test
    void increment_should_wrap_around_on_overflow() {
        var value       = new UInt16(0xFFFF);
        var incremented = value.increment();
        assertEquals(0x0000, incremented.value(), "Incremented value should be 0x0000");
    }

    @Test
    void decrement_should_wrap_around_on_underflow() {
        var value       = new UInt16(0x0000);
        var decremented = value.decrement();
        assertEquals(0xFFFF, decremented.value(), "Decremented value should be 0xFFFF");
    }

    @Test
    void fromBytes_should_combine_low_and_high_bytes_correctly() {
        // arrange
        var high     = new UInt8(0x12);
        var low      = new UInt8(0x34);
        var expected = new UInt16(0x1234);

        // act
        var actual = UInt16.fromBytes(low, high);

        // assert
        assertEquals(expected, actual, "Combined bytes should equal expected value");
    }

    @Test
    void fromBytes_should_throw_exception_on_null_arguments() {
        assertThrows(IllegalArgumentException.class, () -> UInt16.fromBytes(null, null));
        assertThrows(IllegalArgumentException.class, () -> UInt16.fromBytes(new UInt8(0x00), null));
        assertThrows(IllegalArgumentException.class, () -> UInt16.fromBytes(null, new UInt8(0x00)));
    }

    @Test
    void fromBytes_should_handle_maximum_boundaries() {
        // arrange
        var high     = new UInt8(0xFF);
        var low      = new UInt8(0xFF);
        var expected = new UInt16(0xFFFF);

        // act
        var actual = UInt16.fromBytes(low, high);

        // assert
        assertEquals(expected, actual);
    }

    @Test
    void add_8_should_wrap_around_on_overflow() {
        var value       = new UInt16(0xFFFF);
        var incremented = value.add8(new UInt8(1));
        assertEquals(0x0000, incremented.value(), "Incremented value should be 0x0000");
    }

    @Test
    void add_8_should_throw_exception_on_null_argument() {
        assertThrows(IllegalArgumentException.class, () -> new UInt16(0x0000).add8(null));
    }

    @Test
    void add_should_add_8_correctly() {
        var value       = new UInt16(0x1000);
        var incremented = value.add8(new UInt8(0x20));
        assertEquals(0x1020, incremented.value(), "Incremented value should be 0x1020");
    }

    @Test
    void addSignedOffset_should_handle_positive_offsets() {
        // arrange
        var base     = new UInt16(0x1000);
        var offset   = new UInt8(0x05);
        var expected = new UInt16(0x1005);

        // act
        var actual = base.addSignedOffset(offset);

        // assert
        assertEquals(expected, actual);
    }

    @Test
    void addSignedOffset_should_handle_negative_offsets() {
        // arrange
        var base     = new UInt16(0x1000);
        var offset   = new UInt8(0xFA); // 250 unsigned, which is -6 signed.
        var expected = new UInt16(0x0FFA);

        // act
        var actual = base.addSignedOffset(offset);

        // assert
        assertEquals(expected, actual, "Should jump backward by 6 (0x1000 - 6 = 0x0FFA");
    }

    @Test
    void addSignedOffset_should_wrap_around_on_underflow() {
        // arrange
        var base     = new UInt16(0x0002);
        var offset   = new UInt8(0xFB); // -5 signed.
        var expected = new UInt16(0xFFFD);

        // act
        var actual = base.addSignedOffset(offset);

        // assert
        assertEquals(expected, actual, "Should wrap around to 0xFFFD");
    }
}