package no.clueless.emulation.ppu;

import no.clueless.emulation.types.UnsignedByte;
import no.clueless.emulation.types.UnsignedWord;

import java.util.Arrays;

/**
 * Represents the Palette RAM in the PPU.
 * <p>Holds 32 bytes of palette memory.</p>
 */
public class PaletteRAM {
    private static final int PALETTE_SIZE = 32;

    private final UnsignedByte[] ram = new UnsignedByte[PALETTE_SIZE];

    public PaletteRAM() {
        Arrays.fill(ram, UnsignedByte.ZERO);
    }

    private boolean isSpriteBackdropIndex(int index) {
        return index == 16 || index == 20 || index == 24 || index == 28;
    }

    private int normalizeAddress(UnsignedWord address) {
        // Clamp to 0-31 (handles $3F20-$3FFF mirroring).
        var index = (address.intValue() - 0x3F00) & 0x1F;

        // Sprite palette backdrop slots (indices 16, 20, 24, 28) are mirrored to Background palette backdrop slots (indices 0, 4, 8, 12).
        if(isSpriteBackdropIndex(index)) {
            index -= 16;
        }

        return index;
    }

    public UnsignedByte read(UnsignedWord address) {
        return ram[normalizeAddress(address)];
    }

    public void write(UnsignedWord address, UnsignedByte value) {
        // We only use the lower 6 bits of the value.
        ram[normalizeAddress(address)] = new UnsignedByte(value.intValue() & 0x3F);
    }
}
