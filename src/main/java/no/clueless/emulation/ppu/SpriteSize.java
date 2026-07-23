package no.clueless.emulation.ppu;

public class SpriteSize {
    private final int width;
    private final int height;

    private SpriteSize(int width, int height) {
        this.width  = width;
        this.height = height;
    }

    public static SpriteSize SIZE_8x8  = new SpriteSize(8, 8);
    public static SpriteSize SIZE_8x16 = new SpriteSize(8, 16);

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
