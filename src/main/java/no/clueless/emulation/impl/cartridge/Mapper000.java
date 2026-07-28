package no.clueless.emulation.impl.cartridge;

import no.clueless.emulation.Mapper;

import java.util.function.IntConsumer;

public class Mapper000 implements Mapper {
    private final int numberOfPrgBanks;
    private final int numberOfChrBanks;

    public Mapper000(int numberOfPrgBanks, int numberOfChrBanks) {
        if (numberOfPrgBanks != 1 && numberOfPrgBanks != 2) {
            throw new IllegalArgumentException("Invalid number of PRG banks: " + numberOfPrgBanks);
        }
        this.numberOfPrgBanks = numberOfPrgBanks;
        this.numberOfChrBanks = numberOfChrBanks;
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
    public boolean mapPpuWrite(int address, IntConsumer callback) {
        if (address >= 0x0000 && address <= 0x1FFF) {
            if (numberOfChrBanks == 0) {
                callback.accept(address);
                return true;
            }
        }

        return false;
    }
}