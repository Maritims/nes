package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class BreakTest {

    Cpu6502 cpu;
    Break   sut;

    @BeforeEach
    void setUp() {
        cpu = mock(Cpu6502.class);
        sut = new Break();
    }

    @Test
    void sets_the_interrupt_disable_flag() {
        sut.execute(cpu, Integer.MAX_VALUE);
        verify(cpu).setFlag(Cpu6502.Flag.INTERRUPT_DISABLE, true);
    }

    @Test
    void does_not_clear_the_interrupt_disable_flag() {
        sut.execute(cpu, Integer.MAX_VALUE);
        verify(cpu, never()).setFlag(Cpu6502.Flag.INTERRUPT_DISABLE, false);
    }

    @Test
    void sets_the_break_flag() {
        sut.execute(cpu, Integer.MAX_VALUE);
        verify(cpu).setFlag(Cpu6502.Flag.BREAK, true);
    }

    @Test
    void clears_the_break_flag() {
        sut.execute(cpu, Integer.MAX_VALUE);
        verify(cpu).setFlag(Cpu6502.Flag.BREAK, false);
    }

    @Test
    void pushes_the_program_counter_to_the_stack() {
        when(cpu.getAndIncrementProgramCounter()).thenReturn(0x1234);
        sut.execute(cpu, Integer.MAX_VALUE);

        verify(cpu).pushToStack(0x12, 0x34);
    }

    @Test
    void pushes_the_status_register_to_the_stack() {
        when(cpu.getStatusRegister()).thenReturn(0x12);
        sut.execute(cpu, Integer.MAX_VALUE);

        verify(cpu).pushToStack(0x12);
    }

    @Test
    void jumps_to_a_new_location() {
        when(cpu.read(0xFFFE)).thenReturn(0x34);
        when(cpu.read(0xFFFF)).thenReturn(0x12);

        var result = sut.execute(cpu, Integer.MAX_VALUE);

        verify(cpu, description("Unexpected address assigned to program counter: 0x%s".formatted(Integer.toHexString(result)))).setProgramCounter(0x1234);
    }
}