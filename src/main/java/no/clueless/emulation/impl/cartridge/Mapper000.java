package no.clueless.emulation.impl.cartridge;

import no.clueless.emulation.Mapper;

import java.util.Optional;

public class Mapper000 implements Mapper {
    private final int numberOfPrgBanks;

    public Mapper000(int numberOfPrgBanks) {
        if (numberOfPrgBanks != 1 && numberOfPrgBanks != 2) {
            throw new IllegalArgumentException("Invalid number of PRG banks: " + numberOfPrgBanks);
        }
        this.numberOfPrgBanks = numberOfPrgBanks;
    }

    @Override
    public Optional<Integer> mapCpuRead(int address) {
        if (address >= 0x8000 && address <= 0xFFFF) {
            // Normalize address relative to window start ($8000)
            address -= 0x8000;

            // If 16KB (1 bank), mirror the index by wrapping around at 16KB ($4000)
            // If 32KB (2 banks), this operation leaves the index unchanged
            var mappedAddress = address & (numberOfPrgBanks > 1 ? 0x7FFF : 0x3FFF);
            return Optional.of(mappedAddress);
        }

        return Optional.empty(); // Address does not point to cartridge PRG memory
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