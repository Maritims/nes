package no.clueless.emulation.impl.function;

import no.clueless.emulation.Bus;
import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.cpu.OperandResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AbsoluteWithRegisterTest {

    Cpu6502              cpu;
    Bus                  bus;
    AbsoluteWithRegister absoluteWithRegister;

    @BeforeEach
    void setUp() {
        cpu                  = mock(Cpu6502.class);
        bus                  = mock(Bus.class);
        absoluteWithRegister = new AbsoluteWithRegister(Cpu6502::getX) {
            @Override
            public OperandResult resolve(Cpu6502 cpu, Bus bus) {
                return super.resolve(cpu, bus);
            }
        };
    }

    @Test
    void fetch_effective_address_without_page_crossed() {
        when(cpu.getAndIncrementProgramCounter()).thenReturn(0x1234, 0x1235);
        when(bus.read(0x1234)).thenReturn(0x78);
        when(bus.read(0x1235)).thenReturn(0x56);
        when(cpu.getX()).thenReturn(0x10);

        var result = absoluteWithRegister.resolve(cpu, bus);

        verify(cpu, times(2)).getAndIncrementProgramCounter();
        verify(bus, times(2)).read(anyInt());
        assertEquals(0x5688, result.address(), "Unexpected address: 0x" + Integer.toHexString(result.address()));
        assertFalse(result.isPageCrossed());
    }

    @Test
    void fetch_effective_address_with_page_crossed() {
        when(cpu.getAndIncrementProgramCounter()).thenReturn(0x1234, 0x1235);
        when(bus.read(0x1234)).thenReturn(0x10);
        when(bus.read(0x1235)).thenReturn(0x10);
        when(cpu.getX()).thenReturn(0xF0);

        var result = absoluteWithRegister.resolve(cpu, bus);

        verify(cpu, times(2)).getAndIncrementProgramCounter();
        verify(bus, times(2)).read(anyInt());
        assertEquals(0x1100, result.address(), "Unexpected address: 0x" + Integer.toHexString(result.address()));
        assertTrue(result.isPageCrossed());
    }
}