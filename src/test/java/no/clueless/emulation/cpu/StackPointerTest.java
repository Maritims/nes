package no.clueless.emulation.cpu;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class StackPointerTest {
    @Test
    void constructor_should_throw_when_value_is_null() {
        assertThrows(IllegalArgumentException.class, () -> new StackPointer(null, 1));
    }

    @Test
    void constructor_should_throw_when_offset_is_negative() {
        assertThrows(IllegalArgumentException.class, () -> new StackPointer(mock(), -1));
    }
}