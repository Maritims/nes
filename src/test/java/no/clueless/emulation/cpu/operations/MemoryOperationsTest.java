package no.clueless.emulation.cpu.operations;

import no.clueless.emulation.Bus;
import no.clueless.emulation.cpu.CPU;
import no.clueless.emulation.ram.RAM;
import no.clueless.emulation.types.UnsignedByte;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class MemoryOperationsTest {
    Bus bus;
    CPU cpu;

    @BeforeEach
    void setUp() {
        bus = new RAM();
        cpu = spy(new CPU(bus));
    }

    @Test
    void dex_should_decrement_x() {
        // arrange
        cpu.setX(UnsignedByte.ONE);

        // act
        MemoryOperations.dex(cpu, null);

        // assert
        verify(cpu).setX(UnsignedByte.ZERO);
        assertEquals(UnsignedByte.ZERO, cpu.getX());
    }

    @Test
    void dey_should_decrement_y() {
        // arrange
        cpu.setY(UnsignedByte.ONE);

        // act
        MemoryOperations.dey(cpu, null);

        // assert
        verify(cpu).setY(UnsignedByte.ZERO);
        assertEquals(UnsignedByte.ZERO, cpu.getY());
    }

    @Test
    void inx_should_increment_x() {
        // arrange
        cpu.setX(UnsignedByte.ZERO);

        // act
        MemoryOperations.inx(cpu, null);

        // assert
        verify(cpu).setX(UnsignedByte.ONE);
        assertEquals(UnsignedByte.ONE, cpu.getX());
    }

    @Test
    void iny_should_increment_y() {
        // arrange
        cpu.setY(UnsignedByte.ZERO);

        // act
        MemoryOperations.iny(cpu, null);

        // assert
        verify(cpu).setY(UnsignedByte.ONE);
        assertEquals(UnsignedByte.ONE, cpu.getY());
    }
}