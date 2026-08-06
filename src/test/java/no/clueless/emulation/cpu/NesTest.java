package no.clueless.emulation.cpu;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.cartridge.CartridgeImpl;
import no.clueless.emulation.impl.BusImpl;
import no.clueless.emulation.impl.cpu.Cpu6502Impl;
import no.clueless.emulation.impl.ppu.Ppu2C02Impl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.mockito.Mockito.mock;

public class NesTest {

    @Test
    public void runNesTest() throws Exception {
        var romPath   = Paths.get("src/test/resources/nestest/nestest.nes");
        var cartridge = new CartridgeImpl(romPath);
        var cpu       = new Cpu6502Impl(mock(), false);
        var bus       = new BusImpl(cpu, mock(), mock(), mock(), mock());
        bus.insertCartridge(cartridge);
        bus.reset();

        // nestest in automated mode starts at 0xC000
        cpu.setProgramCounter(0xC000);

        try (var reader = Files.newBufferedReader(Paths.get("src/test/resources/nestest/nestest.log"))) {
            String line;
            var    lineNum = 0;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                var expected = parseLogLine(line);

                // Compare current state with expected state BEFORE executing mnemonic
                assertState(lineNum, expected, cpu);

                do {
                    cpu.clock();
                } while (!cpu.isInstructionComplete());
            }
        }
    }

    private void assertState(int lineNum, ExpectedState expected, Cpu6502 cpu) {
        var actualState = String.format("PC:%04X A:%02X X:%02X Y:%02X P:%02X SP:%02X CYC:%d",
                cpu.getProgramCounter(),
                cpu.getAccumulator(),
                cpu.getX(),
                cpu.getY(),
                cpu.getStatusRegister(),
                cpu.getStackPointer(),
                cpu.getTotalClockCount());
        var expectedState = String.format("PC:%04X A:%02X X:%02X Y:%02X P:%02X SP:%02X CYC:%d",
                expected.pc, expected.a, expected.x, expected.y, expected.p, expected.sp, expected.cyc);

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

        int aPos   = line.indexOf("A:") + 2;
        int xPos   = line.indexOf("X:") + 2;
        int yPos   = line.indexOf("Y:") + 2;
        int pPos   = line.indexOf("P:") + 2;
        int spPos  = line.indexOf("SP:") + 3;
        var cycPos = line.indexOf("CYC:") + 4;

        int a   = Integer.parseInt(line.substring(aPos, aPos + 2), 16);
        int x   = Integer.parseInt(line.substring(xPos, xPos + 2), 16);
        int y   = Integer.parseInt(line.substring(yPos, yPos + 2), 16);
        int p   = Integer.parseInt(line.substring(pPos, pPos + 2), 16);
        int sp  = Integer.parseInt(line.substring(spPos, spPos + 2), 16);
        int cyc = Integer.parseInt(line.substring(cycPos), 10);
        return new ExpectedState(pc, a, x, y, p, sp, cyc);
    }

    private record ExpectedState(int pc, int a, int x, int y, int p, int sp, int cyc) {
    }

    @Test
    @DisplayName("Verify CPU writes nestest text into PPU Nametable RAM")
    public void verifyNametableContentsAfterCpuExecution() throws Exception {
        var romPath   = Paths.get("src/test/resources/Super Mario Bros. (Japan, USA).nes");
        var cartridge = new CartridgeImpl(romPath);
        var cpu       = new Cpu6502Impl(mock(), false);
        var ppu       = new Ppu2C02Impl(mock());
        var bus       = new BusImpl(cpu, ppu, mock(), mock(), mock());

        bus.insertCartridge(cartridge);
        bus.reset();

        // Step CPU forward to let nestest run its internal routines
        for (int i = 0; i < 100_000; i++) {
            cpu.clock();
            ppu.clock();
            ppu.clock();
            ppu.clock();
        }

        // Dump Nametable 0 (32 columns x 30 rows)
        System.out.println("--- NAMETABLE 0 DUMP ---");
        for (int row = 0; row < 30; row++) {
            StringBuilder sb = new StringBuilder();
            for (int col = 0; col < 32; col++) {
                int vramAddr  = 0x2000 + (row * 32) + col;
                int tileIndex = ppu.readPpuBus(vramAddr); // Read raw VRAM tile index

                // Convert tile index to ASCII character if printable, else show '.'
                char c = (tileIndex >= 32 && tileIndex <= 126) ? (char) tileIndex : '.';
                sb.append(c);
            }
            System.out.println(sb.toString());
        }
    }
}
