package no.clueless.emulation;

import no.clueless.emulation.impl.BusImpl;
import no.clueless.emulation.impl.apu.Apu2A03Impl;
import no.clueless.emulation.impl.cartridge.CartridgeImpl;
import no.clueless.emulation.impl.controller.NESControllerImpl;
import no.clueless.emulation.impl.cpu.Cpu6502Impl;
import no.clueless.emulation.impl.ppu.Ppu2C02Impl;
import no.clueless.emulation.util.SwingFrameBuffer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.mockito.Mockito.mock;

public class SuperMariosBrosTest {
    @Test
    public void run() throws IOException {
        var romPath     = Paths.get("src/test/resources/Super Mario Bros. (Japan, USA).nes");
        var cpu         = new Cpu6502Impl(mock(), false);
        var controller  = new NESControllerImpl();
        var frameBuffer = new SwingFrameBuffer("NES", 3, controller);
        var ppu         = new Ppu2C02Impl(frameBuffer);
        var apu         = new Apu2A03Impl();
        var cartridge   = new CartridgeImpl(romPath);
        var bus         = new BusImpl(cpu, ppu, apu, controller, mock());
        bus.insertCartridge(cartridge);
        bus.reset();

        var emulationThread = new Thread(() -> {
            final var targetFps    = 60.0988;
            final var optimalTime  = (long) (1_000_000_000 / targetFps);
            var       lastLoopTime = System.nanoTime();

            while (frameBuffer.isVisible()) {
                var now          = System.nanoTime();
                var updateLength = now - lastLoopTime;
                lastLoopTime = now;

                var cyclesThisFrame = 0;
                while (cyclesThisFrame < 29780) {
                    bus.clock();
                    cyclesThisFrame++;
                }

                frameBuffer.render();

                var nextLoopTime = lastLoopTime + optimalTime - System.nanoTime();
                if (nextLoopTime > 0) {
                    try {
                        Thread.sleep(nextLoopTime / 1_000_000, (int) (nextLoopTime % 1_000_000));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }, "NES-Emulation-Thread");

        emulationThread.start();
    }
}
