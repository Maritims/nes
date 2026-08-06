package no.clueless.emulation;

import no.clueless.emulation.impl.ppu.register.OAM;

/**
 * Represents the NES Picture Processing Unit, the 2C02.
 */
public interface Ppu2C02 {
    int getScanLine();

    int getCycle();

    boolean isNmi();

    boolean isVerticalBlank();

    int getFineX();

    int getFineY();

    int getCoarseX();

    int getCoarseY();

    void connectToCartridge(Cartridge cartridge);

    void clock();

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

    int readBus(int address);

    /**
     * Writes an 8-bit value to a 16-bit address in the {@link OAM}.
     *
     * @param dmaAddress A 16-bit address.
     * @param dmaData    An 8-bit value.
     */
    void writePrimaryOAM(int dmaAddress, int dmaData);

    void clearNmi();

    boolean isFrameComplete();

    void setFrameComplete(boolean frameComplete);
}
