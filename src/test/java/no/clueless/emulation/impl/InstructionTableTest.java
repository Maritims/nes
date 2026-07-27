package no.clueless.emulation.impl;

import no.clueless.emulation.impl.cpu.InstructionTable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InstructionTableTest {
    @Test
    void should_have_256_elements() {
        assertEquals(256, InstructionTable.INSTRUCTIONS.length);
    }
}