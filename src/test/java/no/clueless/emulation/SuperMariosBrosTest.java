package no.clueless.emulation;

import no.clueless.emulation.impl.BusImpl;
import no.clueless.emulation.impl.apu.Apu2A03Impl;
import no.clueless.emulation.impl.cartridge.CartridgeImpl;
import no.clueless.emulation.impl.cpu.Cpu6502Impl;
import no.clueless.emulation.impl.ppu.Ppu2C02Impl;
import no.clueless.emulation.util.SwingFrameBuffer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

public class SuperMariosBrosTest {
    @Test
    public void run() throws IOException {
        var romPath     = Paths.get("src/test/resources/Super Mario Bros. (Japan, USA).nes");
        var cpu         = new Cpu6502Impl(false);
        var frameBuffer = new SwingFrameBuffer("NES", 3);
        var ppu         = new Ppu2C02Impl(frameBuffer);
        var apu         = new Apu2A03Impl();
        var cartridge   = new CartridgeImpl(romPath);
        var bus         = new BusImpl(cpu, ppu, apu);
        bus.insertCartridge(cartridge);
        bus.reset();

        // 3. Execution loop
        for (var i = 0; i < 100_000_000; i++) {
            bus.clock();
        }
    }
}
