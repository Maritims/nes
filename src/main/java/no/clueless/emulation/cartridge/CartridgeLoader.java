package no.clueless.emulation.cartridge;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Loads a cartridge from a file.
 */
public class CartridgeLoader {
    /**
     * Verifies the iNES header signature "NES" followed by MS-DOS EOF character (0x1A).
     */
    static boolean isValidHeaderSignature(byte[] bytes) {
        return bytes[0] == 'N' && bytes[1] == 'E' && bytes[2] == 'S' && bytes[3] == 0x1A;
    }

    /**
     * Returns the size of the PRG ROM in 16KB chunks. The size of the PRG-ROM is contained within the LSB of the 4th byte.
     */
    static int getProgramReadOnlyMemorySize(byte[] bytes) {
        return bytes[4] & 0xFF;
    }

    /**
     * Returns the size of the CHR ROM in 8KB chunks. The size of the CHR-ROM is contained within the LSB of the 5th byte.
     */
    static int getCharacterReadOnlyMemorySize(byte[] bytes) {
        return bytes[5] & 0xFF;
    }

    /**
     * Returns the mapper ID contained within the 6th and 7th bytes.
     */
    static int getMapperId(byte[] bytes) {
        var lowByte  = (bytes[6] & 0xF0) >> 4;
        var highByte = (bytes[7] & 0xF0);
        return highByte | lowByte;
    }

    /**
     * Returns true if the trainer buffer is present in the ROM.
     */
    static boolean isTrainerPresent(byte[] bytes) {
        return (bytes[6] & 0x04) != 0;
    }

    public static Cartridge load(byte[] bytes) {
        if (!isValidHeaderSignature(bytes)) {
            throw new IllegalArgumentException("Invalid iNES file format signature.");
        }

        var mapperId = getMapperId(bytes);

        // Parse Mirroring configuration flag
        var mirroredVertically = (bytes[6] & 0x01) != 0;

        // Pinpoint where ROM data arrays begin (16-byte header + 512-byte Trainer if present).
        // Skip trainer buffer if flag is set.
        var byteOffset = 16 + (isTrainerPresent(bytes) ? 512 : 0);

        var prgRomSize = getProgramReadOnlyMemorySize(bytes);
        var prgRom     = new byte[prgRomSize * 16384];
        System.arraycopy(bytes, byteOffset, prgRom, 0, prgRom.length);
        byteOffset += prgRom.length;

        var chrRomSize = getCharacterReadOnlyMemorySize(bytes);
        var chrRom     = new byte[chrRomSize * 8192];
        System.arraycopy(bytes, byteOffset, chrRom, 0, chrRom.length);
        byteOffset += chrRom.length;

        var mapper = mapperId == 0 ? new Mapper0(prgRomSize, chrRomSize) : null;

        return new Cartridge(prgRom, chrRom, mapperId, mapper, mirroredVertically);
    }

    public static Cartridge load(Path path) {
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + path);
        }
        return load(bytes);
    }

    public static Cartridge load(InputStream inputStream) {
        try {
            var bytes = inputStream.readAllBytes();
            return load(bytes);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + inputStream);
        }
    }
}
