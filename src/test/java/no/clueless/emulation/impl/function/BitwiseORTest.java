package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class BitwiseORTest {
    Cpu6502   cpu;
    BitwiseOR bitwiseOR;

    @BeforeEach
    void setUp() {
        cpu       = mock(Cpu6502.class);
        bitwiseOR = BitwiseOR.ORA;
    }

    @Test
    void ORs_accumulator_and_memory_without_zero_and_negative_flags() {
        var address = 0x1234;
        when(cpu.getAccumulator()).thenReturn(0x12);
        when(cpu.read(address)).thenReturn(0x34);

        var result = bitwiseOR.execute(cpu, address);

        verify(cpu, description("Unexpected value assigned to accumulator: 0x%s".formatted(Integer.toHexString(result)))).setAccumulator(0x36);
        verify(cpu, description("The ZERO flag should not be set")).setFlag(Cpu6502.Flag.ZERO, false);
        verify(cpu, description("The NEGATIVE flag should not be set")).setFlag(Cpu6502.Flag.NEGATIVE, false);
    }

    @Test
    void ORs_accumulator_and_memory_with_zero_flag_only() {
        var address = 0x1234;
        when(cpu.getAccumulator()).thenReturn(0x00);
        when(cpu.read(address)).thenReturn(0x00);

        var result = bitwiseOR.execute(cpu, address);

        verify(cpu, description("Unexpected value assigned to accumulator: 0x%s".formatted(Integer.toHexString(result)))).setAccumulator(0x00);
        verify(cpu, description("The ZERO flag should be set")).setFlag(Cpu6502.Flag.ZERO, true);
        verify(cpu, description("The NEGATIVE flag should not be set")).setFlag(Cpu6502.Flag.NEGATIVE, false);
    }

    @Test
    void ORs_accumulator_and_memory_with_negative_flag_only() {
        var address = 0x1234;
        when(cpu.getAccumulator()).thenReturn(0x12);
        when(cpu.read(address)).thenReturn(0x80);

        var result = bitwiseOR.execute(cpu, address);

        verify(cpu, description("Unexpected value assigned to accumulator: 0x%s".formatted(Integer.toHexString(result)))).setAccumulator(0x92);
        verify(cpu, description("The ZERO flag should not be set")).setFlag(Cpu6502.Flag.ZERO, false);
        verify(cpu, description("The NEGATIVE flag should be set")).setFlag(Cpu6502.Flag.NEGATIVE, true);
    }
}