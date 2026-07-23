package no.clueless.emulation.ppu;

public class Tile {
    public static final int     SIZE   = 8;
    private final       int[][] pixels = new int[SIZE][SIZE];

    /**
     * Constructor.
     *
     * @param bytes The tile data. The first half is plane 0 and the second half is plane 1. Must be exactly 16 bytes long.
     */
    public Tile(byte[] bytes) {
        if (bytes.length != 16) {
            throw new IllegalArgumentException("bytes must be 16 bytes long");
        }

        for (var row = 0; row < SIZE; row++) {
            // Row X of sheet 0 (low bits).
            var plane0 = bytes[row] & 0xFF;

            // Row X of sheet 1 (high bits).
            var plane1 = bytes[row + 8] & 0xFF;

            for (var col = 0; col < SIZE; col++) {
                var lsb = getBitAtColumn(plane0, col);
                var msb = getBitAtColumn(plane1, col);
                pixels[row][col] = combineBitsToColorIndex(msb, lsb);
            }
        }
    }

    private static int getBitAtColumn(int val, int col) {
        var pos = 7 - col;
        return (val >> pos) & 0x01;
    }

    private static int combineBitsToColorIndex(int msb, int lsb) {
        return (msb << 1) | lsb;
    }

    public int getPixel(int x, int y) {
        return pixels[y][x];
    }
}
