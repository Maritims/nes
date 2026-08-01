package no.clueless.emulation.impl.ppu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NameTableManager {
    private static final Logger log = LoggerFactory.getLogger(NameTableManager.class);
    private final NameTable[] nameTables = new NameTable[]{new NameTable(), new NameTable()};
    private       Mirroring   mode       = Mirroring.VERTICAL;

    public void setMirroring(Mirroring mode) {
        this.mode = mode;
    }

    private NameTable resolvePhysicalTable(int tableIndex) {
        return switch (mode) {
            case VERTICAL -> (tableIndex == 0 || tableIndex == 2) ? nameTables[0] : nameTables[1];
            case HORIZONTAL -> (tableIndex == 0 || tableIndex == 1) ? nameTables[0] : nameTables[1];
            case SINGLE_SCREEN_LOWER -> nameTables[0];
            case SINGLE_SCREEN_UPPER -> nameTables[1];
        };
    }

    public int read(int address) {
        var calculation = NameTableCalculation.calculate(address);
        var table       = resolvePhysicalTable(calculation.tableIndex());
        return table.read(calculation.offset());
    }

    public void write(int address, int data) {

        if (data != 32 && data != 0) {
            //System.out.println("Non-blank tile written: " + data + " at " + Integer.toHexString(address));
        }

        var calculation = NameTableCalculation.calculate(address);
        var table       = resolvePhysicalTable(calculation.tableIndex());
        table.write(calculation.offset(), data);
    }

    /**
     * Gets the tile id at the given coarse coordinates in the given name table.
     */
    public int getTileId(int nameTableIndex, int coarseX, int coarseY) {

        // The nametable index is shifted left by 10 bits to select the correct nametable.
        nameTableIndex <<= 10;

        // The coarse X coordinate is masked to 0x1F to fit in the nametable.
        coarseX &= 0x1F;

        // The coarse Y coordinate is shifted left by 5 bits to jump to the start of the row.
        coarseY = ((coarseY & 0x1F) << 5);

        // All nametables start at $2000 in PPU memory.
        // By OR-ing with the name table index, we get the correct nametable in memory.
        // Then we OR-in the coarse X and coarse Y coordinates to get the correct tile id.
        int address = 0x2000 | nameTableIndex | coarseY | coarseX;

        // We utilise read() to apply mirroring.
        return read(address);
    }

    public int getAttributePalette(int nameTableIndex, int coarseX, int coarseY) {
        // The nametable index is shifted left by 10 bits to select the correct nametable.
        nameTableIndex <<= 10;

        // The attribute data starts at $23C0 in PPU memory, since $2000-$23BF is used for tile data.
        var attributeBaseAddress = 0x23C0 | nameTableIndex;

        // The coarse Y coordinate is divided by 4 to get the correct row, then multiplied by 8 to account for 8 attributes per row.
        // The coarse X coordinate is divided by 4 to get the correct column.
        var attributeOffset = ((coarseY / 4) * 8) + (coarseX / 4);
        var attributeByte   = read(attributeBaseAddress + attributeOffset);

        // Determine the palette index based on the coarse X and coarse Y coordinates.
        var isBottomHalf = (coarseY % 4) >= 2;
        var isRightHalf  = (coarseX % 4) >= 2;
        var shift        = (isBottomHalf ? 4 : 0) + (isRightHalf ? 2 : 0);
        return (attributeByte >> shift) & 0x03;
    }

    record NameTableCalculation(int relativeAddress, int tableIndex, int offset) {
        public static NameTableCalculation calculate(int address) {
            var relativeAddress = (address - 0x2000) % 0x1000;
            var tableIndex      = relativeAddress / 0x0400;
            var offset          = relativeAddress % 0x0400;
            return new NameTableCalculation(relativeAddress, tableIndex, offset);
        }
    }
}
