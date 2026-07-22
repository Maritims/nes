package no.clueless.emulation.cpu;

import no.clueless.emulation.cpu.operations.JumpOperations;
import no.clueless.emulation.ram.RAM;
import no.clueless.emulation.types.UnsignedWord;
import no.clueless.emulation.types.UnsignedByte;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class FunctionalCPUTest {

    @Test
    public void runFunctionalTest() throws IOException {
        var ram   = new RAM();
        var path  = Paths.get("src/test/resources/6502_functional_test.bin");
        var bytes = Files.readAllBytes(path);

        // Klaus Dormann's test binary is a 64KB image
        for (var i = 0; i < bytes.length && i < 65536; i++) {
            ram.write(new UnsignedWord(i), new UnsignedByte(bytes[i] & 0xFF));
        }

        var cpu = new CPU(ram);
        // The test binary starts at 0x0400
        cpu.reset();
        JumpOperations.jmp(cpu, new UnsignedWord(0x0400));

        var stuckCount   = 0;
        var maxCycles    = 100_000_000;
        var instructions = 0;

        while (cpu.getTotalCycles() < maxCycles) {
            var pc = cpu.getProgramCounter();
            cpu.step();
            instructions++;

            if (pc.equals(cpu.getProgramCounter())) {
                stuckCount++;
                if (stuckCount > 10) {
                    // Success trap for Klaus Dormann's test is 0x3469
                    assertEquals(new UnsignedWord(0x3469), pc, "CPU trapped at unexpected location (failure)");
                    return;
                }
            } else {
                stuckCount = 0;
            }
        }

        fail("Test timed out. Last PC: %s".formatted(cpu.getProgramCounter()));
    }

}
