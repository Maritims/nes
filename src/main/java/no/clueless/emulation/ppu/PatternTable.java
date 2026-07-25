package no.clueless.emulation.ppu;

import no.clueless.emulation.cartridge.ChrRom;
import no.clueless.emulation.types.UnsignedByte;
import no.clueless.emulation.types.UnsignedWord;

import java.util.Arrays;

public class PatternTable {
    private final Tile[] tiles = new Tile[256];

    public PatternTable(ChrRom chrRom, int tableOffset) {
        if (chrRom == null) {
            throw new IllegalArgumentException("chrRom cannot be null");
        }
        if (tableOffset < 0 || tableOffset + (256 * 16) > chrRom.getSize()) {
            throw new IllegalArgumentException("tableOffset must be between 0 and " + (chrRom.getSize() - 16));
        }

        for (var i = 0; i < tiles.length; i++) {
            var tileStart  = tableOffset + (i * 16);
            var tileEnd    = tileStart + 16;

            var tileData = Arrays.copyOfRange(chrRom.getData(), tileStart, tileEnd);
            tiles[i] = new Tile(tileData);
        }
    }

    public Tile getTile(int index) {
        return tiles[index & 0xFF];
    }

    public UnsignedByte readByte(int address) {
        return UnsignedByte.ZERO;
    }
}
