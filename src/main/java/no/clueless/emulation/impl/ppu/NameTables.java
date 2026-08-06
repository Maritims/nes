package no.clueless.emulation.impl.ppu;

import static no.clueless.emulation.impl.Masks.MASK_12BIT;

public class NameTables {
    private final int[][] tables = new int[2][1024];

    /**
     * Returns the nametable id based on the address and whether the nametable is mirrored vertically.
     *
     * @param address              The address to calculate the nametable id for.
     * @param isMirroredVertically Whether the nametable is mirrored vertically.
     * @return The nametable id by shifting and masking out the least singificant bit in the result. The address is shifted by 10 bits if mirrored vertically, otherwise by 11 bits.
     */
    public int getNameTableId(int address, boolean isMirroredVertically) {
        address &= MASK_12BIT;
        return isMirroredVertically ? ((address >> 10) & 1) : (address >> 11) & 1;
    }

    public int read(int address, boolean isMirroredVertically) {
        address &= MASK_12BIT;
        var nameTableId = getNameTableId(address, isMirroredVertically);
        return tables[nameTableId][address & 0x03FF] & 0xFF;
    }

    public void write(int address, int data, boolean isMirroredVertically) {
        address &= MASK_12BIT;
        var nameTableId = getNameTableId(address, isMirroredVertically);
        tables[nameTableId][address & 0x03FF] = data;
    }
}
