package no.clueless.emulation;

import no.clueless.emulation.impl.BusImpl;
import no.clueless.emulation.impl.cartridge.CartridgeImpl;
import no.clueless.emulation.impl.cpu.Cpu6502Impl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class BlarggBranchTimingTest {

    // Maximum CPU cycles to run before failing on an infinite loop / hang
    private static final long MAX_CYCLES = 50_000_000L;

    /*@ParameterizedTest(name = "Blargg Test: {0}")
    @ValueSource(strings = {
            "1.Branch_Basics.nes",
            "2.Backward_Branch.nes",
            "3.Forward_Branch.nes"
    })
    @DisplayName("Run Blargg Branch Timing ROMs")*/
    void testBranchTimingRom(String romFilename) throws Exception {
        var romPath   = Paths.get("src/test/resources/blargg/branch_timing_tests", romFilename);
        var cpu       = new Cpu6502Impl(false);
        var cartridge = new CartridgeImpl(romPath);
        var bus       = new BusImpl(cpu, mock(), mock());
        bus.insertCartridge(cartridge);
        bus.reset();

        bus.write(0x6000, 0xFF);
        bus.write(0x6001, 0x00);
        bus.write(0x6002, 0x00);
        bus.write(0x6003, 0x00);

        var testFinished = false;

        // 3. Execution loop
        while (cpu.getClockCount() < MAX_CYCLES) {
            do {
                cpu.clock();
            } while (!cpu.isInstructionComplete());

            var status = bus.read(0x6000) & 0xFF;
            var sig1   = bus.read(0x6001) & 0xFF;
            var sig2   = bus.read(0x6002) & 0xFF;
            var sig3   = bus.read(0x6003) & 0xFF;

            // Verify the signature bytes ($DE $B0 $61) are present
            boolean signatureValid = (sig1 == 0xDE && sig2 == 0xB0 && sig3 == 0x61);

            // $80 means running. Only break if signature is valid AND status moved off $80
            if (signatureValid && status != 0x80) {
                testFinished = true;
                break;
            }
        }

        // 4. Extract result string starting at $6004
        String outputText = readBlarggString(bus);
        int    statusCode = bus.read(0x6000) & 0xFF;

        // Ensure the test completed rather than hitting max cycle timeout
        assertTrue(testFinished, () -> "Test timed out! Output so far: " + outputText);

        // Status code 0x00 indicates success
        assertEquals(0x00, statusCode, () -> "Blargg Test Failed (" + romFilename + "):\n" + outputText);
    }

    private String readBlarggString(Bus bus) {
        StringBuilder sb   = new StringBuilder();
        int           addr = 24580;

        while (addr < 0x8000) {
            int b = bus.read(addr++) & 0xFF;
            if (b == 0x00) break; // Null terminator
            sb.append((char) b);
        }
        return sb.toString().trim();
    }
}