package no.clueless.emulation.impl.ppu;

import static no.clueless.emulation.impl.Masks.MASK_12BIT;
import static no.clueless.emulation.impl.PpuMemoryMap.NAME_TABLE_SIZE_MINUS_ONE;

public class NameTables {
    private final int[][] tables = new int[2][1024];

    private int readMirroredVertically(int address) {
        if (address <= NAME_TABLE_SIZE_MINUS_ONE) {
            return tables[0][address & NAME_TABLE_SIZE_MINUS_ONE];
        }

        if (address <= 0x07FF) {
            return tables[1][address & NAME_TABLE_SIZE_MINUS_ONE];
        }

        if (address <= 0x0BFF) {
            return tables[0][address & NAME_TABLE_SIZE_MINUS_ONE];
        }

        return tables[1][address & NAME_TABLE_SIZE_MINUS_ONE];
    }

    private int readNormally(int address) {
        if (address <= NAME_TABLE_SIZE_MINUS_ONE) {
            return tables[0][address & NAME_TABLE_SIZE_MINUS_ONE];
        }

        if (address <= 0x07FF) {
            return tables[0][address & NAME_TABLE_SIZE_MINUS_ONE];
        }

        if (address <= 0x0BFF) {
            return tables[1][address & NAME_TABLE_SIZE_MINUS_ONE];
        }

        return tables[1][address & NAME_TABLE_SIZE_MINUS_ONE];
    }

    public int read(int address, boolean isMirroredVertically) {
        address &= MASK_12BIT;
        return isMirroredVertically ? readMirroredVertically(address) : readNormally(address);
    }

    public void write(int address, int data, boolean isMirroredVertically) {
        address &= MASK_12BIT;

        if (isMirroredVertically) {
            if (address <= NAME_TABLE_SIZE_MINUS_ONE) {
                tables[0][address & NAME_TABLE_SIZE_MINUS_ONE] = data;
            } else if (address <= 0x07FF) {
                tables[1][address & NAME_TABLE_SIZE_MINUS_ONE] = data;
            } else if (address <= 0x0BFF) {
                tables[0][address & NAME_TABLE_SIZE_MINUS_ONE] = data;
            } else {
                tables[1][address & NAME_TABLE_SIZE_MINUS_ONE] = data;
            }
        } else {
            if (address <= NAME_TABLE_SIZE_MINUS_ONE) {
                tables[0][address & NAME_TABLE_SIZE_MINUS_ONE] = data;
            } else if (address <= 0x07FF) {
                tables[0][address & NAME_TABLE_SIZE_MINUS_ONE] = data;
            } else if (address <= 0x0BFF) {
                tables[1][address & NAME_TABLE_SIZE_MINUS_ONE] = data;
            } else {
                tables[1][address & NAME_TABLE_SIZE_MINUS_ONE] = data;
            }
        }
    }
}
