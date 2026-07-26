package no.clueless.emulation.impl.cartridge;

import no.clueless.emulation.Cartridge;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Loads a cartridge from a file.
 */
public class CartridgeLoader {
    /**
     * Verifies the iNES header signature "NES" followed by MS-DOS EOF character (0x1A).
     */
    public static boolean isValidHeaderSignature(byte[] cartData) {
        return cartData[0] == 'N' &&
                cartData[1] == 'E' &&
                cartData[2] == 'S' &&
                cartData[3] == 0x1A;
    }

    public static Cartridge load(byte[] cartData) {
        return new CartridgeImpl(cartData);
    }

    public static Cartridge load(Path path) {
        byte[] cartData;
        try {
            cartData = Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + path);
        }
        return load(cartData);
    }
}
