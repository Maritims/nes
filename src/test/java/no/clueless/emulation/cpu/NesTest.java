package no.clueless.emulation.cpu;

import no.clueless.emulation.Cartridge;
import no.clueless.emulation.SystemBus;
import no.clueless.emulation.ram.RAM;
import no.clueless.emulation.types.UInt16;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NesTest {

    @Test
    public void runNesTest() throws Exception {
        Path romPath = Paths.get("src/test/resources/nestest/nestest.nes");
        Cartridge cartridge = Cartridge.loadFromFile(romPath);
        RAM ram = new RAM();
        SystemBus bus = new SystemBus(ram, cartridge);
        CPU cpu = new CPU(bus);
        cpu.setDecimalModeSupported(false);

        // nestest in automated mode starts at 0xC000
        cpu.setProgramCounter(new UInt16(0xC000));

        try (BufferedReader reader = Files.newBufferedReader(Paths.get("src/test/resources/nestest/nestest.log"))) {
            
            String line;
            int lineNum = 0;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                ExpectedState expected = parseLogLine(line);

                // Compare current state with expected state BEFORE executing instruction
                assertState(lineNum, expected, cpu);

                cpu.step();
            }
        }
    }

    private void assertState(int lineNum, ExpectedState expected, CPU cpu) {
        String actualState = String.format("PC:%04X A:%02X X:%02X Y:%02X P:%02X SP:%02X",
                cpu.getProgramCounter().getValue().value(),
                cpu.getAccumulator().getValue().value(),
                cpu.getX().getValue().value(),
                cpu.getY().getValue().value(),
                cpu.getStatusRegister().toByte().value(),
                cpu.getStackPointer().getValue().value());
        String expectedState = String.format("PC:%04X A:%02X X:%02X Y:%02X P:%02X SP:%02X",
                expected.pc, expected.a, expected.x, expected.y, expected.p, expected.sp);

        if (!actualState.equals(expectedState)) {
            System.out.println("[DEBUG_LOG] Failure at line " + lineNum);
            System.out.println("[DEBUG_LOG] Expected: " + expectedState);
            System.out.println("[DEBUG_LOG] Actual:   " + actualState);
        }

        Assertions.assertEquals(expectedState, actualState, "[DEBUG_LOG] Expected: " + expectedState + " Actual: " + actualState + " at line " + lineNum);
    }

    private ExpectedState parseLogLine(String line) {
        // Example: C000  4C F5 C5  JMP $C5F5                       A:00 X:00 Y:00 P:24 SP:FD PPU:  0, 21 CYC:7
        int pc = Integer.parseInt(line.substring(0, 4), 16);
        
        int aPos = line.indexOf("A:") + 2;
        int xPos = line.indexOf("X:") + 2;
        int yPos = line.indexOf("Y:") + 2;
        int pPos = line.indexOf("P:") + 2;
        int spPos = line.indexOf("SP:") + 3;

        int a = Integer.parseInt(line.substring(aPos, aPos + 2), 16);
        int x = Integer.parseInt(line.substring(xPos, xPos + 2), 16);
        int y = Integer.parseInt(line.substring(yPos, yPos + 2), 16);
        int p = Integer.parseInt(line.substring(pPos, pPos + 2), 16);
        int sp = Integer.parseInt(line.substring(spPos, spPos + 2), 16);
        return new ExpectedState(pc, a, x, y, p, sp);
    }

    private static class ExpectedState {
        final int pc, a, x, y, p, sp;

        ExpectedState(int pc, int a, int x, int y, int p, int sp) {
            this.pc = pc;
            this.a = a;
            this.x = x;
            this.y = y;
            this.p = p;
            this.sp = sp;
        }
    }
}
