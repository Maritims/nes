package no.clueless.emulation;

import no.clueless.emulation.gui.FrameBuffer;
import no.clueless.emulation.impl.ppu.OAM;
import no.clueless.emulation.impl.ppu.PPUCtrl;
import no.clueless.emulation.impl.ppu.PPUMask;
import no.clueless.emulation.impl.ppu.PPUStatus;

/**
 * Represents the NES Picture Processing Unit, the 2C02.
 */
public interface Ppu2C02 {
    PPUCtrl getControl();

    PPUMask getMask();

    PPUStatus getStatus();

    OAM getPrimaryOAM();

    OAM getSecondaryOAM();

    int getScanLine();

    int getCycle();

    boolean isNmi();

    boolean isVerticalBlank();

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

    int readVideoMemory(int address);

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

    FrameBuffer getFrameBuffer();
}
