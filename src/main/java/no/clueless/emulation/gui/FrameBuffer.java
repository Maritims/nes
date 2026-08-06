package no.clueless.emulation.gui;

import no.clueless.emulation.impl.ppu.event.PixelListener;
import no.clueless.emulation.impl.ppu.event.RenderListener;

public interface FrameBuffer extends PixelListener, RenderListener {
    int WIDTH  = 256;
    int HEIGHT = 240;
}
