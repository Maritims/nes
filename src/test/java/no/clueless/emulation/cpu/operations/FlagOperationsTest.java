package no.clueless.emulation.cpu.operations;

import no.clueless.emulation.Bus;
import no.clueless.emulation.cpu.CPU;
import no.clueless.emulation.cpu.Flag;
import no.clueless.emulation.ram.RAM;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FlagOperationsTest {
    Bus bus;
    CPU cpu;

    @BeforeEach
    void setUp() {
        bus = new RAM();
        cpu = new CPU(bus);
    }

    @Test
    void clc_should_clear_the_carry_flag() {
        cpu.getStatusRegister().setFlag(Flag.Carry);
        FlagOperations.clc(cpu, null);
        assertFalse(cpu.getStatusRegister().hasFlag(Flag.Carry), "Carry flag should be cleared");
    }

    @Test
    void cld_should_clear_the_decimal_flag() {
        cpu.getStatusRegister().setFlag(Flag.Decimal);
        FlagOperations.cld(cpu, null);
        assertFalse(cpu.getStatusRegister().hasFlag(Flag.Decimal), "Decimal flag should be cleared");
    }

    @Test
    void cli_should_clear_the_interrupt_flag() {
        cpu.getStatusRegister().setFlag(Flag.InterruptDisable);
        FlagOperations.cli(cpu, null);
        assertFalse(cpu.getStatusRegister().hasFlag(Flag.InterruptDisable), "Interrupt Disable flag should be cleared");
    }

    @Test
    void clv_should_clear_the_overflow_flag() {
        cpu.getStatusRegister().setFlag(Flag.Overflow);
        FlagOperations.clv(cpu, null);
        assertFalse(cpu.getStatusRegister().hasFlag(Flag.Overflow), "Overflow flag should be cleared");
    }

    @Test
    void sec_should_set_the_carry_flag() {
        cpu.getStatusRegister().setFlag(Flag.Carry);
        assertTrue(cpu.getStatusRegister().hasFlag(Flag.Carry), "Carry flag should be set");
    }

    @Test
    void swd_should_set_the_decimal_flag() {
        cpu.getStatusRegister().setFlag(Flag.Decimal);
        assertTrue(cpu.getStatusRegister().hasFlag(Flag.Decimal), "Decimal flag should be set");
    }

    @Test
    void sei_should_set_the_interrupt_flag() {
        cpu.getStatusRegister().setFlag(Flag.InterruptDisable);
        assertTrue(cpu.getStatusRegister().hasFlag(Flag.InterruptDisable), "Interrupt Disable flag should be set");
    }
}