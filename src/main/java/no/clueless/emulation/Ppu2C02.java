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
}
