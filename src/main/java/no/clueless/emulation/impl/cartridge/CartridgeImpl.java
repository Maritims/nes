package no.clueless.emulation.impl.cartridge;

import no.clueless.emulation.Cartridge;
import no.clueless.emulation.Mapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Represents a cartridge for the Nintendo Entertainment System.
 */
public class CartridgeImpl implements Cartridge {
    private final byte[]  prgRom;
    private final byte[]  chrRom;
    private final Mapper  mapper;
    private final boolean mirroredVertically;

    public CartridgeImpl(byte[] data) {
        if(data[0] != 'N' || data[1] != 'E' || data[2] != 'S' || data[3] != 0x1A) {
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
        var prgRomBanks = data[4] & 0xFF;

        // The size of the CHR-ROM in 8KB chunks is stored in the fifth byte.
        var chrRomBanks = data[5] & 0xFF;

        this.prgRom = new byte[prgRomBanks * 16384];
        this.chrRom = new byte[chrRomBanks * 8192];
        this.mapper = switch (mapperId) {
            case 0 -> new Mapper000(prgRomBanks);
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
    public Optional<Integer> readPrgRom(int address) {
        return mapper.mapCpuAddress(address).map(i -> (int) prgRom[i & 0xFFFF]);
    }

    @Override
    public Optional<Integer> readChrRom(int address) {
        return mapper.mapPpuAddress(address).map(i -> (int) chrRom[i & 0xFFFF]);
    }

    @Override
    public void reset() {

    }
}
