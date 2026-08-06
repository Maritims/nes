package no.clueless.emulation.impl.ppu.event;

import java.util.EventListener;

@FunctionalInterface
public interface PixelListener extends EventListener {
    void setPixel(int x, int y, int rgb);
}
