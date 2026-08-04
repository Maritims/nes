package no.clueless.emulation;

import no.clueless.emulation.impl.BusImpl;
import no.clueless.emulation.impl.apu.Apu2A03Impl;
import no.clueless.emulation.impl.cartridge.CartridgeImpl;
import no.clueless.emulation.impl.cpu.Cpu6502Impl;
import no.clueless.emulation.impl.ppu.Ppu2C02Impl;
import no.clueless.emulation.util.SwingFrameBuffer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class BlarggBranchTimingTest {

    // Maximum CPU cycles to run before failing on an infinite loop / hang
    private static final long MAX_CYCLES = 5_000_000L;

    @ParameterizedTest(name = "Blargg Test: {0}")
    @ValueSource(strings = {
            "1.Branch_Basics.nes",
            "2.Backward_Branch.nes",
            "3.Forward_Branch.nes"
    })
    @DisplayName("Run Blargg Branch Timing ROMs")
    void testBranchTimingRom(String romFilename) throws Exception {
        var romPath     = Paths.get("src/test/resources/blargg/branch_timing_tests", romFilename);
        var cpu         = new Cpu6502Impl(mock(), false);
        var frameBuffer = new SwingFrameBuffer("NES", 3, mock());
        var ppu         = new Ppu2C02Impl(frameBuffer);
        var apu         = new Apu2A03Impl();
        var cartridge   = new CartridgeImpl(romPath);
        var bus         = new BusImpl(cpu, ppu, apu, mock(), mock());
        bus.insertCartridge(cartridge);
        bus.reset();

        // 3. Execution loop
        for (var i = 0; i < MAX_CYCLES; i++) {
            bus.clock();
        }

        // Ensure the test completed rather than hitting max cycle timeout
        var success = (bus.read(0x00F8) & 0xFF) == 0x01;
        assertTrue(success, "Test failed");
    }
}