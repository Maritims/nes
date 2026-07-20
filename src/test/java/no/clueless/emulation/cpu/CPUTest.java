package no.clueless.emulation.cpu;

import no.clueless.emulation.ram.RAM;
import no.clueless.emulation.types.UInt16;
import no.clueless.emulation.types.UInt8;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CPUTest {
    @Test
    void CLC_should_clear_the_carry_flag() {
        // arrange
        var statusRegister = mock(StatusRegister.class);
        var cpu            = new CPU(new RAM(), mock(), mock(), mock(), statusRegister);

        // act
        cpu.CLC();

        // assert
        verify(statusRegister).clearFlag(Flag.Carry);
    }

    @Test
    void CLD_should_clear_the_decimal_flag() {
        // arrange
        var statusRegister = mock(StatusRegister.class);
        var cpu            = new CPU(new RAM(), mock(), mock(), mock(), statusRegister);

        // act
        cpu.CLD();

        // assert
        verify(statusRegister).clearFlag(Flag.Decimal);
    }

    @Test
    void CLI_should_clear_the_interrupt_flag() {
        // arrange
        var statusRegister = mock(StatusRegister.class);
        var cpu            = new CPU(new RAM(), mock(), mock(), mock(), statusRegister);

        // act
        cpu.CLI();

        // assert
        verify(statusRegister).clearFlag(Flag.InterruptDisable);
    }

    @Test
    void CLV_should_clear_the_overflow_flag() {
        // arrange
        var statusRegister = mock(StatusRegister.class);
        var cpu            = new CPU(new RAM(), mock(), mock(), mock(), statusRegister);

        // act
        cpu.CLV();

        // assert
        verify(statusRegister).clearFlag(Flag.Overflow);
    }

    @Test
    void SEC_should_set_the_carry_flag() {
        // arrange
        var statusRegister = mock(StatusRegister.class);
        var cpu            = new CPU(new RAM(), mock(), mock(), mock(), statusRegister);

        // act
        cpu.SEC();

        // assert
        verify(statusRegister).setFlag(Flag.Carry);
    }

    @Test
    void SED_should_set_the_decimal_flag() {
        // arrange
        var statusRegister = mock(StatusRegister.class);
        var cpu            = new CPU(new RAM(), mock(), mock(), mock(), statusRegister);

        // act
        cpu.SED();

        // assert
        verify(statusRegister).setFlag(Flag.Decimal);
    }

    @Test
    void SEI_should_set_the_interrupt_flag() {
        // arrange
        var statusRegister = mock(StatusRegister.class);
        var cpu            = new CPU(new RAM(), mock(), mock(), mock(), statusRegister);

        // act
        cpu.SEI();

        // assert
        verify(statusRegister).setFlag(Flag.InterruptDisable);
    }

    @Test
    void AND_should_yield_1_when_both_operands_are_1() {
        // arrange
        var bus            = new RAM();
        var address        = new UInt16(0x1234);
        var accumulator    = new Accumulator(new UInt8(1));
        var statusRegister = new StatusRegister();
        var cpu            = new CPU(bus, accumulator, new X(new UInt8(0)), new Y(new UInt8(0)), statusRegister);

        bus.write(address, new UInt8(1));

        // act
        cpu.AND(address);

        // assert
        assertEquals(1, accumulator.getValue().value(), "Accumulator should be 1");
    }

    @Test
    void AND_should_yield_0_when_either_operand_is_0() {
        // arrange
        var bus            = new RAM();
        var address        = new UInt16(0x1234);
        var accumulator    = new Accumulator(new UInt8(1));
        var statusRegister = new StatusRegister();
        var cpu            = new CPU(bus, accumulator, new X(new UInt8(0)), new Y(new UInt8(0)), statusRegister);

        bus.write(address, new UInt8(0));

        // act
        cpu.AND(address);

        // assert
        assertEquals(0, accumulator.getValue().value(), "Accumulator should be 0");
    }

    @Test
    void AND_should_throw_when_address_is_null() {
        var bus            = new RAM();
        var accumulator    = new Accumulator(new UInt8(1));
        var statusRegister = new StatusRegister();
        var cpu            = new CPU(bus, accumulator, new X(new UInt8(0)), new Y(new UInt8(0)), statusRegister);

        assertThrows(IllegalArgumentException.class, () -> cpu.AND(null));
    }

    @Test
    void ASL_should_shift_bits_to_the_left() {
        // arrange
        var bus            = new RAM();
        var statusRegister = new StatusRegister();
        var accumulator    = new Accumulator(new UInt8(255));
        var cpu            = new CPU(bus, accumulator, mock(), mock(), statusRegister);

        // act
        cpu.ASL(null);

        // assert
        assertEquals(254, accumulator.getValue().value(), "Accumulator should be 254");
        assertTrue(statusRegister.hasFlag(Flag.Carry), "Carry flag should be set");
    }

    @Test
    void ASL_should_read_from_bus_when_address_is_not_null() {
        // arrange
        var bus     = spy(new RAM());
        var cpu     = new CPU(bus, mock(), mock(), mock(), mock());
        var address = new UInt16(0x1234);

        bus.write(address, new UInt8(255));

        // act
        cpu.ASL(address);

        // assert
        verify(bus).read(address);
    }

    @Test
    void ASL_should_read_from_accumulator_when_address_is_null() {
        // arrange
        var bus         = spy(new RAM());
        var accumulator = spy(new Accumulator(new UInt8(255)));
        var cpu         = new CPU(bus, accumulator, mock(), mock(), mock());

        // act
        cpu.ASL(null);

        // assert
        verify(bus, atMost(2).description("The bus should not be read more than two times, and only from within the CPU.reset() method")).read(any());
        verify(accumulator).updateValue(any());
    }

    @Test
    void EOR_should_throw_when_address_is_null() {
        var bus            = new RAM();
        var accumulator    = new Accumulator(new UInt8(1));
        var statusRegister = new StatusRegister();
        var cpu            = new CPU(bus, accumulator, new X(new UInt8(0)), new Y(new UInt8(0)), statusRegister);

        assertThrows(IllegalArgumentException.class, () -> cpu.EOR(null));
    }

    @Test
    void EOR_should_return_0_when_both_operands_are_0() {
        // arrange
        var bus            = new RAM();
        var address        = new UInt16(0x1234);
        var accumulator    = new Accumulator(new UInt8(0));
        var statusRegister = new StatusRegister();

        var cpu = new CPU(bus, accumulator, new X(new UInt8(0)), new Y(new UInt8(0)), statusRegister);

        bus.write(address, new UInt8(0));

        // act
        cpu.EOR(address);

        // assert
        assertEquals(0, accumulator.getValue().value(), "Accumulator should be 0");
    }

    @Test
    void EOR_should_return_1_when_one_operand_is_1() {
        // arrange
        var bus            = new RAM();
        var address        = new UInt16(0x1234);
        var accumulator    = new Accumulator(new UInt8(0));
        var statusRegister = new StatusRegister();
        var cpu            = new CPU(bus, accumulator, new X(new UInt8(0)), new Y(new UInt8(0)), statusRegister);

        bus.write(address, new UInt8(1));

        // act
        cpu.EOR(address);

        // assert
        assertEquals(1, accumulator.getValue().value(), "Accumulator should be 1");
    }

    @Test
    void EOR_should_return_0_when_both_operands_are_1() {
        // arrange
        var bus            = new RAM();
        var address        = new UInt16(0x1234);
        var accumulator    = new Accumulator(new UInt8(1));
        var statusRegister = new StatusRegister();
        var cpu            = new CPU(bus, accumulator, new X(new UInt8(1)), new Y(new UInt8(1)), statusRegister);

        bus.write(address, new UInt8(1));

        // act
        cpu.EOR(address);

        // assert
        assertEquals(0, accumulator.getValue().value(), "Accumulator should be 0");
    }

    @Test
    void LSR_should_shift_bits_to_the_right() {
        // arrange
        var bus            = new RAM();
        var statusRegister = new StatusRegister();
        var accumulator    = new Accumulator(new UInt8(1));
        var cpu            = new CPU(bus, accumulator, mock(), mock(), statusRegister);

        // act
        cpu.LSR(null);

        // assert
        assertEquals(0, accumulator.getValue().value(), "Accumulator should be 0");
        assertTrue(statusRegister.hasFlag(Flag.Carry), "Carry flag should be set");
    }

    @Test
    void LSR_should_read_from_bus_when_address_is_not_null() {
        // arrange
        var bus     = spy(new RAM());
        var cpu     = new CPU(bus, mock(), mock(), mock(), mock());
        var address = new UInt16(0x1234);

        // act
        cpu.LSR(address);

        // assert
        verify(bus).read(address);
    }

    @Test
    void LSR_should_read_from_accumulator_when_address_is_null() {
        // arrange
        var bus         = spy(new RAM());
        var accumulator = spy(new Accumulator(new UInt8(1)));
        var cpu         = new CPU(bus, accumulator, mock(), mock(), mock());

        // act
        cpu.LSR(null);

        // assert
        verify(bus, atMost(2).description("The bus should not be read more than two times, and only from within the CPU.reset() method")).read(any());
        verify(accumulator).updateValue(any());
    }

    @Test
    void ORA_should_throw_when_address_is_null() {
        var bus            = new RAM();
        var accumulator    = new Accumulator(new UInt8(1));
        var statusRegister = new StatusRegister();
        var cpu            = new CPU(bus, accumulator, new X(new UInt8(0)), new Y(new UInt8(0)), statusRegister);

        assertThrows(IllegalArgumentException.class, () -> cpu.ORA(null));
    }

    @Test
    void ORA_should_return_1_when_both_operands_are_1() {
        var bus            = new RAM();
        var address        = new UInt16(0x1234);
        var accumulator    = new Accumulator(new UInt8(1));
        var statusRegister = new StatusRegister();

        var cpu = new CPU(bus, accumulator, new X(new UInt8(0)), new Y(new UInt8(0)), statusRegister);
        bus.write(address, new UInt8(1));

        cpu.ORA(address);

        assertEquals(1, accumulator.getValue().value(), "Accumulator should be 1");
    }

    @Test
    void ORA_should_return_0_when_both_operands_are_0() {
        var bus            = new RAM();
        var address        = new UInt16(0x1234);
        var accumulator    = new Accumulator(new UInt8(0));
        var statusRegister = new StatusRegister();

        var cpu = new CPU(bus, accumulator, new X(new UInt8(0)), new Y(new UInt8(0)), statusRegister);
        bus.write(address, new UInt8(0));

        cpu.ORA(address);

        assertEquals(0, accumulator.getValue().value(), "Accumulator should be 0");
    }

    @Test
    void ORA_should_return_1_when_one_operand_is_1() {
        var bus            = new RAM();
        var address        = new UInt16(0x1234);
        var accumulator    = new Accumulator(new UInt8(0));
        var statusRegister = new StatusRegister();

        var cpu = new CPU(bus, accumulator, new X(new UInt8(0)), new Y(new UInt8(0)), statusRegister);
        bus.write(address, new UInt8(1));

        cpu.ORA(address);

        assertEquals(1, accumulator.getValue().value(), "Accumulator should be 1");
    }

    @Test
    void ROL_should_rotate_bits_to_the_left_by_1() {
        var bus            = new RAM();
        var statusRegister = new StatusRegister();
        var accumulator    = new Accumulator(new UInt8(255));
        var cpu            = new CPU(bus, accumulator, mock(), mock(), statusRegister);

        cpu.ROL(null);

        assertEquals(254, accumulator.getValue().value(), "Accumulator should be 254");
        assertTrue(statusRegister.hasFlag(Flag.Carry), "Carry flag should be set");
    }

    @Test
    void ROL_should_read_from_bus_when_address_is_not_null() {
        var bus     = spy(new RAM());
        var cpu     = new CPU(bus, mock(), mock(), mock(), mock());
        var address = new UInt16(0x1234);

        bus.write(address, new UInt8(255));

        cpu.ROL(address);

        verify(bus).read(address);
    }

    @Test
    void ROL_should_read_from_accumulator_when_address_is_null() {
        var bus         = spy(new RAM());
        var accumulator = spy(new Accumulator(new UInt8(255)));
        var cpu         = new CPU(bus, accumulator, mock(), mock(), mock());

        cpu.ROL(null);

        verify(bus, atMost(2).description("The bus should not be read more than two times, and only from within the CPU.reset() method")).read(any());
        verify(accumulator).updateValue(any());
    }

    @Test
    void ROR_should_rotate_bits_to_the_right_by_1() {
        var bus            = new RAM();
        var statusRegister = new StatusRegister();
        var accumulator    = new Accumulator(new UInt8(255));
        var cpu            = new CPU(bus, accumulator, mock(), mock(), statusRegister);

        cpu.ROR(null);

        assertEquals(127, accumulator.getValue().value(), "Accumulator should be 2");
        assertTrue(statusRegister.hasFlag(Flag.Carry), "Carry flag should be set");
    }

    @Test
    void ROR_should_read_from_bus_when_address_is_not_null() {
        var bus     = spy(new RAM());
        var cpu     = new CPU(bus, mock(), mock(), mock(), mock());
        var address = new UInt16(0x1234);

        bus.write(address, new UInt8(255));

        cpu.ROR(address);

        verify(bus).read(address);
    }

    @Test
    void ROR_should_read_from_accumulator_when_address_is_null() {
        var bus         = spy(new RAM());
        var accumulator = spy(new Accumulator(new UInt8(255)));
        var cpu         = new CPU(bus, accumulator, mock(), mock(), mock());

        cpu.ROR(null);

        verify(bus, atMost(2).description("The bus should not be read more than two times, and only from within the CPU.reset() method")).read(any());
        verify(accumulator).updateValue(any());
    }

    @Test
    void DEX_should_decrement_the_X_register() {
        // arrange
        var bus = new RAM();
        var x   = mock(X.class);
        var cpu = new CPU(bus, mock(), x, mock(), mock());

        // act
        cpu.DEX();

        // assert
        verify(x).decrement();
    }

    @Test
    void DEY_should_decrement_the_Y_register() {
        // arrange
        var bus = new RAM();
        var y   = mock(Y.class);
        var cpu = new CPU(bus, mock(), mock(), y, mock());

        // act
        cpu.DEY();

        // assert
        verify(y).decrement();
    }

    @Test
    void INX_should_increment_the_X_register() {
        // arrange
        var bus = new RAM();
        var x   = spy(new X(new UInt8(0)));
        var cpu = new CPU(bus, mock(), x, mock(), mock());

        // act
        cpu.INX();

        // assert
        verify(x).increment();
    }

    @Test
    void INY_should_increment_the_Y_register() {
        // arrange
        var bus = new RAM();
        var y   = mock(Y.class);
        var cpu = new CPU(bus, mock(), mock(), y, mock());

        // act
        cpu.INY();

        // assert
        verify(y).increment();
    }

    @Test
    void ADC_should_return_300_when_adding_150_to_150() {
        var bus            = new RAM();
        var address        = new UInt16(0x1234);
        var accumulator    = new Accumulator(new UInt8(150));
        var statusRegister = new StatusRegister();
        var cpu            = new CPU(bus, accumulator, new X(new UInt8(0)), new Y(new UInt8(0)), statusRegister);

        bus.write(address, new UInt8(150));

        cpu.ADC(address);

        assertEquals(44, accumulator.getValue().value(), "Accumulator should be 44");
        assertTrue(statusRegister.hasFlag(Flag.Carry), "Carry flag should be set");
    }
}