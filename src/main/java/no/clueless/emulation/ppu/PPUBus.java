package no.clueless.emulation.ppu;

import no.clueless.emulation.cartridge.Cartridge;
import no.clueless.emulation.types.UnsignedByte;
import no.clueless.emulation.types.UnsignedWord;

public class PPUBus {
    private final NameTableBank nameTableBank;
    private final PaletteRAM    paletteRam;

    private Cartridge cartridge;

    public PPUBus(NameTableBank nameTableBank, PaletteRAM paletteRam) {
        this.nameTableBank = nameTableBank;
        this.paletteRam    = paletteRam;
    }

    public void loadCartridge(Cartridge cartridge) {
        this.cartridge = cartridge;
    }

    public UnsignedByte read(UnsignedWord address) {
        var addr = address.intValue() & 0x3FFF; // 14-bit PPU address space.

        if (addr < 0x2000) {
            return cartridge == null ? UnsignedByte.ZERO : cartridge.readChrRom(new UnsignedWord(addr));
        } else if (addr < 0x3F00) {
            return nameTableBank.read(new UnsignedWord(addr));
        } else {
            return paletteRam.read(new UnsignedWord(addr));
        }
    }

    public void write(UnsignedWord address, UnsignedByte value) {
        var addr = address.intValue() & 0x3FFF;

        if (addr < 0x2000) {
            if (cartridge != null) {
                //
            }
        } else if (addr < 0x3F00) {
            nameTableBank.write(new UnsignedWord(addr), value);
        } else {
            paletteRam.write(new UnsignedWord(addr), value);
        }
    }
}
