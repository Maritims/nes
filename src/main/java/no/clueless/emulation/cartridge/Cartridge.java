package no.clueless.emulation.cartridge;

import no.clueless.emulation.types.UnsignedWord;
import no.clueless.emulation.types.UnsignedByte;

/**
 * Represents a cartridge for the Nintendo Entertainment System.
 */
public class Cartridge {
    private final PrgRom  prgRom;
    private final ChrRom  chrRom;
    private final int     mapperId;
    private final Mapper  mapper;
    private final boolean mirroredVertically;

    public Cartridge(PrgRom prgRom, ChrRom chrRom, int mapperId, Mapper mapper, boolean mirroredVertically) {
        this.prgRom             = prgRom;
        this.chrRom             = chrRom;
        this.mapperId           = mapperId;
        this.mapper             = mapper;
        this.mirroredVertically = mirroredVertically;
    }

    public ChrRom getChrRom() {
        return chrRom;
    }

    public UnsignedByte readChrRom(UnsignedWord address) {
        var mappedAddress = mapper.mapPpuRead(address);
        return mappedAddress == null || mappedAddress.intValue() >= chrRom.getSize() ? UnsignedByte.ZERO : chrRom.read(mappedAddress);
    }

    public UnsignedByte readCpu(UnsignedWord address) {
        var mappedAddress = mapper.mapCpuRead(address);
        return mappedAddress == null ? UnsignedByte.ZERO : prgRom.readByte(mappedAddress);
    }
}
