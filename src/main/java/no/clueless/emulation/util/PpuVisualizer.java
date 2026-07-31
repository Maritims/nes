package no.clueless.emulation.util;

import no.clueless.emulation.impl.BusImpl;
import no.clueless.emulation.impl.cartridge.CartridgeImpl;
import no.clueless.emulation.impl.cpu.Cpu6502Impl;
import no.clueless.emulation.impl.ppu.Ppu2C02Impl;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Paths;

public class PpuVisualizer {

    public static void main(String[] args) throws IOException {
        var romPath   = Paths.get("src/test/resources/nestest/nestest.nes");
        var cartridge = new CartridgeImpl(romPath);

        var cpu = new Cpu6502Impl(false);
        var ppu = new Ppu2C02Impl(null);

        // Pass cpu and ppu into your Bus
        var bus = new BusImpl(cpu, ppu, null);
        bus.insertCartridge(cartridge);
        bus.reset();

        // Standard nestest automated mode entry point
        cpu.setProgramCounter(0xC000);

        // Force PPU rendering flags ON manually (0x1E = show BG + Sprites + Left margin)
        ppu.writeRegister(0x2001, 0x1E);

        // Run continuous execution loop (~60 FPS)
        Timer timer = new Timer(16, e -> {
            // Run enough CPU/PPU cycles for roughly 1 frame (~29,780 CPU cycles)
            for (int i = 0; i < 29780; i++) {
                bus.clock();
            }
        });
        timer.start();
    }
}
