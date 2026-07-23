package no.clueless.emulation.gui;

import no.clueless.emulation.types.UnsignedByte;

import java.util.Arrays;

/**
 * Represents a name table in the PPU. A name table is a 1024-byte block that contains both tiles and attributes.
 * A name table defines a 32x30 grid of tiles. Each tile is 8x8 pixels and each attribute is 8 bits.
 * The grid is used by the PPU to render sprites and background tiles.
 */
public class NameTable {
    private static final int COLS            = 32;
    private static final int ROWS            = 30;
    private static final int TILE_COUNT      = COLS * ROWS;
    private static final int ATTRIBUTE_COUNT = 64;
    private static final int TOTAL_SIZE      = TILE_COUNT + ATTRIBUTE_COUNT;

    private final UnsignedByte[] tiles      = new UnsignedByte[TILE_COUNT];
    private final UnsignedByte[] attributes = new UnsignedByte[ATTRIBUTE_COUNT];

    public NameTable() {
        Arrays.fill(tiles, UnsignedByte.ZERO);
        Arrays.fill(attributes, UnsignedByte.ZERO);
    }

    /**
     * Reads a tile or attribute from the name table.
     *
     * @param offset The offset to read from. It's clamped to the range of the name table.
     *               If the offset is greater than or equal to the tile count, it's treated as an attribute since we treat the name table as one continuous 1024-byte block.
     * @return The value read from the name table.
     */
    public UnsignedByte read(int offset) {
        // Clamp the offset to the range of the name table. 0x03FF is the last addressable tile.
        offset &= (TOTAL_SIZE - 1);
        return offset >= TILE_COUNT ? attributes[offset - TILE_COUNT] : tiles[offset];
    }

    /**
     * Writes a tile or attribute to the name table.
     *
     * @param offset The offset to write to. It's clamped to the range of the name table.
     *               If the offset is greater than or equal to the tile count, it's treated as an attribute since we treat the name table as one continuous 1024-byte block.
     * @param value  The value to write to the name table.
     */
    public void write(int offset, UnsignedByte value) {
        offset &= (TOTAL_SIZE - 1);
        if (offset >= TILE_COUNT) {
            attributes[offset - TILE_COUNT] = value;
        } else {
            tiles[offset] = value;
        }
    }

    /**
     * Returns the tile index at the given coordinates.
     *
     * @param x 0-based, must be in the range [0, 31]
     * @param y 0-based, must be in the range [0, 29]
     * @return The tile index at the given coordinates.
     * @throws IndexOutOfBoundsException if x or y is out of bounds.
     */
    public UnsignedByte getTileIndex(int x, int y) {
        if (x < 0 || x >= COLS || y < 0 || y >= ROWS) {
            throw new IndexOutOfBoundsException("Tile index out of bounds: (" + x + ", " + y + ")");
        }
        return tiles[y * COLS + x];
    }

    public UnsignedByte getPaletteIndex(int x, int y) {
        if (x < 0 || x >= COLS || y < 0 || y >= ROWS) {
            throw new IndexOutOfBoundsException("Palette index out of bounds: (" + x + ", " + y + ")");
        }

        // An attribute controls a 4x4 tile region on screen (16 tiles total).
        // Which attribute controls this 4x4 tile region?
        var attributeX     = x / 4;
        var attributeY     = y / 4;
        var attributeIndex = attributeY * 8 + attributeX;
        var attributeByte  = attributes[attributeIndex];
        var quadrant       = Quadrant.fromTileCoordinates(x, y);
        var paletteId      = quadrant.extractPaletteId(attributeByte);

        return new UnsignedByte(paletteId);
    }
}
