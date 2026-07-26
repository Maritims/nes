package no.clueless.emulation.cpu;

import no.clueless.emulation.impl.BusImpl;
import no.clueless.emulation.impl.Cpu6502Impl;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class KlausDormannTest {

    @Test
    public void runFunctionalTest() throws IOException {
        var cpu6502 = new Cpu6502Impl();
        var bus     = new BusImpl(cpu6502, mock(), mock());

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

        while (cpu6502.getClockCount() < maxCycles) {
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
