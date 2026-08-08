package no.clueless.emulation.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ByteFlipperTest {

    @Test
    void flip() {
        assertEquals(0b00000111, ByteFlipper.flip(0b11100000));
    }
}