package no.clueless.emulation;

/**
 * Represents the audio processor unit.
 */
public interface Apu2A03 {
    /**
     * Clocks the APU.
     */
    void clock();

    int readRegister(int address);

    void writeRegister(int address, int value);

    void reset();
}
