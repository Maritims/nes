package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class ArithmeticShiftLeftTest {

    Cpu6502             cpu;
    ArithmeticShiftLeft asl;

    @BeforeEach
    void setUp() {
        cpu = mock(Cpu6502.class);
        asl = ArithmeticShiftLeft.ASL;
    }

    @Test
    void shifts_bit7_into_the_carry_flag_when_bit7_is_set() {
        when(cpu.getAccumulator()).thenReturn(0x80);
        asl.shiftLeft(cpu, -1);
        verify(cpu).setFlag(Cpu6502.Flag.CARRY, true);
    }

    @Test
    void does_not_shift_bit7_into_the_carry_flag_when_bit7_is_not_set() {
        asl.shiftLeft(cpu, 0x7F);
        verify(cpu).setFlag(Cpu6502.Flag.CARRY, false);
    }

    @Test
    void shifts_value_0_into_the_zero_flag() {
        asl.shiftLeft(cpu, 0x80);
        verify(cpu).setFlag(Cpu6502.Flag.ZERO, true);
    }

    @Test
    void stores_result_in_accumulator_when_address_is_not_provided() {
        var result = asl.execute(cpu, -1);
        verify(cpu).setAccumulator(result);
        verify(cpu, never()).write(anyInt(), anyByte());
    }

    @Test
    void stores_result_in_memory_when_address_is_provided() {
        asl.execute(cpu, 0x1234);
        verify(cpu, never()).setAccumulator(anyInt());
        verify(cpu, times(2)).write(anyInt(), anyInt());
    }

    @Test
    void writes_original_before_result_when_address_is_provided() {
        var address = 0x1234;
        var original = 0x12;
        var result   = 0x24;
        when(cpu.read(address)).thenReturn(original);

        asl.execute(cpu, address);

        verify(cpu).write(address, original);
        verify(cpu).write(address, result);
    }
}