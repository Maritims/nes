package no.clueless.emulation.ppu;

import no.clueless.emulation.types.UnsignedByte;

import java.awt.image.BufferedImage;

public class PatternTableViewer {
    public static BufferedImage renderPatternTable(UnsignedByte[] chrRom, int bankOffset) {
        var image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);

        for (var i = 0; i < 256; i++) {
            var tileX      = (i % 16) * 8;
            var tileY      = (i / 16) * 8;
            var tileOffset = bankOffset + (i * 16);

            if (tileOffset + 16 <= chrRom.length) {
                TileDecoder.decodeTile(chrRom, tileOffset, image, tileX, tileY);
            }
        }

        return image;
    }
}
