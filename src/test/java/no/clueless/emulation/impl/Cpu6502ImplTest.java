package no.clueless.emulation.impl;

import no.clueless.emulation.Bus;
import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.cpu.Cpu6502Impl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class Cpu6502ImplTest {
    Cpu6502Impl cpu;

    @BeforeEach
    void setUp() {
        cpu = new Cpu6502Impl(false);
    }

    @Test
    void reset() {
        var bus = mock(Bus.class);
        when(bus.read(0xFFFC)).thenReturn(0x34);
        when(bus.read(0xFFFD)).thenReturn(0x12);

        cpu.connectToBus(bus);
        cpu.reset();

        assertEquals(0x1234, cpu.getProgramCounter());
        assertEquals(0, cpu.getAccumulator());
        assertEquals(0, cpu.getX());
        assertEquals(0, cpu.getY());
        assertEquals(0xFD, cpu.getStackPointer());
        assertEquals(Cpu6502.Flag.INTERRUPT_DISABLE.getValue() | Cpu6502.Flag.UNUSED.getValue(), cpu.getStatusRegister());
    }
}