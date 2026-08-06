package no.clueless.emulation.impl.ppu.register;

import no.clueless.emulation.Cartridge;
import no.clueless.emulation.impl.ppu.NameTables;
import no.clueless.emulation.impl.ppu.PaletteRAM;
import no.clueless.emulation.impl.ppu.PatternTable;

import static no.clueless.emulation.impl.Masks.MASK_12BIT;
import static no.clueless.emulation.impl.Masks.MASK_8BIT;
import static no.clueless.emulation.impl.PpuMemoryMap.*;
import static no.clueless.emulation.impl.PpuMemoryMap.PALETTE_RAM_END;

public class PpuBus {
    private final PatternTable[] patternTables = new PatternTable[]{new PatternTable(), new PatternTable()};
    private final NameTables     nameTables    = new NameTables();
    private final PaletteRAM     paletteRAM;

    private Cartridge cartridge;

    public PpuBus(PaletteRAM paletteRAM) {
        this.paletteRAM = paletteRAM;
    }

    public void connectToCartridge(Cartridge cartridge) {
        this.cartridge = cartridge;
    }

    public int read(int address) {
        address &= 0x3FFF;
        var data = 0;

        if (cartridge != null) {
            var cartridgeData = cartridge.readChr(address).orElse(null);
            if (cartridgeData != null) {
                return cartridgeData;
            }
        }

        if (address >= PATTERN_TABLES_START && address <= PATTERN_TABLES_END) {
            var patternTableIndex = (address & PATTERN_TABLE_SIZE) >> 12;
            data = patternTables[patternTableIndex].read(address & MASK_12BIT);
        } else if (address >= NAME_TABLE_START && address <= UNUSED_END) {
            if (cartridge != null) {
                address &= MASK_12BIT;
                data = nameTables.read(address, cartridge.isMirroredVertically());
            }
        } else if (address >= PALETTE_RAM_START && address <= PALETTE_RAM_END) {
            data = paletteRAM.read(address);
        }

        return data;
    }

    public void write(int address, int data) {
        data &= MASK_8BIT;

        if (cartridge != null) {
            if (cartridge.writeChr(address, data)) {
                return;
            }
        }

        if (address >= PATTERN_TABLES_START && address <= PATTERN_TABLES_END) {
            var patternTableIndex = (address & PATTERN_TABLE_SIZE) >> 12;
            patternTables[patternTableIndex].write(address & MASK_12BIT, data);
            return;
        }

        if (address >= NAME_TABLE_START && address <= UNUSED_END) {
            address &= MASK_12BIT;

            if (cartridge != null) {
                nameTables.write(address, data, cartridge.isMirroredVertically());
            }
        }

        if (address >= PALETTE_RAM_START && address <= PALETTE_RAM_END) {
            paletteRAM.write(address, data);
        }
    }
}
