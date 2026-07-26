package no.clueless.emulation;

import java.util.OptionalInt;

/**
 * The mapper can be considered a train track switch. It breaks a ROM into 16 smaller 16 KB chunks known as "banks".
 */
public interface Mapper {
    int getId();

    /**
     * Translate a CPU address into a physical index inside PRG-ROM.
     *
     * @param address A 16-bit address to translate.
     * @return A 16-bit address, or empty if the address does not map to a valid PRG-ROM bank.
     */
    OptionalInt mapCpuAddress(int address);

    /**
     * Translate a PPU address into a physical index inside CHR-ROM.
     *
     * @param address A 16-bit address to translate.
     * @return A 16-bit address, or empty if the address does not map to a valid CHR-ROM bank.
     */
    OptionalInt mapPpuAddress(int address);
}
