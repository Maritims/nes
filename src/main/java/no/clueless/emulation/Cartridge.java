package no.clueless.emulation;

import no.clueless.emulation.types.UnsignedWord;
import no.clueless.emulation.types.UnsignedByte;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Represents a cartridge for the Nintendo Entertainment System.
 */
public class Cartridge {
    private byte[]  prgRom;
    private byte[]  chrRom;
    private int     mapperId;
    private Mapper  mapper;
    private boolean mirrorVertical;

    private Cartridge() {}

    public static Cartridge loadFromFile(Path filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("filePath cannot be null");
        }
        if (!filePath.toFile().exists()) {
            throw new IllegalArgumentException("File does not exist");
        }
        if (!filePath.toFile().isFile()) {
            throw new IllegalArgumentException("File is not a file");
        }
        if (!filePath.toFile().canRead()) {
            throw new IllegalArgumentException("File cannot be read");
        }

        var bytes = Files.readAllBytes(filePath);

        // Verify the iNES header signature "NES" followed by MS-DOS EOF character (0x1A)
        if (bytes[0] != 'N' || bytes[1] != 'E' || bytes[2] != 'S' || bytes[3] != 0x1A) {
            throw new IllegalArgumentException("Invalid iNES file format signature.");
        }

        int prgBanks = bytes[4] & 0xFF;
        int chrBanks = bytes[5] & 0xFF;

        // Extract Mapper ID from flags
        int lowerMapper = (bytes[6] & 0xF0) >> 4;
        int upperMapper = (bytes[7] & 0xF0);
        int mapperId = upperMapper | lowerMapper;

        // Parse Mirroring configuration flag
        boolean mirrorVertical = (bytes[6] & 0x01) != 0;

        // Pinpoint where ROM data arrays begin (16-byte header + 512-byte Trainer if present)
        int dataOffset = 16;
        if ((bytes[6] & 0x04) != 0) {
            dataOffset += 512; // Skip trainer buffer if flag is set
        }

        Cartridge cart = new Cartridge();
        cart.mapperId = mapperId;
        cart.mirrorVertical = mirrorVertical;

        // Extract raw chunks
        cart.prgRom = new byte[prgBanks * 16384]; // 16KB per bank
        System.arraycopy(bytes, dataOffset, cart.prgRom, 0, cart.prgRom.length);
        dataOffset += cart.prgRom.length;

        if (chrBanks > 0) {
            cart.chrRom = new byte[chrBanks * 8192];  // 8KB per bank
            System.arraycopy(bytes, dataOffset, cart.chrRom, 0, cart.chrRom.length);
        } else {
            // If CHR bank size is 0, the cartridge contains dynamic CHR RAM instead of fixed ROM
            cart.chrRom = new byte[8192];
        }

        // Instantiate the appropriate hardware mapper based on parsed header ID
        if (mapperId == 0) {
            cart.mapper = new Mapper0(prgBanks, chrBanks);
        } else {
            throw new UnsupportedOperationException("Mapper " + mapperId + " is not implemented yet.");
        }

        return cart;
    }

    public UnsignedByte readCpu(UnsignedWord address) {
        int mappedTarget = mapper.mapCpuRead(address);
        if (mappedTarget != -1) {
            return new UnsignedByte(prgRom[mappedTarget]);
        }
        return UnsignedByte.ZERO;
    }

    public void writeCpu(UnsignedWord address, UnsignedByte data) {
        // NROM PRG-ROM is read-only; writes are ignored.
    }

    // Expose these for when we build out the PPU bus connection later!
    public UnsignedByte readPpu(UnsignedWord address) {
        int mappedTarget = mapper.mapPpuRead(address);
        if (mappedTarget != -1) {
            return new UnsignedByte(chrRom[mappedTarget]);
        }
        return UnsignedByte.ZERO;
    }
}
