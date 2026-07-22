package no.clueless.emulation.cpu;

import no.clueless.emulation.types.UnsignedByte;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class StatusRegisterTest {
    @Test
    void constructor_should_throw_exception_on_null_flags() {
        assertThrows(IllegalArgumentException.class, () -> new StatusRegister(null));
    }

    @Test
    void constructor_should_not_throw_when_flags_are_empty() {
        assertDoesNotThrow(() -> new StatusRegister());
    }

    @Test
    void constructor_should_not_throw_when_flags_are_not_empty() {
        assertDoesNotThrow(() -> new StatusRegister(Set.of(Flag.Five)));
    }

    @Test
    void clearFlag_should_throw_when_argument_is_null() {
        var statusRegister = new StatusRegister();
        assertThrows(IllegalArgumentException.class, () -> statusRegister.clearFlag(null));
    }

    @Test
    void clearFlag_should_throw_when_argument_is_empty() {
        var statusRegister = new StatusRegister();
        assertThrows(IllegalArgumentException.class, statusRegister::clearFlag);
    }

    @Test
    void clearFlag_should_clear_flag_when_argument_is_not_null() {
        var statusRegister = new StatusRegister();
        statusRegister.setFlag(Flag.Five);
        statusRegister.clearFlag(Flag.Five);
        assertFalse(statusRegister.hasFlag(Flag.Five));
    }

    @Test
    void clearAllFlags_should_clear_all_flags() {
        var statusRegister = new StatusRegister();
        statusRegister.setFlag(Flag.Five);
        statusRegister.setFlag(Flag.Negative);
        statusRegister.clearAllFlags();
    }

    @Test
    void clearFlag_should_be_idempotent() {
        var statusRegister = new StatusRegister();
        statusRegister.clearFlag(Flag.Five);
        statusRegister.clearFlag(Flag.Five);
        assertFalse(statusRegister.hasFlag(Flag.Five));
    }

    @Test
    void fromByte_should_throw_when_argument_is_null() {
        assertThrows(IllegalArgumentException.class, () -> StatusRegister.fromByte(null));
    }

    @Test
    void fromByte_should_succeed_when_argument_is_not_null() {
        // arrange
        var unsignedByte = new UnsignedByte(0x80 | 0x40);
        var expected     = new StatusRegister(Set.of(Flag.Negative, Flag.Overflow));

        // act
        var actual = StatusRegister.fromByte(unsignedByte);

        // assert
        assertEquals(expected, actual);
    }

    @Test
    void hasFlag_should_throw_when_argument_is_null() {
        var statusRegister = new StatusRegister();
        assertThrows(IllegalArgumentException.class, () -> statusRegister.hasFlag(null));
    }

    @Test
    void setFlag_should_throw_when_argument_is_empty() {
        var statusRegister = new StatusRegister();
        assertThrows(IllegalArgumentException.class, statusRegister::setFlag);
    }

    @Test
    void setFlag_should_throw_when_argument_is_null() {
        var statusRegister = new StatusRegister();
        assertThrows(IllegalArgumentException.class, () -> statusRegister.setFlag(null));
    }

    @Test
    void setFlag_should_set_flag_when_argument_is_not_null() {
        var statusRegister = new StatusRegister();
        statusRegister.setFlag(Flag.Five);
        assertTrue(statusRegister.hasFlag(Flag.Five));
    }

    @Test
    void setFlag_should_be_idempotent() {
        var statusRegister = new StatusRegister();
        statusRegister.setFlag(Flag.Five);
        statusRegister.setFlag(Flag.Five);
        assertTrue(statusRegister.hasFlag(Flag.Five));
    }

    @Test
    void setFlag_should_be_cumulative() {
        var statusRegister = new StatusRegister();
        statusRegister.setFlag(Flag.Carry);
        statusRegister.setFlag(Flag.Decimal);
        assertTrue(statusRegister.hasFlag(Flag.Carry));
        assertTrue(statusRegister.hasFlag(Flag.Decimal));
    }

    @Test
    void updateNegativeAndZero_should_throw_when_argument_is_null() {
        var statusRegister = new StatusRegister();
        assertThrows(IllegalArgumentException.class, () -> statusRegister.updateNegativeAndZero(null));
    }

    @Test
    void update_should_throw_when_argument_is_null() {
        var statusRegister = new StatusRegister();
        assertThrows(IllegalArgumentException.class, () -> statusRegister.update(null));
    }
}