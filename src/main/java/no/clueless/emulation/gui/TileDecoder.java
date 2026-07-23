package no.clueless.emulation.gui;

import java.awt.image.BufferedImage;

public class TileDecoder {
    private static final int   TILE_SIZE       = 8;
    private static final int[] DEFAULT_PALETTE = {
            0xFF000000, // Black
            0xFF555555, // Dark Gray
            0xFFAAAAAA, // Light Gray
            0xFFFFFFFF, // White
    };

    public static void decodeTile(byte[] tileData, int tileOffset, BufferedImage image, int startX, int startY) {
        for (var y = 0; y < TILE_SIZE; y++) {
            var plane0 = tileData[tileOffset + y] & 0xFF;
            var plane1 = tileData[tileOffset + y + TILE_SIZE] & 0xFF;

            for (var x = 0; x < TILE_SIZE; x++) {
                var bitShift   = 7 - x;
                var bit0       = (plane0 >> bitShift) & 0x01;
                var bit1       = (plane1 >> bitShift) & 0x01;
                var colorIndex = (bit1 << 1) | bit0;
                var rgb        = DEFAULT_PALETTE[colorIndex];

                image.setRGB(startX + x, startY + y, rgb);
            }
        }
    }
}
