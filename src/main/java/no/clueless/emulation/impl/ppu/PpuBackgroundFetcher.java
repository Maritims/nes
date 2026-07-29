package no.clueless.emulation.impl.ppu;

public class PpuBackgroundFetcher {
    private int backgroundShiftPatternTableLowRegister  = 0;
    private int backgroundShiftPatternTableHighRegister = 0;
    private int backgroundShiftAttributeLowRegister     = 0;
    private int backgroundShiftAttributeHighRegister    = 0;

    private int nextTileIdByte           = 0;
    private int nextAttributeByte        = 0;
    private int nextPatternTableLowByte  = 0;
    private int nextPatternTableHighByte = 0;

    /**
     * Shifts the background shift registers left by one.
     */
    public void shiftRegistersLeft() {
        backgroundShiftPatternTableHighRegister = (backgroundShiftPatternTableHighRegister << 1) & 0xFFFF;
        backgroundShiftPatternTableLowRegister  = (backgroundShiftPatternTableLowRegister << 1) & 0xFFFF;
        backgroundShiftAttributeHighRegister    = (backgroundShiftAttributeHighRegister << 1) & 0xFFFF;
        backgroundShiftAttributeLowRegister     = (backgroundShiftAttributeLowRegister << 1) & 0xFFFF;
    }

    /**
     * Loads the next tile id and attribute byte into the background shift registers.
     */
    public void loadShiftRegisters() {
        backgroundShiftPatternTableHighRegister = (backgroundShiftPatternTableHighRegister & 0xFF00) | nextPatternTableHighByte;
        backgroundShiftPatternTableLowRegister  = (backgroundShiftPatternTableLowRegister & 0xFF00) | nextPatternTableLowByte;
        backgroundShiftAttributeHighRegister    = (backgroundShiftAttributeHighRegister & 0xFF00) | ((nextAttributeByte & 0x02) != 0 ? 0xFF : 0x00);
        backgroundShiftAttributeLowRegister     = (backgroundShiftAttributeLowRegister & 0xFF00) | ((nextAttributeByte & 0x01) != 0 ? 0xFF : 0x00);
    }

    public void performSequence(int cycle, LoopyRegister vram, NameTableManager nameTableManager) {
        var step = (cycle - 1) % 8;

        switch (step) {
            case 0:
                // Dump the tile data we fetched in the previous cycle.
                loadShiftRegisters();

                // Fetch the next tile id.
                nextTileIdByte = nameTableManager.getTileId(vram.getNameTableIndex(), vram.getCoarseX(), vram.getCoarseY());
                break;
            case 2:
                nextAttributeByte = nameTableManager.getAttributePalette(vram.getNameTableIndex(), vram.getCoarseX(), vram.getCoarseY());
                break;
            case 4:
                // TODO: Implement pattern table fetch.
                break;
            case 6:
                // TODO: Implement pattern table fetch.
                break;
            case 7:
                // TODO: Implement scroll.
                break;
        }
    }
}
