package no.clueless.emulation;

import no.clueless.emulation.types.UnsignedWord;
import no.clueless.emulation.types.UnsignedByte;

/**
 * Represents the bus.
 */
public interface Bus {
    /**
     * Read a byte from the bus.
     *
     * @param address The address to read from.
     * @return The byte read.
     * @throws IllegalArgumentException if the address is null.
     */
    UnsignedByte read(UnsignedWord address);

    /**
     * Write a byte to the bus.
     *
     * @param address The address to write to.
     * @param value   The value to write.
     * @throws IllegalArgumentException if the address is null.
     */
    void write(UnsignedWord address, UnsignedByte value);
}
