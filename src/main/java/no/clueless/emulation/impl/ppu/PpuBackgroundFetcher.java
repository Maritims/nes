package no.clueless.emulation.impl.ppu;

import no.clueless.emulation.Cartridge;

/**
 * The background fetcher acts as an assembly line which prepares data for rendering to the screen. It operates in three stages:
 * <ul>
 *     <li>The Output Stage: By shifting registers every single clock cycle a pixel is sent to a rendering engine.</li>
 *     <li>The Pipeline Stage: The fetcher goes through an 8-cycle sequence in which it prepares the next 8 pixels to be passed on to the output stage.</li>
 *     <li>The Load Stage: At the end of every 8-cycle sequence 8 newly fetched pixels are dumped into the internal variables, and coarse X is incremented.</li>
 * </ul>
 */
public class PpuBackgroundFetcher {
    /**
     * Bitplane 0.
     */
    private int bgPatternShiftLow    = 0;
    /**
     * Bitplane 1.
     */
    private int bgPatternShiftHigh   = 0;
    /**
     * Bitplane 2.
     */
    private int bgAttributeShiftLow  = 0;
    /**
     * Bitplane 3.
     */
    private int bgAttributeShiftHigh = 0;

    private int nextTileIdByte           = 0;
    private int nextAttributeByte        = 0;
    private int nextPatternTableLowByte  = 0;
    private int nextPatternTableHighByte = 0;

    /**
     * Shifts the background shift registers left by one.
     */
    public void shiftRegistersLeft() {
        bgPatternShiftHigh   = (bgPatternShiftHigh << 1) & 0xFFFF;
        bgPatternShiftLow    = (bgPatternShiftLow << 1) & 0xFFFF;
        bgAttributeShiftHigh = (bgAttributeShiftHigh << 1) & 0xFFFF;
        bgAttributeShiftLow  = (bgAttributeShiftLow << 1) & 0xFFFF;
    }

    /**
     * Loads the next tile id and attribute byte into the background shift registers.
     */
    public void loadShiftRegisters() {
        bgPatternShiftHigh   = (bgPatternShiftHigh & 0xFF00) | nextPatternTableHighByte;
        bgPatternShiftLow    = (bgPatternShiftLow & 0xFF00) | nextPatternTableLowByte;
        bgAttributeShiftHigh = (bgAttributeShiftHigh & 0xFF00) | ((nextAttributeByte & 0x02) != 0 ? 0xFF : 0x00);
        bgAttributeShiftLow  = (bgAttributeShiftLow & 0xFF00) | ((nextAttributeByte & 0x01) != 0 ? 0xFF : 0x00);
    }

    /**
     * Calculates the CHR-ROM/RAM memory address for a specific bitplane row of a tile.
     *
     * @param tileId                        The 8-bit name table tile index.
     * @param fineY                         The scanline row offset inside the 8x8 tile.
     * @param bitplane                      0 for low bitplane, 1 for high bitplane.
     * @param backgroundPatternTableAddress The background pattern table address.
     * @return The calculated address.
     */
    private int calculateChrPatternAddress(int tileId, int fineY, int bitplane, int backgroundPatternTableAddress) {
        // Each 8x8 tile takes up 16 bytes in memory.
        var bytesPerTile = 16;

        // Bitplane 0 has no horizontal offset.
        // Bitplane 1 has a horizontal offset of 8.
        var bitplaneOffset = bitplane * 8;

        // Multiply the tileId with bytesPerTile to get the correct offset since each 8x8 tile takes up 16 bytes in memory.
        var tileBaseAddress = backgroundPatternTableAddress + (tileId * bytesPerTile);

        return tileBaseAddress + fineY + bitplaneOffset;
    }

    public void performSequence(int cycle, LoopyRegister vram, NameTableManager nameTableManager, Cartridge cartridge, int backgroundPatternTableAddress) {
        var step = (cycle - 1) % 8;
        int patternAddress;

        switch (step) {
            case 0:
                // Dump the tile data we fetched in the previous cycle, effectively outputting the data to be rendered to the screen.
                loadShiftRegisters();

                // Fetch the next tile id.
                nextTileIdByte = nameTableManager.getTileId(vram.getNameTableIndex(), vram.getCoarseX(), vram.getCoarseY());
                break;
            case 2:
                nextAttributeByte = nameTableManager.getAttributePalette(vram.getNameTableIndex(), vram.getCoarseX(), vram.getCoarseY());
                break;
            case 4:
                var lowBitplaneAddress = calculateChrPatternAddress(nextTileIdByte, vram.getFineY(), 0, backgroundPatternTableAddress);
                nextPatternTableLowByte = cartridge.readChr(lowBitplaneAddress).orElse(0);
                break;
            case 6:
                var highBitplaneAddress = calculateChrPatternAddress(nextTileIdByte, vram.getFineY(), 1, backgroundPatternTableAddress);
                nextPatternTableHighByte = cartridge.readChr(highBitplaneAddress).orElse(0);
                break;
            case 7:
                vram.incrementCoarseX();
                break;
        }
    }

    public int getPixelColorIndex(int fineX) {
        var shift = 15 - fineX;

        // Bit 0 (weight 1).
        var lowBitplanePixel  = (bgPatternShiftLow >> shift) & 0x01;

        // Bit 1 (weight 2).
        var highBitplanePixel = ((bgPatternShiftHigh >> shift) & 0x01) << 1;

        // Bit 3 (weight 4).
        var lowPalette        = ((bgAttributeShiftLow >> shift) & 0x01) << 2;

        // Bit 4 (weight 8).
        var highPalette       = ((bgAttributeShiftHigh >> shift) & 0x01) << 3;

        return (highPalette | lowPalette | highBitplanePixel | lowBitplanePixel) & 0x0F;
    }
}
