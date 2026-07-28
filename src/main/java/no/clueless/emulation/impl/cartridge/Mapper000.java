package no.clueless.emulation.impl.cartridge;

import no.clueless.emulation.Mapper;

import java.util.function.IntConsumer;

public class Mapper000 implements Mapper {
    private final int numberOfPrgBanks;

    public Mapper000(int numberOfPrgBanks) {
        if (numberOfPrgBanks != 1 && numberOfPrgBanks != 2) {
            throw new IllegalArgumentException("Invalid number of PRG banks: " + numberOfPrgBanks);
        }
        this.numberOfPrgBanks = numberOfPrgBanks;
    }

    private int mapCpuAddress(int address) {
        return address & (numberOfPrgBanks > 1 ? 0x7FFF : 0x3FFF);
    }

    @Override
    public boolean mapCpuRead(int address, IntConsumer callback) {
        if (address >= 0x8000 && address <= 0xFFFF) {
            address -= 0x8000;
            callback.accept(mapCpuAddress(address));
            return true;
        }

        return false;
    }

    @Override
    public boolean mapCpuWrite(int address, IntConsumer callback) {
        if (address >= 0x8000 && address <= 0xFFFF) {
            address -= 0x8000;
            callback.accept(mapCpuAddress(address));
            return true;
        }

        return false;
    }

    @Override
    public boolean mapPpuRead(int address, IntConsumer callback) {
        if (address >= 0x0000 && address <= 0x1FFF) {
            callback.accept(address);
            return true;
        }

        return false;
    }

    @Override
    public void mapPpuWrite(int address, int value) {

    }
}