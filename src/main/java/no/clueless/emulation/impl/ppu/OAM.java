package no.clueless.emulation.impl.ppu;

import static no.clueless.emulation.impl.Masks.MASK_8BIT;

/**
 * Represents the Object Attribute Memory.
 */
public class OAM {
    /**
     * 64 sprites * 4 bytes each = 256 bytes. Any content should be masked to 0xFF to fit in the OAM.
     */
    private final int[] oam = new int[256];

    /**
     * Read from OAM.
     *
     * @param address An 8-bit address. Masked to 0-255 to fit in the OAM.
     * @return An 8-bit value.
     */
    public int read(int address) {
        return oam[address & MASK_8BIT] & MASK_8BIT;
    }

    /**
     * Write to OAM.
     *
     * @param address An 8-bit address. Masked to 0-255 to fit in the OAM.
     * @param data    An 8-bit value.
     */
    public void write(int address, int data) {
        oam[address & MASK_8BIT] = data & MASK_8BIT;
    }

    /**
     * Gets a sprite entry from the OAM.
     *
     * @param index The index of the sprite. Masked to 0-63 to fit in the OAM.
     * @return The sprite entry.
     */
    public Entry getSprite(int index) {
        var base = (index & 63) * 4;
        return new Entry(oam[base], oam[base + 1], oam[base + 2], oam[base + 3]);
    }

    /**
     * Represents a sprite entry.
     *
     * @param y          The Y coordinate of the sprite (byte 0).
     * @param tileIndex  The index of the tile (byte 1).
     * @param attributes The attributes of the sprite (byte 2).
     * @param x          The X coordinate of the sprite (byte 3).
     */
    public record Entry(int y, int tileIndex, int attributes, int x) {
    }
}
