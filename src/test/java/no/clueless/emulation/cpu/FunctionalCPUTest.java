package no.clueless.emulation.cpu;

import no.clueless.emulation.ram.RAM;
import no.clueless.emulation.types.UnsignedWord;
import no.clueless.emulation.types.UnsignedByte;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class FunctionalCPUTest {

    @Test
    public void runFunctionalTest() throws IOException {
        RAM    ram   = new RAM();
        Path   path  = Paths.get("src/test/resources/6502_functional_test.bin");
        byte[] bytes = Files.readAllBytes(path);

        // Klaus Dormann's test binary is a 64KB image
        for (int i = 0; i < bytes.length && i < 65536; i++) {
            ram.write(new UnsignedWord(i), new UnsignedByte(bytes[i] & 0xFF));
        }

        CPU cpu = new CPU(ram);
        // The test binary starts at 0x0400
        cpu.reset();
        cpu.JMP(new UnsignedWord(0x0400));

        int  stuckCount   = 0;
        long maxCycles    = 100_000_000;
        long instructions = 0;

        while (cpu.getTotalCycles() < maxCycles) {
            UnsignedWord pc = getPC(cpu);
            cpu.step();
            instructions++;

            if (pc.equals(getPC(cpu))) {
                stuckCount++;
                if (stuckCount > 10) {
                    // Success trap for Klaus Dormann's test is 0x3469
                    assertEquals(0x3469, pc.intValue(), "CPU trapped at unexpected location (failure)");
                    return;
                }
            } else {
                stuckCount = 0;
            }
        }

        fail("Test timed out. Last PC: 0x%04X".formatted(getPC(cpu).intValue()));
    }

    private UnsignedWord getPC(CPU cpu) {
        try {
            var field = CPU.class.getDeclaredField("programCounter");
            field.setAccessible(true);
            return (UnsignedWord) field.get(cpu);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
