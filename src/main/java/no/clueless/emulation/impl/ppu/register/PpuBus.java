package no.clueless.emulation.impl.ppu.register;

import no.clueless.emulation.Cartridge;
import no.clueless.emulation.impl.ppu.PaletteRAM;
import no.clueless.emulation.impl.ppu.PatternTable;

import static no.clueless.emulation.impl.Masks.MASK_12BIT;
import static no.clueless.emulation.impl.Masks.MASK_8BIT;
import static no.clueless.emulation.impl.PpuMemoryMap.*;
import static no.clueless.emulation.impl.PpuMemoryMap.PALETTE_RAM_END;

public class PpuBus {
    private final PatternTable[] patternTables = new PatternTable[]{new PatternTable(), new PatternTable()};
    private final int[][]        nameTables    = new int[2][1024];
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

                if (cartridge.isMirroredVertically()) {
                    if (address <= NAME_TABLE_SIZE_MINUS_ONE) {
                        data = nameTables[0][address & NAME_TABLE_SIZE_MINUS_ONE];
                    } else if (address <= 0x07FF) {
                        data = nameTables[1][address & NAME_TABLE_SIZE_MINUS_ONE];
                    } else if (address <= 0x0BFF) {
                        data = nameTables[0][address & NAME_TABLE_SIZE_MINUS_ONE];
                    } else {
                        data = nameTables[1][address & NAME_TABLE_SIZE_MINUS_ONE];
                    }
                } else {
                    if (address <= NAME_TABLE_SIZE_MINUS_ONE) {
                        data = nameTables[0][address & NAME_TABLE_SIZE_MINUS_ONE];
                    } else if (address <= 0x07FF) {
                        data = nameTables[0][address & NAME_TABLE_SIZE_MINUS_ONE];
                    } else if (address <= 0x0BFF) {
                        data = nameTables[1][address & NAME_TABLE_SIZE_MINUS_ONE];
                    } else {
                        data = nameTables[1][address & NAME_TABLE_SIZE_MINUS_ONE];
                    }
                }
            }
        } else if (address >= PALETTE_RAM_START && address <= PALETTE_RAM_END) {
            address &= 0x001F;
            if (address == 0x0010) address = 0x0000;
            if (address == 0x0014) address = 0x0004;
            if (address == 0x0018) address = 0x0008;
            if (address == 0x001C) address = 0x000C;
            //data = paletteTable[address] & (grayscale.getAsBoolean() ? 0x30 : 0x3F);
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
                if (cartridge.isMirroredVertically()) {
                    if (address <= NAME_TABLE_SIZE_MINUS_ONE) {
                        nameTables[0][address & NAME_TABLE_SIZE_MINUS_ONE] = data;
                    } else if (address <= 0x07FF) {
                        nameTables[1][address & NAME_TABLE_SIZE_MINUS_ONE] = data;
                    } else if (address <= 0x0BFF) {
                        nameTables[0][address & NAME_TABLE_SIZE_MINUS_ONE] = data;
                    } else {
                        nameTables[1][address & NAME_TABLE_SIZE_MINUS_ONE] = data;
                    }
                } else {
                    if (address <= NAME_TABLE_SIZE_MINUS_ONE) {
                        nameTables[0][address & NAME_TABLE_SIZE_MINUS_ONE] = data;
                    } else if (address <= 0x07FF) {
                        nameTables[0][address & NAME_TABLE_SIZE_MINUS_ONE] = data;
                    } else if (address <= 0x0BFF) {
                        nameTables[1][address & NAME_TABLE_SIZE_MINUS_ONE] = data;
                    } else {
                        nameTables[1][address & NAME_TABLE_SIZE_MINUS_ONE] = data;
                    }
                }
            }
        }

        if (address >= PALETTE_RAM_START && address <= PALETTE_RAM_END) {
            address &= 0x001F;
            if (address == 0x0010) address = 0x0000;
            if (address == 0x0014) address = 0x0004;
            if (address == 0x0018) address = 0x0008;
            if (address == 0x001C) address = 0x000C;
            //paletteTable[address] = data;
            paletteRAM.write(address, data);
        }
    }
}
