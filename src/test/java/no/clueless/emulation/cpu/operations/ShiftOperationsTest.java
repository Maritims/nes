package no.clueless.emulation.cpu.operations;

import no.clueless.emulation.Bus;
import no.clueless.emulation.cpu.CPU;
import no.clueless.emulation.cpu.Flag;
import no.clueless.emulation.ram.RAM;
import no.clueless.emulation.types.UnsignedByte;
import no.clueless.emulation.types.UnsignedWord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShiftOperationsTest {
    Bus bus;
    CPU cpu;

    @BeforeEach
    void setUp() {
        bus = spy(new RAM());
        cpu = new CPU(bus);
    }

    @Test
    void asl_should_shift_bits_to_the_left() {
        ShiftOperations.asl(cpu, null);
        assertEquals(254, cpu.getAccumulator().intValue(), "Accumulator should be 254");
        assertTrue(cpu.getStatusRegister().hasFlag(Flag.Carry), "Carry flag should be set");
    }

    @Test
    void asl_should_read_from_bus_when_address_is_not_null() {
        // arrange
        var address = new UnsignedWord(0x1234);
        bus.write(address, new UnsignedByte(255));

        // act
        ShiftOperations.asl(cpu, address);

        // assert
        verify(bus).read(address);
    }

    @Test
    void asl_should_read_from_accumulator_when_address_is_null() {
        ShiftOperations.asl(cpu, null);
        verify(bus, atMost(2).description("The bus should not be read more than two times, and only from within the CPU.reset() method")).read(any());
    }

    @Test
    void lsr_should_shift_bits_to_the_right() {
        // arrange
        cpu.setAccumulator(UnsignedByte.ONE);

        // act
        ShiftOperations.lsr(cpu, null);

        // assert
        assertEquals(0, cpu.getAccumulator().intValue(), "Accumulator should be 0");
        assertTrue(cpu.getStatusRegister().hasFlag(Flag.Carry), "Carry flag should be set");
    }

    @Test
    void lsr_should_read_from_bus_when_address_is_not_null() {
        // arrange
        var address = new UnsignedWord(0x1234);

        // act
        ShiftOperations.lsr(cpu, address);

        // assert
        verify(bus).read(address);
    }

    @Test
    void lsr_should_read_from_accumulator_when_address_is_null() {
        ShiftOperations.lsr(cpu, null);
        verify(bus, atMost(2).description("The bus should not be read more than two times, and only from within the CPU.reset() method")).read(any());
    }

    @Test
    void rol_should_rotate_bits_to_the_left_by_1() {
        // arrange
        cpu.setAccumulator(UnsignedByte.MAX_VALUE);

        // act
        ShiftOperations.rol(cpu, null);

        // assert
        assertEquals(254, cpu.getAccumulator().intValue(), "Accumulator should be 254");
        assertTrue(cpu.getStatusRegister().hasFlag(Flag.Carry), "Carry flag should be set");
    }

    @Test
    void rol_should_read_from_bus_when_address_is_not_null() {
        // arrange
        var address = new UnsignedWord(0x1234);
        bus.write(address, new UnsignedByte(255));

        // act
        ShiftOperations.rol(cpu, address);

        // assert
        verify(bus).read(address);
    }

    @Test
    void rol_should_read_from_accumulator_when_address_is_null() {
        // arrange
        cpu.setAccumulator(UnsignedByte.MAX_VALUE);

        // act
        ShiftOperations.rol(cpu, null);

        // assert
        verify(bus, atMost(2).description("The bus should not be read more than two times, and only from within the CPU.reset() method")).read(any());
    }

    @Test
    void ror_should_rotate_bits_to_the_right_by_1() {
        // arrange
        cpu.setAccumulator(UnsignedByte.MAX_VALUE);

        // act
        ShiftOperations.ror(cpu, null);

        // assert
        assertEquals(127, cpu.getAccumulator().intValue(), "Accumulator should be 2");
        assertTrue(cpu.getStatusRegister().hasFlag(Flag.Carry), "Carry flag should be set");
    }

    @Test
    void ror_should_read_from_bus_when_address_is_not_null() {
        // arrange
        var address = new UnsignedWord(0x1234);
        bus.write(address, new UnsignedByte(255));

        // act
        ShiftOperations.ror(cpu, address);

        // assert
        verify(bus).read(address);
    }

    @Test
    void ror_should_read_from_accumulator_when_address_is_null() {
        // arrange
        cpu.setAccumulator(UnsignedByte.MAX_VALUE);

        // act
        ShiftOperations.ror(cpu, null);

        // assert
        verify(bus, atMost(2).description("The bus should not be read more than two times, and only from within the CPU.reset() method")).read(any());
    }
}