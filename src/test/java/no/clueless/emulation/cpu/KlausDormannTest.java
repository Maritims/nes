package no.clueless.emulation.cpu;

import no.clueless.emulation.*;
import no.clueless.emulation.impl.cpu.Cpu6502Impl;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class KlausDormannTest {

    static class TestBus implements Bus {
        private final int[] ram = new int[65536];
        private final Cpu6502 cpu;

        TestBus(Cpu6502 cpu) {
            this.cpu = cpu;
            cpu.connectToBus(this);
        }

        @Override
        public Cpu6502 getCpu() {
            return cpu;
        }

        @Override
        public Ppu2C02 getPpu() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Apu2A03 getApu() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Cartridge getCartridge() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void insertCartridge(Cartridge cartridge) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clock() {
            /* No-op */
        }

        @Override
        public int read(int address) {
            return ram[address & 0xFFFF];
        }

        @Override
        public void write(int address, int data) {
            ram[address & 0xFFFF] = data & 0xFF;
        }

        @Override
        public void reset() {
            cpu.reset();
        }
    }

    @Test
    public void runFunctionalTest() throws IOException {
        var cpu6502 = new Cpu6502Impl(true);
        var bus     = new TestBus(cpu6502);

        // Klaus Dormann's test binary is a 64KB image
        var path  = Paths.get("src/test/resources/6502_functional_test.bin");
        var bytes = Files.readAllBytes(path);
        for (var i = 0; i < bytes.length && i < 65536; i++) {
            bus.write(i, bytes[i] & 0xFF);
        }

        // The test binary starts at 0x0400
        cpu6502.reset();
        cpu6502.setProgramCounter(0x0400);

        var stuckCount   = 0;
        var maxCycles    = 100_000_000;
        var instructions = 0;

        while (cpu6502.getTotalClockCount() < maxCycles) {
            var pc = cpu6502.getProgramCounter();
            cpu6502.clock();
            instructions++;

            if (pc == cpu6502.getProgramCounter()) {
                stuckCount++;
                if (stuckCount > 10) {
                    // Success trap for Klaus Dormann's test is 0x3469
                    assertEquals(0x3469, pc, "CPU trapped at unexpected location (failure): %s".formatted(Integer.toHexString(pc)));
                    return;
                }
            } else {
                stuckCount = 0;
            }
        }

        fail("Test timed out. Last PC: %s".formatted(Integer.toHexString(cpu6502.getProgramCounter())));
    }

}
