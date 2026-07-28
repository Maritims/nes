package no.clueless.emulation;

import java.util.function.IntConsumer;

public interface Mapper {

    /**
     * Maps a CPU read operation.
     *
     * @param address  A 16-bit address.
     * @param callback callback to be called with the mapped address.
     * @return true if the CPU read operation was mapped, false otherwise.
     */
    boolean mapCpuRead(int address, IntConsumer callback);

    /**
     * Maps a CPU write operation.
     *
     * @param address  A 16-bit address.
     * @param callback callback to be called with the mapped address.
     * @return true if the CPU write operation was mapped, false otherwise.
     */
    boolean mapCpuWrite(int address, IntConsumer callback);

    /**
     * Maps a PPU read operation.
     *
     * @param address  A 16-bit address.
     * @param callback callback to be called with the mapped address.
     * @return true if the PPU read operation was mapped, false otherwise.
     */
    boolean mapPpuRead(int address, IntConsumer callback);

    /**
     * Maps a PPU write operation.
     *
     * @param address  A 16-bit address.
     * @param callback callback to be called with the mapped address.
     * @return true if the PPU write operation was mapped, false otherwise.
     */
    boolean mapPpuWrite(int address, IntConsumer callback);
}
