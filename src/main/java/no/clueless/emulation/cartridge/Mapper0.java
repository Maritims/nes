package no.clueless.emulation.cartridge;

import no.clueless.emulation.types.UnsignedWord;

public class Mapper0 implements Mapper {
    private final int prgBanks;
    private final int chrBanks;

    public Mapper0(int prgBanks, int chrBanks) {
        this.prgBanks = prgBanks;
        this.chrBanks = chrBanks;
    }

    @Override
    public UnsignedWord mapCpuRead(UnsignedWord address) {
        int addr = address.intValue();

        if (addr >= 0x8000 && addr <= 0xFFFF) {
            // Normalize address relative to window start ($8000)
            int relativeAddr = addr - 0x8000;

            // If 16KB (1 bank), mirror the index by wrapping around at 16KB ($4000)
            // If 32KB (2 banks), this operation leaves the index unchanged
            if (prgBanks == 1) {
                return new UnsignedWord(relativeAddr % 0x4000);
            } else {
                return new UnsignedWord(relativeAddr);
            }
        }

        return null; // Address does not point to cartridge PRG memory
    }

    @Override
    public UnsignedWord mapPpuRead(UnsignedWord address) {
        var addr = address.intValue();

        // PPU reads pattern tables directly from $0000 to $1FFF (8KB)
        if (addr >= 0x0000 && addr <= 0x1FFF) {
            return address;
        }

        return null;
    }
}