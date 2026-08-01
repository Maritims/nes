package no.clueless.emulation.impl.ppu;

public class NameTable {
    private static final int COLS                 = 32;
    private static final int ROWS                 = 30;
    private static final int NUMBER_OF_TILES      = COLS * ROWS;
    private static final int ATTRIBUTES           = 64;
    private static final int NAME_TABLE_BYTE_SIZE = NUMBER_OF_TILES + ATTRIBUTES;

    private final int[] memory = new int[NAME_TABLE_BYTE_SIZE];

    /**
     * Read from the name table. The address is masked to 0x03FF to fit in the name table.
     */
    public int read(int address) {
        return memory[address & 0x03FF] & 0xFF;
    }

    /**
     * Write to the name table. The address is masked to 0x03FF to fit in the name table.
     */
    public void write(int address, int data) {
        memory[address & 0x03FF] = data & 0xFF;
    }
}
