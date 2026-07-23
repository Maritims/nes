package no.clueless.emulation;

import no.clueless.emulation.cartridge.CartridgeLoader;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.nio.file.Paths;

class PatternTableViewerTest {
    @Test
    public void launch() throws InterruptedException {
        var romPath   = Paths.get("src/test/resources/Super Mario Bros. (Japan, USA).nes");
        var cartridge = CartridgeLoader.load(romPath);

        SwingUtilities.invokeLater(() -> {
            var frame = new PatternTableFrame(cartridge.getCharacterReadOnlyMemory());
            frame.setVisible(true);
        });

        // 4. Keep the test thread alive while inspecting the window
        System.out.println("Pattern Table Viewer launched! Close the window to complete test.");
        Thread.sleep(10_000); // Keeps test active for 10 seconds (or use a CountDownLatch)
    }
}