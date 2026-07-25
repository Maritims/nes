package no.clueless.emulation.ppu;

import no.clueless.emulation.cartridge.ChrRom;
import no.clueless.emulation.types.UnsignedByte;
import no.clueless.emulation.types.UnsignedWord;

public class PatternTableBank {
    /**
     * Table 0 ($0000 - $0FFF)
     */
    private final PatternTable table0;
    /**
     * Table 1 ($1000 - $1FFF)
     */
    private final PatternTable table1;

    public PatternTableBank(ChrRom chrRom) {
        if (chrRom == null) {
            throw new IllegalArgumentException("chrRom cannot be null");
        }
        this.table0 = new PatternTable(chrRom, 0x0000);
        this.table1 = new PatternTable(chrRom, 0x1000);
    }

    public PatternTable getTable(int tableIndex) {
        if (tableIndex != 0 && tableIndex != 1) {
            throw new IllegalArgumentException("tableIndex must be 0 or 1");
        }
        return tableIndex == 0 ? table0 : table1;
    }

    public Tile getTileAtAddress(UnsignedWord address) {
        var tableIndex        = address.intValue() < 0x1000 ? 0 : 1;
        var tileOffsetInTable = (address.intValue() % 0x1000) / 16;
        return getTable(tableIndex).getTile(tileOffsetInTable);
    }

    public UnsignedByte readByte(UnsignedWord address) {
        var normalizedAddress = address.intValue() & 0x1FFF;
        if (normalizedAddress < 0x1000) {
            return null;
        }
        return null;
    }
}
