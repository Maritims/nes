package no.clueless.emulation.impl.cartridge;

import no.clueless.emulation.Mapper;

import java.util.Optional;
import java.util.function.IntConsumer;

public class Mapper000 implements Mapper {
    private final int numberOfPrgBanks;

    public Mapper000(int numberOfPrgBanks) {
        if (numberOfPrgBanks != 1 && numberOfPrgBanks != 2) {
            throw new IllegalArgumentException("Invalid number of PRG banks: " + numberOfPrgBanks);
        }
        this.numberOfPrgBanks = numberOfPrgBanks;
    }

    @Override
    public boolean mapCpuRead(int address, IntConsumer callback) {
        if (address >= 0x8000 && address <= 0xFFFF) {
            address -= 0x8000;

            var mappedAddress = address & (numberOfPrgBanks > 1 ? 0x7FFF : 0x3FFF);
            callback.accept(mappedAddress);
            return true;
        }

        return false;
    }

    @Override
    public void mapCpuWrite(int address, int value) {

    }

    @Override
    public Optional<Integer> mapPpuRead(int address) {
        // PPU reads pattern tables directly from $0000 to $1FFF (8KB)
        return address >= 0x0000 && address <= 0x1FFF ? Optional.of(address) : Optional.empty();
    }

    @Override
    public void mapPpuWrite(int address, int value) {

    }
}