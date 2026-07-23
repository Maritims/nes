package no.clueless.emulation.ppu;

import no.clueless.emulation.types.UnsignedByte;
import no.clueless.emulation.types.UnsignedWord;

public class NameTableBank {
    private static final UnsignedWord TABLE_SIZE = new UnsignedWord(0x0400);

    private final NameTable[] tables = new NameTable[]{new NameTable(), new NameTable()};

    private MirroringMode mirroringMode = MirroringMode.VERTICAL;

    public MirroringMode getMirroringMode() {
        return mirroringMode;
    }

    public void setMirroringMode(MirroringMode mirroringMode) {
        this.mirroringMode = mirroringMode;
    }

    /**
     * Normalizes the address to the range 0x2000-0x3FFF.
     */
    private int normalizeAddress(UnsignedWord address) {
        return (address.intValue() - 0x2000) & 0x0FFF;
    }

    public UnsignedByte read(UnsignedWord address) {
        var offset        = normalizeAddress(address);
        var slot          = offset / TABLE_SIZE.intValue();
        var tableOffset   = offset % TABLE_SIZE.intValue();
        var physicalIndex = mirroringMode.resolvePhysicalTableIndex(slot);

        return tables[physicalIndex].read(tableOffset);
    }

    public void write(UnsignedWord address, UnsignedByte value) {
        var offset        = normalizeAddress(address);
        var slot          = offset / TABLE_SIZE.intValue();
        var tableOffset   = offset % TABLE_SIZE.intValue();
        var physicalIndex = mirroringMode.resolvePhysicalTableIndex(slot);

        tables[physicalIndex].write(tableOffset, value);
    }

    public NameTable getTable(int index) {
        if (index < 0 || index >= tables.length) {
            throw new IndexOutOfBoundsException("Index must be between 0 and " + (tables.length - 1));
        }
        return tables[index];
    }
}
