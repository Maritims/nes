package no.clueless.emulation.cartridge;

import no.clueless.emulation.types.UnsignedWord;

public interface Mapper {
    /**
     * Translate a CPU address into a physical index inside PRG-ROM.
     *
     * @param address The address to translate.
     * @return A physical inside PRG-ROM, or -1 if the address doesn't map to PRG-ROM.
     * @throws IllegalArgumentException if address is null.
     */
    UnsignedWord mapCpuRead(UnsignedWord address);

    /**
     * Translate a PPU address into a physical index inside CHR-ROM.
     *
     * @param address The address to translate.
     * @return A physical inside CHR-ROM, or -1 if the address doesn't map to CHR-ROM.
     * @throws IllegalArgumentException if address is null.
     */
    UnsignedWord mapPpuRead(UnsignedWord address);
}
