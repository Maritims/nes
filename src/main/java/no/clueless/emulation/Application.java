package no.clueless.emulation;

import no.clueless.emulation.impl.BusImpl;
import no.clueless.emulation.impl.apu.Apu2A03Impl;
import no.clueless.emulation.impl.cartridge.CartridgeImpl;
import no.clueless.emulation.impl.controller.NESControllerImpl;
import no.clueless.emulation.impl.cpu.Cpu6502Impl;
import no.clueless.emulation.impl.ppu.Ppu2C02Impl;
import no.clueless.emulation.util.SwingFrameBuffer;

import java.io.*;

public class Application {
    public static void main(String[] args) throws IOException {
        /*var       filename = "Super Mario Bros. (Japan, USA).nes";
        var       filename = "ppu_vbl_nmi.nes";
        var       filename = "1.Branch_Basics.nes";
        var       filename = "2.Backward_Branch.nes";*/
        var       filename = "3.Forward_Branch.nes";
        Cartridge cartridge;
        try (var is = Application.class.getClassLoader().getResourceAsStream(filename)) {
            if (is == null) {
                throw new IllegalStateException("%s does not exist".formatted(filename));
            }
            var data = is.readAllBytes();
            cartridge = new CartridgeImpl(data);
        }

        var cpu         = new Cpu6502Impl(false);
        var controller  = new NESControllerImpl();
        var frameBuffer = new SwingFrameBuffer("NES", 3, controller);
        var ppu         = new Ppu2C02Impl(frameBuffer);
        var apu         = new Apu2A03Impl();
        var bus         = new BusImpl(cpu, ppu, apu, controller, null);
        bus.insertCartridge(cartridge);
        bus.reset();

        var emulationThread = new Thread(() -> {
            final var targetFps     = 60.0988;
            final var optimalTimeNs = (long) (1_000_000_000 / targetFps);
            var       nextFrameTime = System.nanoTime() + optimalTimeNs;
            var       lastStatsTime = System.nanoTime();
            var       framesCount   = 0;
            var       cyclesCount   = 0;

            while (frameBuffer.isVisible()) {
                var cyclesThisFrame = 29780;
                for (var i = 0; i < cyclesThisFrame; i++) {
                    bus.clock();
                }

                cyclesCount += cyclesThisFrame;
                frameBuffer.render();
                framesCount++;

                // Update FPS once every second (1,000,000,000 ns)
                var now       = System.nanoTime();
                var elapsedNs = now - lastStatsTime;
                if (now - lastStatsTime >= 1_000_000_000L) {
                    var currentFps = framesCount * 1_000_000_000.0 / elapsedNs;
                    var cpuMhz     = (cyclesCount / 1_000_000.0) / (elapsedNs / 1_000_000_000.0);

                    frameBuffer.setStatus(currentFps, cpuMhz);

                    framesCount   = 0;
                    cyclesCount   = 0;
                    lastStatsTime = now;
                }

                var sleepTimeNs = nextFrameTime - now;
                if (sleepTimeNs > 0) {
                    try {
                        var sleepMs = sleepTimeNs / 1_000_000;
                        var sleepNs = (int) (sleepMs % 1_000_000);
                        Thread.sleep(sleepMs, sleepNs);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }

                nextFrameTime += optimalTimeNs;
            }
        }, "NES-Emulation-Thread");

        emulationThread.start();
    }
}
