package no.clueless.emulation;

import no.clueless.emulation.types.UInt16;
import no.clueless.emulation.types.UInt8;

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
    UInt8 read(UInt16 address);

    /**
     * Write a byte to the bus.
     *
     * @param address The address to write to.
     * @param value   The value to write.
     * @throws IllegalArgumentException if the address is null.
     */
    void write(UInt16 address, UInt8 value);
}
