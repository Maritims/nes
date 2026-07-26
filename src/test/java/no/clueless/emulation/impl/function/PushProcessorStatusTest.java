package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.Cpu6502Impl;
import no.clueless.emulation.util.ResolvedAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class PushProcessorStatusTest {

    Cpu6502             cpu;
    PushProcessorStatus php;

    @BeforeEach
    void setUp() {
        cpu = mock(Cpu6502Impl.class);
        php = new PushProcessorStatus();
    }

    @Test
    void pushes_the_status_register_onto_the_stack_including_the_break_flag() {
        when(cpu.getStatusRegister()).thenReturn(Cpu6502.Flag.NEGATIVE.getValue() | Cpu6502.Flag.ZERO.getValue());
        php.execute(cpu, new ResolvedAddress(0x1234, false));

        verify(cpu).getStatusRegister();
        verify(cpu).pushToStack(Cpu6502.Flag.NEGATIVE.getValue() | Cpu6502.Flag.ZERO.getValue() | Cpu6502.Flag.BREAK.getValue());
    }
}