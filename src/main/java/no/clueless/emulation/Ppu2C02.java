package no.clueless.emulation;

import no.clueless.emulation.gui.FrameBuffer;

/**
 * Represents the NES Picture Processing Unit, the 2C02.
 */
public interface Ppu2C02 {
    int getScanLine();

    int getCycle();

    boolean isNmi();

    boolean isVerticalBlank();

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
    int readRegister(int address);

    /**
     * Writes an 8-bit value to a 16-bit address.
     *
     * @param address A 16-bit address.
     * @param value   An 8-bit value.
     */
    void writeRegister(int address, int value);

    void clearNmi();

    boolean isFrameComplete();

    void setFrameComplete(boolean frameComplete);

    FrameBuffer getFrameBuffer();
}
