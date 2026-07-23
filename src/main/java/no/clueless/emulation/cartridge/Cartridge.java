package no.clueless.emulation.cartridge;

import no.clueless.emulation.types.UnsignedWord;
import no.clueless.emulation.types.UnsignedByte;

/**
 * Represents a cartridge for the Nintendo Entertainment System.
 */
public class Cartridge {
    private final byte[]  programReadOnlyMemory;
    private final byte[]  characterReadOnlyMemory;
    private final int     mapperId;
    private final Mapper  mapper;
    private final boolean mirroredVertically;

    public Cartridge(byte[] programReadOnlyMemory, byte[] characterReadOnlyMemory, int mapperId, Mapper mapper, boolean mirroredVertically) {
        this.programReadOnlyMemory   = programReadOnlyMemory;
        this.characterReadOnlyMemory = characterReadOnlyMemory;
        this.mapperId                = mapperId;
        this.mapper                  = mapper;
        this.mirroredVertically      = mirroredVertically;
    }

    public byte[] getCharacterReadOnlyMemory() {
        return characterReadOnlyMemory;
    }

    public UnsignedByte readChrRom(UnsignedWord address) {
        var targetAddress = mapper.mapPpuRead(address);
        return targetAddress == -1 || targetAddress >= characterReadOnlyMemory.length ? UnsignedByte.ZERO : new UnsignedByte(characterReadOnlyMemory[targetAddress]);
    }

    public UnsignedByte readCpu(UnsignedWord address) {
        int mappedTarget = mapper.mapCpuRead(address);
        if (mappedTarget != -1) {
            return new UnsignedByte(programReadOnlyMemory[mappedTarget]);
        }
        return UnsignedByte.ZERO;
    }
}
