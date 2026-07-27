package no.clueless.emulation;

/**
 * Represents the NES Picture Processing Unit, the 2C02.
 */
public interface Ppu2C02 {
    void connectToCartridge(Cartridge cartridge);

    /**
     * Clocks the PPU.
     */
    void clock();

    /**
     * Resets the PPU.
     */
    void reset();

    /**
     * Reads an 8-bit value from a 16-bit address.
     *
     * @param address A 16-bit address.
     * @return An 8-bit value.
     */
    int read(int address);

    /**
     * Writes an 8-bit value to a 16-bit address.
     *
     * @param address A 16-bit address.
     * @param value   An 8-bit value.
     */
    void write(int address, int value);
}
