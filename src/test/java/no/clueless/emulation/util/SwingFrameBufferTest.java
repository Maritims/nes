package no.clueless.emulation.util;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;

import static org.mockito.Mockito.mock;

class SwingFrameBufferTest {
    @Test
    void run() throws InterruptedException, InvocationTargetException {
        var latch = new CountDownLatch(1);

        SwingUtilities.invokeAndWait(() -> {
            var sut = new SwingFrameBuffer("Test", 5, mock());
            var brightRed = 0xFFFF0000;

            new Timer(16, e -> {
                // Clear / draw a bright red block at [10, 10] every frame
                for (int y = 0; y < 50; y++) {
                    for (int x = 0; x < 50; x++) {
                        sut.setPixel(x, y, 0xFFFF0000); // Opaque Red
                    }
                }
                sut.render();
            }).start();
        });

        latch.await();
    }
}