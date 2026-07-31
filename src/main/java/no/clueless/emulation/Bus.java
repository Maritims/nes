package no.clueless.emulation;

/**
 * Represents the bus.
 */
public interface Bus {
    // region Devices
    Cpu6502 getCpu();

    Ppu2C02 getPpu();

    Apu2A03 getApu();

    Cartridge getCartridge();
    // endregion

    // region System methods

    /**
     * Inserts a cartridge into the system and connects it to the PPU.
     */
    void insertCartridge(Cartridge cartridge);

    /**
     * Clocks the system.
     */
    void clock();

    /**
     * Reads an 8-bit value from a 16-bit address. Implementations should mask the address to ensure it fits.
     *
     * @param address A 16-bit address. AND with 0xFFFF to mask.
     * @return An 8-bit value.
     */
    int read(int address);

    /**
     * Writes an 8-bit value to a 16-bit address. Implementations should mask the address and data to ensure they fit.
     *
     * @param address A 16-bit address. AND with 0xFFFF to mask.
     * @param data    An 8-bit value. AND with 0xFF to mask.
     */
    void write(int address, int data);

    void reset();
}
