package no.clueless.emulation.impl.ppu;

public class PaletteRAM {
    /**
     * The PPU has 32 bytes of internal palette RAM.
     */
    private final int[] paletteRAM = new int[32];

    private int getPaletteIndex(int address) {
        var index = address & 0x1F;

        return switch (index) {
            case 0x10, 0x14, 0x18, 0x1C -> index & 0x0F;
            default -> index;
        };
    }

    public int read(int address) {
        return paletteRAM[getPaletteIndex(address)];
    }

    public void write(int address, int data) {
        paletteRAM[getPaletteIndex(address)] = data;
    }
}
