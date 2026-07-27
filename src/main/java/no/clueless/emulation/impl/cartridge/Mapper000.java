package no.clueless.emulation.impl.cartridge;

import no.clueless.emulation.Mapper;

import java.util.Optional;

public class Mapper000 implements Mapper {
    private static final int CPU_ADDRESS_SPACE_START = 0x8000;
    private static final int CPU_ADDRESS_SPACE_END   = 0xFFFF;
    private static final int PPU_ADDRESS_SPACE_START = 0x0000;
    private static final int PPU_ADDRESS_SPACE_END   = 0x1FFF;
    private final        int numberOfPrgBanks;

    public Mapper000(int numberOfPrgBanks) {
        if (numberOfPrgBanks != 1 && numberOfPrgBanks != 2) {
            throw new IllegalArgumentException("Invalid number of PRG banks: " + numberOfPrgBanks);
        }
        this.numberOfPrgBanks = numberOfPrgBanks;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public Optional<Integer> mapCpuAddress(int address) {
        if (address >= CPU_ADDRESS_SPACE_START && address <= CPU_ADDRESS_SPACE_END) {
            // Normalize address relative to window start ($8000)
            address -= CPU_ADDRESS_SPACE_START;

            // If 16KB (1 bank), mirror the index by wrapping around at 16KB ($4000)
            // If 32KB (2 banks), this operation leaves the index unchanged
            var mappedAddress = address & (numberOfPrgBanks > 1 ? 0x7FFF : 0x3FFF);
            return Optional.of(mappedAddress);
        }

        return Optional.empty(); // Address does not point to cartridge PRG memory
    }

    @Override
    public Optional<Integer> mapPpuAddress(int address) {
        // PPU reads pattern tables directly from $0000 to $1FFF (8KB)
        return address >= PPU_ADDRESS_SPACE_START && address <= PPU_ADDRESS_SPACE_END ? Optional.of(address) : Optional.empty();
    }
}