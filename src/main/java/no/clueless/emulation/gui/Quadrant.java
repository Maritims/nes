package no.clueless.emulation.gui;

import no.clueless.emulation.types.UnsignedByte;

public enum Quadrant {
    TOP_LEFT(0),
    TOP_RIGHT(2),
    BOTTOM_LEFT(4),
    BOTTOM_RIGHT(6);

    private final int bitShift;

    Quadrant(int bitShift) {
        this.bitShift = bitShift;
    }

    public static Quadrant fromTileCoordinates(int x, int y) {
        var isRight = (x % 4) >= 2;
        var isBottom = (y % 4) >= 2;

        if (isBottom) {
            return isRight ? BOTTOM_RIGHT : BOTTOM_LEFT;
        } else {
            return isRight ? TOP_RIGHT : TOP_LEFT;
        }
    }

    public int extractPaletteId(UnsignedByte attributeByte) {
        return (attributeByte.intValue() >> bitShift) & 0x03;
    }
}
