package no.clueless.emulation.impl.ppu;

import java.util.Arrays;

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

    public int getByte(int flatIndex) {
        var idx = flatIndex & 0x1F;
        return oam[idx / 4][idx % 4];
    }

    public void setByte(int flatIndex, int value) {
        var idx = flatIndex & 0x1F;
        oam[idx / 4][idx % 4] = value;
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
     * @param y          The Y coordinate of the sprite (byte 0).
     * @param tileIndex  The index of the tile (byte 1).
     * @param attributes The attributes of the sprite (byte 2).
     * @param x          The X coordinate of the sprite (byte 3).
     */
    public record Entry(int y, int tileIndex, int attributes, int x) {
    }
}
