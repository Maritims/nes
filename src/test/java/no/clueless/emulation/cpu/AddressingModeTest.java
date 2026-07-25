package no.clueless.emulation.cpu;

import no.clueless.emulation.Bus;
import no.clueless.emulation.ram.RAM;
import no.clueless.emulation.types.UnsignedByte;
import no.clueless.emulation.types.UnsignedWord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static no.clueless.emulation.cpu.AddressingModes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddressingModeTest {
    Bus bus;
    CPU cpu;

    @BeforeEach
    void setUp() {
        bus = new RAM();
        cpu = spy(new CPU(bus));
    }

    @Test
    void immediate_fetches_PC_and_increments_PC_once() {
        // arrange
        var pc = cpu.getProgramCounter();
        bus.write(pc.intValue(), 0x56);

        // act
        var actual = IMMEDIATE.resolve(cpu, bus).address();

        // assert
        verify(cpu, description("The program counter should be incremented once")).getAndIncrementProgramCounter();
        assertEquals(pc.intValue(), actual, "Unexpected address");
        assertEquals(0x56, bus.read(actual), "Unexpected value in memory");
        assertEquals(pc.increment(), cpu.getProgramCounter(), "The program counter should be incremented once");
    }

    @Test
    void absolute_fetches_16_bit_address_from_bus_and_increments_PC_twice() {
        // arrange
        var pc       = cpu.getProgramCounter();
        var expected = new UnsignedWord(0x5678);

        bus.write(pc.intValue(), 0x78);
        bus.write(pc.increment().intValue(), 0x56);

        var actual = ABSOLUTE.resolve(cpu, bus).address();

        // assert
        verify(cpu, times(2).description("The program counter should be incremented twice")).getAndIncrementProgramCounter();
        assertEquals(expected, actual, "Unexpected address");
        assertEquals(pc.increment().increment(), cpu.getProgramCounter(), "The program counter should be incremented twice");
    }

    @Test
    void executeAbsoluteWithRegister_fetches_effective_address_without_page_crossed() {
        // arrange
        var pc       = cpu.getProgramCounter();

        bus.write(pc.intValue(), 0x78);
        bus.write(pc.increment().intValue(), 0x56);

        // act
        var result = AddressingModes.executeAbsoluteWithRegister(cpu, bus, new UnsignedByte(0x10));

        // assert
        verify(cpu, times(2).description("The program counter should be incremented twice")).getAndIncrementProgramCounter();
        assertEquals(new UnsignedWord(0x5688), result.address(), "Unexpected address");
        assertFalse(result.isPageCrossed(), "Page was crossed, but should not have been");
        assertEquals(pc.increment().increment(), cpu.getProgramCounter(), "The program counter should be incremented twice");
    }

    @Test
    void executeAbsoluteWithRegister_detects_page_cross() {
        // arrange
        var pc       = cpu.getProgramCounter();

        bus.write(pc.intValue(), 0x10);
        bus.write(pc.increment().intValue(), 0x10);

        // act
        var result = executeAbsoluteWithRegister(cpu, bus, new UnsignedByte(0xF0));

        // assert
        verify(cpu, times(2).description("The program counter should be incremented twice")).getAndIncrementProgramCounter();
        assertEquals(new UnsignedWord(0x1100), result.address(), "Unexpected address");
        assertTrue(result.isPageCrossed(), "Page was not crossed, but should have been");
        assertEquals(pc.increment().increment(), cpu.getProgramCounter(), "The program counter should be incremented twice");
    }

    @Test
    void zeroPage_fetches_address_from_bus_and_increments_PC_once() {
        // arrange
        var pc = cpu.getProgramCounter();
        bus.write(pc.intValue(), 0x56);

        // act
        var result = ZERO_PAGE.resolve(cpu, bus).address();

        // assert
        assertEquals(new UnsignedWord(0x0056), result, "Unexpected address");
        assertEquals(pc.increment(), cpu.getProgramCounter(), "The program counter should be incremented once");
    }

    @Test
    void executeZeroPageWithRegister_calculates_effective_address_using_register_and_increments_PC_once() {
        // arrange
        var pc = cpu.getProgramCounter();
        bus.write(pc.intValue(), 0x56);

        // act
        var result = executeZeroPageWithRegister(cpu, bus, new UnsignedByte(0x10));

        // assert
        assertEquals(new UnsignedWord(0x0066), result.address(), "Unexpected address");
        assertEquals(pc.increment(), cpu.getProgramCounter(), "The program counter should be incremented once");
    }
}