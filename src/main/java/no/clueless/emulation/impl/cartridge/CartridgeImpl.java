package no.clueless.emulation.impl.cartridge;

import no.clueless.emulation.Cartridge;
import no.clueless.emulation.Mapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a cartridge for the Nintendo Entertainment System.
 */
public class CartridgeImpl implements Cartridge {
    private final byte[]  prgRom;
    private final byte[]  chrRom;
    private final int     numberOfPrgBanks;
    private final int     numberOfChrBanks;
    private final Mapper  mapper;
    private final boolean mirroredVertically;

    public CartridgeImpl(byte[] data) {
        if (data[0] != 'N' || data[1] != 'E' || data[2] != 'S' || data[3] != 0x1A) {
            throw new IllegalArgumentException("Invalid iNES file format signature.");
        }

        // The ID of the mapper is stored in the high nibble of the first byte, and the low nibble of the second byte.
        var mapperIdHighNibble = (data[6] & 0xF0) >> 4;
        var mapperIdLowNibble  = (data[7] & 0x0F);
        var mapperId           = mapperIdHighNibble | mapperIdLowNibble;

        // The trainer buffer is present in the ROM if the 4th bit of the 6th byte is set.
        var isTrainerPresent = (data[6] & 0x04) != 0;

        // The offset to the first byte of the PRG-ROM is stored in the 16th byte, but is offset by 512 bytes if the trainer buffer is present.
        var offset = 16 + (isTrainerPresent ? 512 : 0);

        // The size of the PRG-ROM in 16KB chunks is stored in the fourth byte.
        this.numberOfPrgBanks = data[4] & 0xFF;

        // The size of the CHR-ROM in 8KB chunks is stored in the fifth byte.
        this.numberOfChrBanks = data[5] & 0xFF;

        this.prgRom = new byte[numberOfPrgBanks * 16384];
        this.chrRom = new byte[numberOfChrBanks * 8192];
        this.mapper = switch (mapperId) {
            case 0 -> new Mapper000(numberOfPrgBanks);
            case 1 -> new Mapper001();
            default -> throw new IllegalStateException("Unexpected value: " + mapperId);
        };

        // The nametable is mirrored vertically if the 1st bit of the 6th byte is set. Otherwise, it is mirrored horizontally.
        this.mirroredVertically = (data[6] & 0x01) != 0;

        System.arraycopy(data, offset, prgRom, 0, prgRom.length);
        offset += prgRom.length;
        System.arraycopy(data, offset, chrRom, 0, chrRom.length);
    }

    public CartridgeImpl(Path path) throws IOException {
        this(Files.readAllBytes(path));
    }

    @Override
    public Optional<Integer> cpuRead(int address) {
        var data = new AtomicInteger();
        return mapper.mapCpuRead(address, (mappedAddress) -> data.set(prgRom[mappedAddress & 0xFFFF])) ? Optional.of(data.get()) : Optional.empty();
    }

    @Override
    public void cpuWrite(int address, int value) {
        address &= 0xFFFF;
        mapper.mapCpuWrite(address & 0xFFFF, mappedAddress -> prgRom[mappedAddress & 0xFFFF] = (byte) (value & 0xFF));
    }

    @Override
    public Optional<Integer> ppuRead(int address) {
        var data = new AtomicInteger();
        return mapper.mapPpuRead(address, (mappedAddress) -> data.set(chrRom[mappedAddress & 0xFFFF])) ? Optional.of(data.get()) : Optional.empty();
    }

    @Override
    public void ppuWrite(int address, int value) {
        address &= 0xFFFF;
        mapper.mapCpuWrite(address & 0xFFFF, mappedAddress -> chrRom[mappedAddress & 0xFFFF] = (byte) (value & 0xFF));
    }

    @Override
    public void reset() {

    }
}
