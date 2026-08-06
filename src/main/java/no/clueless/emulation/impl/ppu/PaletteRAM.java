package no.clueless.emulation.impl.ppu;

import java.util.function.BooleanSupplier;

public class PaletteRAM {
    private final int[]           memory = new int[32];
    private final BooleanSupplier grayscale;

    /**
     * Creates a new palette RAM.
     * @param grayscale A {@link BooleanSupplier} that returns true if the PPU is in grayscale mode, false otherwise.
     */
    public PaletteRAM(BooleanSupplier grayscale) {
        this.grayscale = grayscale;
    }

    /**
     * Normalizes the given address to fit in the palette RAM.
     *
     * @param address The address to normalize.
     * @return The normalized address.
     */
    private int normalizeAddress(int address) {
        address &= 0x001F;
        if (address == 0x0010) address = 0x0000;
        if (address == 0x0014) address = 0x0004;
        if (address == 0x0018) address = 0x0008;
        if (address == 0x001C) address = 0x000C;
        return address;
    }

    /**
     * Reads a byte from the palette RAM.
     *
     * @param address The address to read from. The address will be normalized by {@link #normalizeAddress(int)} to fit in the palette RAM.
     * @return The byte read.
     */
    public int read(int address) {
        address = normalizeAddress(address);
        return memory[address] & (grayscale.getAsBoolean() ? 0x30 : 0x3F);
    }

    /**
     * Writes a byte to the palette RAM.
     *
     * @param address The address to read from. The address will be normalized by {@link #normalizeAddress(int)} to fit in the palette RAM.
     * @param data    The byte to write.
     */
    public void write(int address, int data) {
        address         = normalizeAddress(address);
        memory[address] = data;
    }
}
