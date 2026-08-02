package no.clueless.emulation.impl.cartridge.mappers.nrom;

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
    public boolean mapPrgRead(int address, IntConsumer callback) {
        if(isValidPrgAddress(address)) {
            address -= 0x8000;
            callback.accept(mapCpuAddress(address));
            return true;
        }

        return false;
    }

    @Override
    public boolean mapPrgWrite(int address, int ignored, IntConsumer callback) {
        if(isValidPrgAddress(address)) {
            address -= getPrgStart();
            callback.accept(mapCpuAddress(address));
            return true;
        }

        return false;
    }

    @Override
    public boolean mapChrRead(int address, IntConsumer callback) {
        if(isValidChrAddress(address)) {
            callback.accept(address);
            return true;
        }

        return false;
    }

    @Override
    public boolean mapChrWrite(int address, IntConsumer callback) {
        if(isValidChrAddress(address)) {
            if (numberOfChrBanks == 0) {
                callback.accept(address);
                return true;
            }
        }

        return false;
    }
}