package no.clueless.emulation.types;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnsignedWordTest {

    @Test
    void subtract16_should_handle_regular_subtraction_without_wraparound() {
        // arrange
        var value1   = new UnsignedWord(0x1234);
        var value2   = new UnsignedWord(0x0024);
        var expected = new UnsignedWord(0x1210);

        // act
        var result = value1.subtract16(value2);

        // assert
        assertEquals(expected, result, "Subtracted result should correctly be 0x1210");
    }

    @Test
    void subtract16_should_wrap_around_on_underflow() {
        // arrange
        var value1   = new UnsignedWord(0x0000);
        var value2   = new UnsignedWord(0x0001);
        var expected = new UnsignedWord(0xFFFF);

        // act
        var result = value1.subtract16(value2);

        // assert
        assertEquals(expected, result, "Subtracted result should wrap around to 0xFFFF on underflow");
    }

    @Test
    void subtract16_should_throw_exception_for_null_argument() {
        // arrange
        var value1 = new UnsignedWord(0x1234);

        // act and assert
        assertThrows(IllegalArgumentException.class, () -> value1.subtract16(null), "Should throw exception when argument is null");
    }

    @Test
    void shiftLeft_should_shift_correctly() {
        // arrange
        var value = new UnsignedWord(0b01010101_10010011); // Binary representation

        // act
        var shifted1 = value.shiftLeft(1);
        var shifted8 = value.shiftLeft(8);

        // assert
        assertEquals(0b10101011_00100110, shifted1.value(), "Shift left by 1 should correctly shift all bits left by 1");
        assertEquals(0b10010011_00000000, shifted8.value(), "Shift left by 8 should correctly shift all bits left by 8");
    }

    @Test
    void shiftLeft_should_return_zero_for_large_shifts() {
        // arrange
        var value = new UnsignedWord(0b01010101_10010011); // Binary representation

        // act
        var shifted = value.shiftLeft(16);

        // assert
        assertEquals(0, shifted.value(), "Shift left by 16 or more should result in 0");
    }

    @Test
    void shiftLeft_should_throw_exception_on_negative_shift() {
        // arrange
        var value = new UnsignedWord(0b01010101_10010011);

        // act and assert
        assertThrows(IllegalArgumentException.class, () -> value.shiftLeft(-1), "Negative shifts should throw IllegalArgumentException");
    }

    @Test
    void isGreaterThan_should_return_true_when_value_is_greater_than_other() {
        // arrange
        var value = new UnsignedWord(300);
        var other = new UnsignedByte(200);

        // act
        var result = value.isGreaterThan(other);

        // assert
        assertTrue(result, "UInt16 value should be greater than UInt8 value");
    }

    @Test
    void isGreaterThan_should_return_false_when_value_is_less_than_or_equal_to_other() {
        // arrange
        var value1 = new UnsignedWord(100);
        var other1 = new UnsignedByte(200);
        var value2 = new UnsignedWord(200);
        var other2 = new UnsignedByte(200);

        // act
        var result1 = value1.isGreaterThan(other1);
        var result2 = value2.isGreaterThan(other2);

        // assert
        assertFalse(result1, "UInt16 value should not be greater when less than UInt8 value");
        assertFalse(result2, "UInt16 value should not be greater when equal to UInt8 value");
    }

    @Test
    void isGreaterThan_should_throw_exception_on_null_argument() {
        // arrange
        var value = new UnsignedWord(100);

        // act and assert
        //noinspection DataFlowIssue
        assertThrows(NullPointerException.class, () -> value.isGreaterThan(null), "Should throw exception when argument is null");
    }

    @Test
    void toUInt8_should_return_least_significant_byte() {
        // arrange
        var value    = new UnsignedWord(0x12AB); // 0xAB is the least significant byte
        var expected = new UnsignedByte(0xAB);

        // act
        var actual = value.toUInt8();

        // assert
        assertEquals(expected, actual, "toUInt8 should return the correct least significant byte");
    }

    @Test
    void toUInt8_should_return_zero_for_zero_value() {
        // arrange
        var value    = new UnsignedWord(0x0000); // least significant byte is 0x00
        var expected = new UnsignedByte(0x00);

        // act
        var actual = value.toUInt8();

        // assert
        assertEquals(expected, actual, "toUInt8 should return 0x00 for a UInt16 value of 0x0000");
    }

    @Test
    void toUInt8_should_return_max_byte_for_max_value() {
        // arrange
        var value    = new UnsignedWord(0xFFFF); // least significant byte is 0xFF
        var expected = new UnsignedByte(0xFF);

        // act
        var actual = value.toUInt8();

        // assert
        assertEquals(expected, actual, "toUInt8 should return 0xFF for a UInt16 value of 0xFFFF");
    }

    @Test
    void isBitSet_should_return_true_for_set_bit() {
        // arrange
        var value = new UnsignedWord(0b1011); // Binary representation: 0b1011

        // act and assert
        assertTrue(value.isBitSet(0), "Bit 0 should be set (value = 0b1011)");
        assertTrue(value.isBitSet(1), "Bit 1 should be set (value = 0b1011)");
        assertFalse(value.isBitSet(2), "Bit 2 should not be set (value = 0b1011)");
        assertTrue(value.isBitSet(3), "Bit 3 should be set (value = 0b1011)");
    }

    @Test
    void isBitSet_should_return_false_for_unset_bit() {
        // arrange
        var value = new UnsignedWord(0b0100); // Binary representation: 0b0100

        // act and assert
        assertFalse(value.isBitSet(0), "Bit 0 should not be set (value = 0b0100)");
        assertFalse(value.isBitSet(1), "Bit 1 should not be set (value = 0b0100)");
        assertTrue(value.isBitSet(2), "Bit 2 should be set (value = 0b0100)");
        assertFalse(value.isBitSet(3), "Bit 3 should not be set (value = 0b0100)");
    }

    @Test
    void isBitSet_should_throw_exception_for_bit_out_of_range() {
        // arrange
        var value = new UnsignedWord(0);

        // act and assert
        assertThrows(IllegalArgumentException.class, () -> value.isBitSet(-1), "Should throw exception for negative bit index");
        assertThrows(IllegalArgumentException.class, () -> value.isBitSet(16), "Should throw exception for bit index out of range");
    }

    @Test
    void constructor_should_throw_exception_on_overflow() {
        assertThrows(IllegalArgumentException.class, () -> new UnsignedWord(0xFFFF + 1));
    }

    @Test
    void constructor_should_throw_exception_on_underflow() {
        assertThrows(IllegalArgumentException.class, () -> new UnsignedWord(-1));
    }

    @Test
    void constructor_should_accept_valid_values() {
        assertDoesNotThrow(() -> new UnsignedWord(0x0000));
        assertDoesNotThrow(() -> new UnsignedWord(0xFFFF));
    }

    @Test
    void increment_should_wrap_around_on_overflow() {
        var value       = new UnsignedWord(0xFFFF);
        var incremented = value.increment();
        assertEquals(0x0000, incremented.value(), "Incremented value should be 0x0000");
    }

    @Test
    void decrement_should_wrap_around_on_underflow() {
        var value       = new UnsignedWord(0x0000);
        var decremented = value.decrement();
        assertEquals(0xFFFF, decremented.value(), "Decremented value should be 0xFFFF");
    }

    @Test
    void fromBytes_should_combine_low_and_high_bytes_correctly() {
        // arrange
        var high     = new UnsignedByte(0x12);
        var low      = new UnsignedByte(0x34);
        var expected = new UnsignedWord(0x1234);

        // act
        var actual = UnsignedWord.fromBytes(low, high);

        // assert
        assertEquals(expected, actual, "Combined bytes should equal expected value");
    }

    @Test
    void fromBytes_should_throw_exception_on_null_arguments() {
        assertThrows(IllegalArgumentException.class, () -> UnsignedWord.fromBytes(null, null));
        assertThrows(IllegalArgumentException.class, () -> UnsignedWord.fromBytes(new UnsignedByte(0x00), null));
        assertThrows(IllegalArgumentException.class, () -> UnsignedWord.fromBytes(null, new UnsignedByte(0x00)));
    }

    @Test
    void fromBytes_should_handle_maximum_boundaries() {
        // arrange
        var high     = new UnsignedByte(0xFF);
        var low      = new UnsignedByte(0xFF);
        var expected = new UnsignedWord(0xFFFF);

        // act
        var actual = UnsignedWord.fromBytes(low, high);

        // assert
        assertEquals(expected, actual);
    }

    @Test
    void add_8_should_wrap_around_on_overflow() {
        var value       = new UnsignedWord(0xFFFF);
        var incremented = value.add8(new UnsignedByte(1));
        assertEquals(0x0000, incremented.value(), "Incremented value should be 0x0000");
    }

    @Test
    void subtract8_should_correctly_subtract_without_wraparound() {
        var value      = new UnsignedWord(0x1000);
        var subtracted = value.subtract8(new UnsignedByte(0x20));
        assertEquals(0x0FE0, subtracted.value(), "Subtracted value should be 0x0FE0");
    }

    @Test
    void subtract8_should_wrap_around_on_underflow() {
        var value      = new UnsignedWord(0x0010);
        var subtracted = value.subtract8(new UnsignedByte(0x20));
        assertEquals(0xFFF0, subtracted.value(), "Subtracted value should wrap around to 0xFFF0");
    }

    @Test
    void subtract8_should_throw_exception_on_null_argument() {
        assertThrows(IllegalArgumentException.class, () -> new UnsignedWord(0x0000).subtract8(null), "Subtracting null should throw IllegalArgumentException");
    }

    @Test
    void add_8_should_throw_exception_on_null_argument() {
        assertThrows(IllegalArgumentException.class, () -> new UnsignedWord(0x0000).add8(null));
    }

    @Test
    void add_should_add_8_correctly() {
        var value       = new UnsignedWord(0x1000);
        var incremented = value.add8(new UnsignedByte(0x20));
        assertEquals(0x1020, incremented.value(), "Incremented value should be 0x1020");
    }

    @Test
    void addSignedOffset_should_handle_positive_offsets() {
        // arrange
        var base     = new UnsignedWord(0x1000);
        var offset   = new UnsignedByte(0x05);
        var expected = new UnsignedWord(0x1005);

        // act
        var actual = base.addSignedOffset(offset);

        // assert
        assertEquals(expected, actual);
    }

    @Test
    void addSignedOffset_should_handle_negative_offsets() {
        // arrange
        var base     = new UnsignedWord(0x1000);
        var offset   = new UnsignedByte(0xFA); // 250 unsigned, which is -6 signed.
        var expected = new UnsignedWord(0x0FFA);

        // act
        var actual = base.addSignedOffset(offset);

        // assert
        assertEquals(expected, actual, "Should jump backward by 6 (0x1000 - 6 = 0x0FFA");
    }

    @Test
    void addSignedOffset_should_wrap_around_on_underflow() {
        // arrange
        var base     = new UnsignedWord(0x0002);
        var offset   = new UnsignedByte(0xFB); // -5 signed.
        var expected = new UnsignedWord(0xFFFD);

        // act
        var actual = base.addSignedOffset(offset);

        // assert
        assertEquals(expected, actual, "Should wrap around to 0xFFFD");
    }

    @Test
    void addSignedOffset_should_throw_on_null_parameter() {
        assertThrows(IllegalArgumentException.class, () -> new UnsignedWord(0x0000).addSignedOffset(null));
    }

    @Test
    void add16_should_add_without_overflow() {
        // arrange
        var value1   = new UnsignedWord(0x1234);
        var value2   = new UnsignedWord(0x4321);
        var expected = new UnsignedWord(0x5555);

        // act
        var result = value1.add16(value2);

        // assert
        assertEquals(expected, result, "Sum should be 0x5555 without overflow");
    }

    @Test
    void add16_should_wrap_around_on_overflow() {
        // arrange
        var value1   = new UnsignedWord(0xFFFF);
        var value2   = new UnsignedWord(0x0001);
        var expected = new UnsignedWord(0x0000);

        // act
        var result = value1.add16(value2);

        // assert
        assertEquals(expected, result, "Sum should wrap around to 0x0000 on overflow");
    }

    @Test
    void add16_should_throw_exception_on_null_argument() {
        // arrange
        var value1 = new UnsignedWord(0x1234);

        // act and assert
        assertThrows(IllegalArgumentException.class, () -> value1.add16(null), "Should throw exception when argument is null");
    }

    @Test
    void factory_should_create_new_instance_with_valid_value() {
        // arrange
        var factory = new UnsignedWord(0).factory();

        // act
        var result = factory.apply(300);

        // assert
        assertNotNull(result, "Factory should create a non-null UInt16 instance");
        assertEquals(300, result.value(), "Factory should create a UInt16 with the correct value");
    }

    @Test
    void factory_should_throw_exception_for_invalid_value() {
        // arrange
        var factory = new UnsignedWord(0).factory();

        // act and assert
        assertThrows(IllegalArgumentException.class, () -> factory.apply(-1), "Factory should throw an exception for negative values");
        assertThrows(IllegalArgumentException.class, () -> factory.apply(65536), "Factory should throw an exception for values exceeding 65535");
    }

    @Test
    void shiftRight_should_shift_correctly() {
        // arrange
        var value = new UnsignedWord(0b10010011_01010101); // Binary representation

        // act
        var shifted1 = value.shiftRight(1);
        var shifted8 = value.shiftRight(8);

        // assert
        assertEquals(0b01001001_10101010, shifted1.value(), "Shift right by 1 should move all bits to the right by 1");
        assertEquals(0b00000000_10010011, shifted8.value(), "Shift right by 8 should move all bits to the right by 8");
    }

    @Test
    void shiftRight_should_return_zero_for_large_shifts() {
        // arrange
        var value = new UnsignedWord(0b10010011_01010101); // Binary representation

        // act
        var shifted = value.shiftRight(16);

        // assert
        assertEquals(0, shifted.value(), "Shift right by 16 or more should result in 0");
    }

    @Test
    void shiftRight_should_throw_exception_on_negative_shift() {
        // arrange
        var value = new UnsignedWord(0b10010011_01010101);

        // act and assert
        assertThrows(IllegalArgumentException.class, () -> value.shiftRight(-1), "Negative shifts should throw IllegalArgumentException");
    }
}