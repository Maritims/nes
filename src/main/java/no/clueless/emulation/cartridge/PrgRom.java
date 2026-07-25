package no.clueless.emulation.cartridge;

import no.clueless.emulation.types.UnsignedByte;
import no.clueless.emulation.types.UnsignedWord;

import java.util.Arrays;

public class PrgRom {
    private static final int            SIZE_16K = 0x4000;
    private static final int            SIZE_32K = 0x8000;
    private final        UnsignedByte[] data;

    public PrgRom(UnsignedByte[] prgRomData) {
        if (prgRomData == null) {
            throw new IllegalArgumentException("prgRomData must not be null");
        }
        if (prgRomData.length == 0 || prgRomData.length % SIZE_16K != 0) {
            throw new IllegalArgumentException("prgRomData must be a multiple of 16KiB");
        }
        this.data = Arrays.copyOf(prgRomData, prgRomData.length);
    }

    public int getSize() {
        return data.length;
    }

    public UnsignedByte readByte(UnsignedWord address) {
        return data[address.intValue() % data.length];
    }

    public UnsignedWord readWord(UnsignedWord address) {
        var low  = readByte(address);
        var high = readByte(address.increment());
        return new UnsignedWord(low.intValue() | (high.intValue() << 8));
    }
}
