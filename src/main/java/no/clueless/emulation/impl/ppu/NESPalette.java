package no.clueless.emulation.impl.ppu;

import static no.clueless.emulation.util.RgbToIntConverter.convertRgbToInt;

public class NESPalette {
    private final int[] palette = new int[64];

    public NESPalette() {
        palette[0x00] = convertRgbToInt(84, 84, 84);
        palette[0x01] = convertRgbToInt(0, 30, 116);
        palette[0x02] = convertRgbToInt(8, 16, 144);
        palette[0x03] = convertRgbToInt(48, 0, 136);
        palette[0x04] = convertRgbToInt(68, 0, 100);
        palette[0x05] = convertRgbToInt(92, 0, 48);
        palette[0x06] = convertRgbToInt(84, 4, 0);
        palette[0x07] = convertRgbToInt(60, 24, 0);
        palette[0x08] = convertRgbToInt(32, 42, 0);
        palette[0x09] = convertRgbToInt(8, 58, 0);
        palette[0x0A] = convertRgbToInt(0, 64, 0);
        palette[0x0B] = convertRgbToInt(0, 60, 0);
        palette[0x0C] = convertRgbToInt(0, 50, 60);
        palette[0x0D] = convertRgbToInt(0, 0, 0);
        palette[0x0E] = convertRgbToInt(0, 0, 0);
        palette[0x0F] = convertRgbToInt(0, 0, 0);

        palette[0x10] = convertRgbToInt(152, 150, 152);
        palette[0x11] = convertRgbToInt(8, 76, 196);
        palette[0x12] = convertRgbToInt(48, 50, 236);
        palette[0x13] = convertRgbToInt(92, 30, 228);
        palette[0x14] = convertRgbToInt(136, 20, 176);
        palette[0x15] = convertRgbToInt(160, 20, 100);
        palette[0x16] = convertRgbToInt(152, 34, 32);
        palette[0x17] = convertRgbToInt(120, 60, 0);
        palette[0x18] = convertRgbToInt(84, 90, 0);
        palette[0x19] = convertRgbToInt(40, 114, 0);
        palette[0x1A] = convertRgbToInt(8, 124, 0);
        palette[0x1B] = convertRgbToInt(0, 118, 40);
        palette[0x1C] = convertRgbToInt(0, 102, 120);
        palette[0x1D] = convertRgbToInt(0, 0, 0);
        palette[0x1E] = convertRgbToInt(0, 0, 0);
        palette[0x1F] = convertRgbToInt(0, 0, 0);

        palette[0x20] = convertRgbToInt(236, 238, 236);
        palette[0x21] = convertRgbToInt(76, 154, 236);
        palette[0x22] = convertRgbToInt(120, 124, 236);
        palette[0x23] = convertRgbToInt(176, 98, 236);
        palette[0x24] = convertRgbToInt(228, 84, 236);
        palette[0x25] = convertRgbToInt(236, 88, 180);
        palette[0x26] = convertRgbToInt(236, 106, 100);
        palette[0x27] = convertRgbToInt(212, 136, 32);
        palette[0x28] = convertRgbToInt(160, 170, 0);
        palette[0x29] = convertRgbToInt(116, 196, 0);
        palette[0x2A] = convertRgbToInt(76, 208, 32);
        palette[0x2B] = convertRgbToInt(56, 204, 108);
        palette[0x2C] = convertRgbToInt(56, 180, 204);
        palette[0x2D] = convertRgbToInt(60, 60, 60);
        palette[0x2E] = convertRgbToInt(0, 0, 0);
        palette[0x2F] = convertRgbToInt(0, 0, 0);

        palette[0x30] = convertRgbToInt(236, 238, 236);
        palette[0x31] = convertRgbToInt(168, 204, 236);
        palette[0x32] = convertRgbToInt(188, 188, 236);
        palette[0x33] = convertRgbToInt(212, 178, 236);
        palette[0x34] = convertRgbToInt(236, 174, 236);
        palette[0x35] = convertRgbToInt(236, 174, 212);
        palette[0x36] = convertRgbToInt(236, 180, 176);
        palette[0x37] = convertRgbToInt(228, 196, 144);
        palette[0x38] = convertRgbToInt(204, 210, 120);
        palette[0x39] = convertRgbToInt(180, 222, 120);
        palette[0x3A] = convertRgbToInt(168, 226, 144);
        palette[0x3B] = convertRgbToInt(152, 226, 180);
        palette[0x3C] = convertRgbToInt(160, 214, 228);
        palette[0x3D] = convertRgbToInt(160, 162, 160);
        palette[0x3E] = convertRgbToInt(0, 0, 0);
        palette[0x3F] = convertRgbToInt(0, 0, 0);
    }

    public int get(int i) {
        return palette[i];
    }
}
