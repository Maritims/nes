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
import static org.mockito.Mockito.spy;

class AluOperationsTest {
    Bus bus;
    CPU cpu;

    @BeforeEach
    void setUp() {
        bus = spy(new RAM());
        cpu = spy(new CPU(bus));
    }

    @Test
    void adc_should_yield_300_when_adding_150_to_150() {
        var address = 0x1234;
        cpu.setAccumulator(new UnsignedByte(150));
        bus.write(0x1234, 150);

        AluOperations.adc(cpu, new UnsignedWord(address));

        assertEquals(44, cpu.getAccumulator().intValue(), "Accumulator should be 44");
        assertTrue(cpu.getStatusRegister().hasFlag(Flag.Carry), "Carry flag should be set");
    }

    @Test
    void and_should_yield_1_when_both_operands_are_1() {
        var address = 0x1234;
        cpu.setAccumulator(new UnsignedByte(1));
        bus.write(address, 1);

        AluOperations.and(cpu, new UnsignedWord(address));

        assertEquals(1, cpu.getAccumulator().intValue(), "Accumulator should be 1");
    }

    @Test
    void and_should_yield_0_when_either_operand_is_0() {
        var address = 0x1234;
        bus.write(address, 0);

        AluOperations.and(cpu, new UnsignedWord(address));

        assertEquals(0, cpu.getAccumulator().intValue(), "Accumulator should be 0");
    }

    @Test
    void ora_should_throw_when_address_is_null() {
        assertThrows(IllegalArgumentException.class, () -> AluOperations.ora(cpu, null));
    }

    @Test
    void ora_should_return_1_when_both_operands_are_1() {
        // arrange
        var address = 0x1234;
        cpu.setAccumulator(UnsignedByte.ONE);
        bus.write(address, 1);

        // act
        AluOperations.ora(cpu, new UnsignedWord(address));

        // assert
        assertEquals(1, cpu.getAccumulator().intValue(), "Accumulator should be 1");
    }

    @Test
    void ora_should_return_0_when_both_operands_are_0() {
        // arrange
        var address = 0x1234;
        bus.write(address, 0);

        // act
        AluOperations.ora(cpu, new UnsignedWord(address));

        // assert
        assertEquals(0, cpu.getAccumulator().intValue(), "Accumulator should be 0");
    }

    @Test
    void ora_should_return_1_when_one_operand_is_1() {
        // arrange
        var address = 0x1234;
        bus.write(address, 1);

        // act
        AluOperations.ora(cpu, new UnsignedWord(address));

        // assert
        assertEquals(1, cpu.getAccumulator().intValue(), "Accumulator should be 1");
    }

    @Test
    void eor_should_throw_when_address_is_null() {
        assertThrows(IllegalArgumentException.class, () -> AluOperations.eor(cpu, null));
    }

    @Test
    void eor_should_return_0_when_both_operands_are_0() {
        // arrange
        var address        = 0x1234;
        bus.write(address, 0);

        // act
        AluOperations.eor(cpu, new UnsignedWord(address));

        // assert
        assertEquals(0, cpu.getAccumulator().intValue(), "Accumulator should be 0");
    }

    @Test
    void eor_should_return_1_when_one_operand_is_1() {
        // arrange
        var address        = 0x1234;
        bus.write(address, 1);

        // act
        AluOperations.eor(cpu, new UnsignedWord(address));

        // assert
        assertEquals(1, cpu.getAccumulator().intValue(), "Accumulator should be 1");
    }

    @Test
    void eor_should_return_0_when_both_operands_are_1() {
        // arrange
        var address        = 0x1234;
        cpu.setAccumulator(UnsignedByte.ONE);
        bus.write(address, 1);

        // act
        AluOperations.eor(cpu, new UnsignedWord(address));

        // assert
        assertEquals(0, cpu.getAccumulator().intValue(), "Accumulator should be 0");
    }
}