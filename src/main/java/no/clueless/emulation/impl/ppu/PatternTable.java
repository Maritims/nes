package no.clueless.emulation.impl.ppu;

public class PatternTable {
    private static final int TILE_COUNT          = 256;
    private static final int BYTES_PER_TILE      = 16;
    private static final int TABLE_SIZE_IN_BYTES = TILE_COUNT * BYTES_PER_TILE;

    private final int[] memory = new int[TABLE_SIZE_IN_BYTES];

    /**
     * Read from the pattern table.
     *
     * @param address A 16-bit address.
     * @return An 8-bit value.
     */
    public int read(int address) {
        return memory[address & 0xFFFF] & 0xFF;
    }

    /**
     * Write to the pattern table.
     *
     * @param address A 16-bit address.
     * @param data    An 8-bit value.
     */
    public void write(int address, int data) {
        memory[address & 0xFFFF] = data & 0xFF;
    }
}
