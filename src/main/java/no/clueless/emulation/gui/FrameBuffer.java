package no.clueless.emulation.gui;

public interface FrameBuffer {
    int WIDTH  = 256;
    int HEIGHT = 240;

    void setPixel(int x, int y, int rgb);

    void render();
}
