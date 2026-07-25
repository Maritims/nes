package no.clueless.emulation.impl.function;

import no.clueless.emulation.Bus;
import no.clueless.emulation.Cpu6502;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ImmediateTest {

    Cpu6502   cpu;
    Bus       bus;
    Immediate immediate;

    @BeforeEach
    void setUp() {
        cpu       = mock(Cpu6502.class);
        bus       = mock(Bus.class);
        immediate = new Immediate();
    }

    @Test
    void fetch_PC_and_increment() {
        immediate.resolve(cpu, bus);
        verify(cpu).getAndIncrementProgramCounter();
    }
}