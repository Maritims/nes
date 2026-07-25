package no.clueless.emulation;

import no.clueless.emulation.cartridge.CartridgeLoader;
import no.clueless.emulation.ppu.NametableRenderer;
import no.clueless.emulation.ppu.NametableViewerFrame;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.nio.file.Path;

class NametableViewerFrameTest {
    @Test
    void launch() throws InterruptedException {
        
        var cartridge = CartridgeLoader.load(Path.of("src/test/resources/Super Mario Bros. (Japan, USA).nes"));
        var vram      = new byte[960];
        for (int i = 0; i < 960; i++) {
            // Cycles through tiles 0..255 from CHR-ROM across the 32x30 screen
            vram[i] = (byte) (i & 0xFF);
        }

        SwingUtilities.invokeLater(() -> {
            var image  = NametableRenderer.renderNametable0(vram, cartridge.getChrRom().getData(), null);
            var viewer = new NametableViewerFrame();
            viewer.updateBuffer(image);
            viewer.setVisible(true);
        });

        // 4. Keep the test thread alive while inspecting the window
        System.out.println("Pattern Table Viewer launched! Close the window to complete test.");
        Thread.sleep(10_000); // Keeps test active for 10 seconds (or use a CountDownLatch)
    }

}