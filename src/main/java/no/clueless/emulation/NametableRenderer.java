package no.clueless.emulation;

import java.awt.image.BufferedImage;

public class NametableRenderer {

    // Default system palette for fallback rendering
    private static final int[] DEFAULT_PALETTE = {
            0xFF000000, 0xFFFC0000, 0xFF00FC00, 0xFFFFFFFF
    };

    /**
     * Renders Nametable 0 ($2000-$23BF) from PPU VRAM onto a 256x240 image.
     */
    public static BufferedImage renderNametable0(byte[] vram, byte[] chrData, int[] activePaletteRgb) {
        BufferedImage frame = new BufferedImage(256, 240, BufferedImage.TYPE_INT_ARGB);
        int[] palette = (activePaletteRgb != null) ? activePaletteRgb : DEFAULT_PALETTE;

        for (int tileY = 0; tileY < 30; tileY++) {
            for (int tileX = 0; tileX < 32; tileX++) {
                // Fetch tile index from $2000 Nametable
                int nametableIndex = tileY * 32 + tileX;
                int tileId = vram[nametableIndex] & 0xFF;

                // CHR-ROM offset (16 bytes per tile)
                int tileOffset = tileId * 16;

                // Decode tile directly onto the image
                decodeTileToCanvas(chrData, tileOffset, frame, tileX * 8, tileY * 8, palette);
            }
        }
        return frame;
    }

    private static void decodeTileToCanvas(byte[] chr, int tileOffset, BufferedImage img, int startX, int startY, int[] palette) {
        for (int y = 0; y < 8; y++) {
            byte plane1 = chr[tileOffset + y];
            byte plane2 = chr[tileOffset + y + 8];

            for (int x = 0; x < 8; x++) {
                int bit1 = (plane1 >> (7 - x)) & 1;
                int bit2 = (plane2 >> (7 - x)) & 1;
                int colorIndex = (bit2 << 1) | bit1;

                img.setRGB(startX + x, startY + y, palette[colorIndex]);
            }
        }
    }
}