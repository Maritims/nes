package no.clueless.emulation.impl.ppu;

import java.util.Arrays;
import java.util.Objects;

import static no.clueless.emulation.impl.Masks.MASK_8BIT;

/**
 * Represents the Object Attribute Memory.
 */
public class OAM {
    private final int[][] oam;

    private OAM(int[][] oam) {
        this.oam = oam;
    }

    /**
     * 64 sprites * 4 bytes each = 256 bytes. Any content should be masked to 0xFF to fit in the OAM.
     */
    public static OAM PRIMARY   = new OAM(new int[64][4]);
    public static OAM SECONDARY = new OAM(new int[8][4]);

    /**
     * Fills the OAM with an 8-bit value.
     *
     * @param value An 8-bit value.
     */
    public void fill(int value) {
        Arrays.stream(oam).forEach(ints -> Arrays.fill(ints, value & MASK_8BIT));
    }

    /**
     * Read from OAM.
     *
     * @param address An 8-bit address. Masked to 0-255 to fit in the OAM.
     * @return An 8-bit value.
     */
    public int read(int address) {
        var flat = address & MASK_8BIT;
        var n    = flat / 4;
        var m    = flat % 4;
        return oam[n][m] & MASK_8BIT;
    }

    public int get(int n, int m) {
        return oam[n][m];
    }

    /**
     * Write to OAM.
     *
     * @param address An 8-bit address. Masked to 0-255 to fit in the OAM.
     * @param data    An 8-bit value.
     */
    public void write(int address, int data) {
        var flat = address & MASK_8BIT;
        var n    = flat / 4;
        var m    = flat % 4;
        oam[n][m] = data & MASK_8BIT;
    }

    public void set(int n, int m, int value) {
        oam[n][m] = value;
    }

    /**
     * Gets a sprite entry from the OAM.
     *
     * @param index The index of the sprite. Masked to 0-63 to fit in the OAM.
     * @return The sprite entry.
     */
    public Entry getSprite(int index) {
        var i = (index & 63);
        return new Entry(oam[i][0], oam[i][1], oam[i][2], oam[i][3]);
    }

    /**
     * Represents a sprite entry.
     *
     */
    public static final class Entry {
        private final int y;
        private final int tileIndex;
        private final int attributes;
        private       int x;

        /**
         * @param y          The Y coordinate of the sprite (byte 0).
         * @param tileIndex  The index of the tile (byte 1).
         * @param attributes The attributes of the sprite (byte 2).
         * @param x          The X coordinate of the sprite (byte 3).
         */
        public Entry(int y, int tileIndex, int attributes, int x) {
            this.y          = y;
            this.tileIndex  = tileIndex;
            this.attributes = attributes;
            this.x          = x;
        }

        public int getY() {
            return y;
        }

        public int getTileIndex() {
            return tileIndex;
        }

        public int getAttributes() {
            return attributes;
        }

        public int getX() {
            return x;
        }

        public void decrementX() {
            x -= 1;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Entry) obj;
            return this.y == that.y &&
                    this.tileIndex == that.tileIndex &&
                    this.attributes == that.attributes &&
                    this.x == that.x;
        }

        @Override
        public int hashCode() {
            return Objects.hash(y, tileIndex, attributes, x);
        }

        @Override
        public String toString() {
            return "Entry[" +
                    "y=" + y + ", " +
                    "tileIndex=" + tileIndex + ", " +
                    "attributes=" + attributes + ", " +
                    "x=" + x + ']';
        }

    }
}
