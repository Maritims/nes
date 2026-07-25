package no.clueless.emulation.impl.function;

import no.clueless.emulation.Bus;
import no.clueless.emulation.Cpu6502;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class AbsoluteTest {

    Cpu6502  cpu;
    Bus      bus;
    Absolute absolute;

    @BeforeEach
    void setUp() {
        cpu      = mock(Cpu6502.class);
        bus      = mock(Bus.class);
        absolute = new Absolute();
    }

    @Test
    void read_two_bytes_from_absolute_address_and_increment_PC_twice() {
        absolute.resolve(cpu, bus);
        verify(cpu, times(2)).getAndIncrementProgramCounter();
        verify(bus, times(2)).read(anyInt());
    }
}