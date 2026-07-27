package no.clueless.emulation;

import java.util.Optional;

public interface Mapper {
    /**
     * The ID of the mapper.
     */
    int getId();

    /**
     * Translate a CPU address into a physical index inside PRG-ROM.
     *
     * @param address A 16-bit address to translate.
     * @return A 16-bit address, or empty if the address does not map to a valid PRG-ROM bank.
     */
    Optional<Integer> mapCpuAddress(int address);

    /**
     * Translate a PPU address into a physical index inside CHR-ROM.
     *
     * @param address A 16-bit address to translate.
     * @return A 16-bit address, or empty if the address does not map to a valid CHR-ROM bank.
     */
    Optional<Integer> mapPpuAddress(int address);
}
