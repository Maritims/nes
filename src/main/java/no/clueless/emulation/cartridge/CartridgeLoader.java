package no.clueless.emulation.cartridge;

import no.clueless.emulation.types.UnsignedByte;

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
    static boolean isValidHeaderSignature(UnsignedByte[] cartData) {
        return cartData[0].equalsInt('N') &&
                cartData[1].equalsInt('E') &&
                cartData[2].equalsInt('S') &&
                cartData[3].equalsInt(0x1A);
    }

    /**
     * Returns the size of the PRG ROM in 16KB chunks. The size of the PRG-ROM is contained within the LSB of the 4th byte.
     */
    static int getProgramReadOnlyMemorySize(UnsignedByte[] bytes) {
        return bytes[4].intValue() & 0xFF;
    }

    /**
     * Returns the size of the CHR ROM in 8KB chunks. The size of the CHR-ROM is contained within the LSB of the 5th byte.
     */
    static int getCharacterReadOnlyMemorySize(UnsignedByte[] bytes) {
        return bytes[5].intValue() & 0xFF;
    }

    /**
     * Returns the mapper ID contained within the 6th and 7th bytes.
     */
    static int getMapperId(UnsignedByte[] bytes) {
        var lowByte  = (bytes[6].intValue() & 0xF0) >> 4;
        var highByte = (bytes[7].intValue() & 0xF0);
        return highByte | lowByte;
    }

    /**
     * Returns true if the trainer buffer is present in the ROM.
     */
    static boolean isTrainerPresent(UnsignedByte[] bytes) {
        return (bytes[6].intValue() & 0x04) != 0;
    }

    public static Cartridge load(UnsignedByte[] cartData) {
        if (!isValidHeaderSignature(cartData)) {
            throw new IllegalArgumentException("Invalid iNES file format signature.");
        }

        var mapperId = getMapperId(cartData);

        // Parse Mirroring configuration flag
        var mirroredVertically = (cartData[6].intValue() & 0x01) != 0;

        // Pinpoint where ROM data arrays begin (16-byte header + 512-byte Trainer if present).
        // Skip trainer buffer if flag is set.
        var byteOffset = 16 + (isTrainerPresent(cartData) ? 512 : 0);

        var prgRomSize = getProgramReadOnlyMemorySize(cartData);
        var prgRomData = new UnsignedByte[prgRomSize * 16384];
        System.arraycopy(cartData, byteOffset, prgRomData, 0, prgRomData.length);
        var prgRom = new PrgRom(prgRomData);

        byteOffset += prgRom.getSize();

        var chrRomSize = getCharacterReadOnlyMemorySize(cartData);
        var chrRomData = new UnsignedByte[chrRomSize * 8192];
        System.arraycopy(cartData, byteOffset, chrRomData, 0, chrRomData.length);
        var chrRom = new ChrRom(chrRomData);

        var mapper = mapperId == 0 ? new Mapper0(prgRomSize, chrRomSize) : null;

        return new Cartridge(prgRom, chrRom, mapperId, mapper, mirroredVertically);
    }

    public static Cartridge load(Path path) {
        byte[] cartData;
        try {
            cartData = Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + path);
        }
        return load(UnsignedByte.arrayOf(cartData));
    }

    public static Cartridge load(InputStream inputStream) {
        try {
            var cartData = inputStream.readAllBytes();
            return load(UnsignedByte.arrayOf(cartData));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + inputStream);
        }
    }
}
