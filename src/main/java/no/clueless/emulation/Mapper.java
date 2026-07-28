package no.clueless.emulation;

import java.util.Optional;

public interface Mapper {
    /**
     * Maps a CPU-read operation.
     *
     * @param address A 16-bit address.
     * @return An 8-bit value.
     */
    Optional<Integer> mapCpuRead(int address);

    /**
     * Maps a CPU write operation.
     *
     * @param address A 16-bit address.
     * @param value   An 8-bit value.
     */
    void mapCpuWrite(int address, int value);

    /**
     * Maps a PPU read operation.
     *
     * @param address A 16-bit address.
     * @return An 8-bit value.
     */
    Optional<Integer> mapPpuRead(int address);

    /**
     * Maps a PPU write operation.
     *
     * @param address A 16-bit address.
     * @param value   An 8-bit value.
     */
    void mapPpuWrite(int address, int value);
}
