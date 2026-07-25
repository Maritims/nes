package no.clueless.emulation.cartridge;

import no.clueless.emulation.types.UnsignedByte;
import no.clueless.emulation.types.UnsignedWord;

import java.util.Arrays;

public class ChrRom {
    private final UnsignedByte[] data;
    private final boolean        isReadOnly;

    public ChrRom(UnsignedByte[] chrRomData) {
        if (chrRomData == null) {
            throw new IllegalArgumentException("chrRomData cannot be null");
        }
        if (chrRomData.length == 0 || chrRomData.length % 4096 != 0) {
            throw new IllegalArgumentException("chrRomData must be a multiple of 4096 bytes");
        }
        this.data       = Arrays.copyOf(chrRomData, chrRomData.length);
        this.isReadOnly = true;
    }

    public UnsignedByte[] getData() {
        return data;
    }

    public int getSize() {
        return data.length;
    }

    public UnsignedByte read(UnsignedWord address) {
        return data[address.intValue() & data.length];
    }
}
