package no.clueless.emulation.impl.ppu;

import java.util.EventListener;

@FunctionalInterface
public interface PixelListener extends EventListener {
    void pixelUpdated(int x, int y, int rgb);
}
