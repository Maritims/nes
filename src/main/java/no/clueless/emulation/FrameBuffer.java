package no.clueless.emulation;

public interface FrameBuffer {
    int WIDTH  = 256;
    int HEIGHT = 240;

    void setPixel(int x, int y, int rgb);

    void setStatus(double fps, double cpuMhz);

    void render();

    int convertRgbToInt(int red, int green, int blue);
}
