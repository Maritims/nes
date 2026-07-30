package no.clueless.emulation;

import java.util.function.IntConsumer;

public interface Mapper {

    default int getPrgStart() {
        return 0x8000;
    }

    default boolean isValidPrgAddress(int address) {
        return address >= getPrgStart() &&  address <= 0xFFFF;
    }

    default boolean isValidChrAddress(int address) {
        return address >= 0x0000 &&  address <= 0x1FFF;
    }

    /**
     * Maps a PRG read request (from CPU address space) to an absolute PRG offset.
     *
     * @param address  A 16-bit CPU bus address (typically 0x8000-0xFFFF).
     * @param callback Callback executed with the translated PRG-ROM/RAM array offset.
     * @return true if the address was handled by this mapper, false otherwise.
     */
    boolean mapPrgRead(int address, IntConsumer callback);

    /**
     * Maps a PRG write request (from CPU address space) or mapper register write.
     *
     * @param address  A 16-bit CPU bus address.
     * @param callback Callback executed with the translated PRG offset if writing to PRG-RAM.
     * @return true if the address was handled by this mapper, false otherwise.
     */
    boolean mapPrgWrite(int address, IntConsumer callback);

    /**
     * Maps a CHR read request (from PPU address space) to an absolute CHR offset.
     *
     * @param address  A 14-bit PPU bus address (typically 0x0000-0x1FFF).
     * @param callback Callback executed with the translated CHR-ROM/RAM array offset.
     * @return true if the address was handled by this mapper, false otherwise.
     */
    boolean mapChrRead(int address, IntConsumer callback);

    /**
     * Maps a CHR write request (from PPU address space) to an absolute CHR offset.
     *
     * @param address  A 14-bit PPU bus address (0x0000-0x1FFF).
     * @param callback Callback executed with the translated CHR-RAM array offset.
     * @return true if the address was handled by this mapper, false otherwise.
     */
    boolean mapChrWrite(int address, IntConsumer callback);
}
