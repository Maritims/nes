package no.clueless.emulation.impl.ppu;

import no.clueless.emulation.Cartridge;
import no.clueless.emulation.FrameBuffer;
import no.clueless.emulation.Ppu2C02;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ppu2C02Impl implements Ppu2C02 {
    /**
     * The PPUCTRL register ($2000, VPHB SINN).
     */
    private final        PPUCTRL   ppuctrl   = new PPUCTRL();
    /**
     * The PPUMASK register ($2001, BGRs bMmG).
     */
    private final        PPUMASK   ppumask   = new PPUMASK();
    /**
     * The PPUSTATUS register ($2002, VSO- ----).
     */
    private final        PPUSTATUS ppustatus = new PPUSTATUS();
    /**
     * The OAM read/write address ($2003).
     */
    private              int       oamaddr;
    /**
     * The OAM data ($2004).
     */
    private              OAM       oamdata;
    /**
     * The PPUSCROLL register ($2005, XXXX XXXX YYYY YYYY).
     */
    private              int       ppuscroll;
    /**
     * The VRAM address ($2006).
     */
    private              int       ppuaddr;
    /**
     * The VRAM data ($2007).
     */
    private              int       ppudata;
    /**
     * The OAM DMA high address ($4014).
     */
    private              int       oamdma;

    /**
     * The current VRAM address. In the hardware this is read from the internal 'v' register outside rendering. In software it is more practical with a dedicated field.
     */
    private final LoopyRegister currentVramAddress = new LoopyRegister();
    /**
     * The temporary VRAM address before it is transferred to the 'v' register. In the hardware this is read from the internal 't' register outside rendering. In software it is more practical with a dedicated field.
     */
    private final LoopyRegister tempVramAddress    = new LoopyRegister();

    /**
     * The fine X scroll position. In the hardware this is read from the internal 'x' register during rendering. In software, it is more practical with a dedicated field.
     */
    private int fineX;
    /**
     * The write-latch indicating whether this is the first or second write. In the hardware, this is read from the 'w' register. In software, it is more practical with a dedicated field.
     */
    private int writeLatch;

    private final PatternTable[]       patternTables    = new PatternTable[]{new PatternTable(), new PatternTable()};
    private final NameTableManager     nameTableManager = new NameTableManager();
    private final PaletteRAM           paletteRAM       = new PaletteRAM();
    private final PpuBackgroundFetcher fetcher          = new PpuBackgroundFetcher();
    private final FrameBuffer          frameBuffer;

    private int scanLine = 0;
    private int cycle    = 0;

    /**
     * The PPU data buffer.
     */
    private int dataBuffer;

    private Cartridge cartridge;
    private boolean   nmi;

    public Ppu2C02Impl(FrameBuffer frameBuffer) {
        this.frameBuffer = frameBuffer;
    }

    @Override
    public void connectToCartridge(Cartridge cartridge) {
        this.cartridge = cartridge;
    }

    public int getFinalPixelColor(int pixelColorIndex) {
        // If the 2-bit CHR pattern is 0, it is transparent -> render universal background color ($3F00)
        int paletteAddress = ((pixelColorIndex & 0x03) == 0)
                ? 0x3F00
                : (0x3F00 | pixelColorIndex);

        // Reads 6-bit NES system color index (0..63)
        int nesColorIndex = paletteRAM.read(paletteAddress);

        // Convert NES color index (0..63) to an RGB integer (0xRRGGBB) using an NES color palette lookup table
        return SystemPalette.getRgb(nesColorIndex);
    }

    public int readVramForTest(int address) {
        address &= 0x3FFF; // Mask address to 16KB PPU space

        if (address < 0x2000) {
            // CHR-ROM / CHR-RAM via Cartridge
            return cartridge != null ? cartridge.readChr(address).orElse(0) : 0;
        } else if (address < 0x3F00) {
            // Nametable RAM ($2000 - $3EFF) mapped via NameTableManager
            return nameTableManager.read(address);
        } else {
            // Palette RAM ($3F00 - $3FFF)
            int paletteAddr = address & 0x001F;
            // Mirror sprite palette backdrop addresses ($10, $14, $18, $1C) to universal backdrop ($00)
            if ((paletteAddr & 0x13) == 0x10) {
                paletteAddr &= ~0x10;
            }
            return paletteRAM.read(paletteAddr & 0xFF);
        }
    }

    @Override
    public void clock() {
        if (scanLine >= -1 && scanLine < 240) {
            if (scanLine == -1 && cycle == 1) {
                ppustatus.setVblank(false);
                ppustatus.setSprite0Hit(false);
                ppustatus.setSpriteOverflow(false);
            }
        }

        if (scanLine >= 241 && scanLine <= 261) {
            if (scanLine == 241 && cycle == 1) {
                ppustatus.setVblank(true);

                if (ppuctrl.isNmiEnabled()) {
                    nmi = true;
                }
            }
        }

        cycle++;

        if (cycle >= 341) {
            cycle = 0;
            scanLine++;
            if (scanLine >= 261) {
                scanLine = -1;
            }
        }
    }

    @Override
    public void reset() {
        fineX      = 0;
        writeLatch = 0;
        dataBuffer = 0;
        scanLine   = 0;
        cycle      = 0;
        ppustatus.write(0);
        ppuctrl.write(0);
        ppumask.write(0);
        currentVramAddress.write(0);
        tempVramAddress.write(0);

        fetcher.reset();
    }

    @Override
    public int readRegister(int address) {
        address = 0x2000 + (address & 0x0007);

        return switch (address) {
            case 0x2000, 0x2001, 0x2003, 0x2005, 0x2006 -> dataBuffer;
            case 0x2002 -> {
                // Only the 3 bits furthest to the left in the PPUSTATUS register contain status information.
                // However, when reading the PPUSTATUS register, the bottom 5 bits is expected to contain data from the previous PPU bus operation.
                var data = (ppustatus.read() & 0xE0) | (dataBuffer & 0x1F);

                // Clear the VBLANK flag.
                ppustatus.setVblank(false);

                // Clear the write-latch.
                writeLatch = 0;

                yield data;
            }
            case 0x2004 -> oamdata.read(oamaddr);
            case 0x2007 -> {
                // Reading from the PPUDATA register is delayed by one cycle.
                // Rather than returning the data in the register, data is returned from an internal data buffer.
                // The buffer is updated on every read from the PPUDATA register, but only after the previous contents have been returned to the CPU.
                var vramAddress = currentVramAddress.read() & 0x3FFF;
                var data        = dataBuffer;
                readVideoMemory(vramAddress);

                // The $3F00-$3FFF range of VRAM contains palette data on later PPUs, specifically the 2C02G, 2C02H and PAL PPUs.
                if (vramAddress >= 0x3F00) {
                    data       = paletteRAM.read(vramAddress);
                    dataBuffer = nameTableManager.read(vramAddress);
                }

                // The VRAM address is incremented after each read from the PPUDATA register.
                var increment = ppuctrl.getIncrementMode() == 0 ? 1 : 32;
                currentVramAddress.write(currentVramAddress.read() + increment);

                yield data;
            }
            default -> throw new IllegalStateException("Unexpected value: " + "%04X".formatted(address));
        };
    }

    @Override
    public void writeRegister(int address, int value) {
        address &= 0xFFFF;
        value &= 0xFF;

        switch (address) {
            case 0x2000:
                ppuctrl.write(value);
                tempVramAddress.setNameTableX((ppuctrl.getNameTableX() & 0x01) != 0);
                tempVramAddress.setNameTableY((ppuctrl.getNameTableY() & 0x02) != 0);
                break;
            case 0x2001:
                ppumask.write(value);
                break;
            case 0x2003:
                oamaddr = value;
                break;
            case 0x2004:
                oamdata.write(oamaddr, value);
                break;
            case 0x2005:
                if (writeLatch == 0) {
                    fineX = value & 0x07;
                    tempVramAddress.setCoarseX(value);
                    writeLatch = 1;
                } else {
                    tempVramAddress.setFineY(value);
                    tempVramAddress.setCoarseY(value);
                    writeLatch = 0;
                }
                break;
            case 0x2006:
                if (writeLatch == 0) {
                    var highByte = (value & 0x3F) << 8;
                    var lowByte  = tempVramAddress.read() & 0x00FF;
                    tempVramAddress.write(highByte | lowByte);
                    writeLatch = 1;
                } else {
                    var t = tempVramAddress.read();
                    t = (t & 0xFF00) | value;
                    tempVramAddress.write(t);
                    var highByte = tempVramAddress.read() & 0xFF00;
                    tempVramAddress.write(highByte | value);
                    currentVramAddress.write(t);
                    writeLatch = 0;
                }
                break;
            case 0x2007:
                writeVideoMemory(currentVramAddress.read(), value);

                var increment = ppuctrl.getIncrementMode() == 0 ? 1 : 32;
                currentVramAddress.write(currentVramAddress.read() + increment);
                break;
        }
    }

    @Override
    public boolean isNmi() {
        return nmi;
    }

    @Override
    public void handleNmi() {
        nmi = false;
    }

    public int readVideoMemory(int address) {
        address &= 0x3FFF;

        if (cartridge != null) {
            var cartridgeData = cartridge.readChr(address).orElse(null);
            if (cartridgeData != null) {
                dataBuffer = cartridgeData;
                return dataBuffer;
            }
        }

        if (address <= 0x1FFF) {
            var patternTableIndex = (address & 0x1000) >> 12;
            dataBuffer = patternTables[patternTableIndex].read(address & 0x0FFF);
        } else if (address <= 0x3EFF) {
            dataBuffer = nameTableManager.read(address);
        } else {
            dataBuffer = paletteRAM.read(address);
        }

        return dataBuffer;
    }

    private void writeVideoMemory(int address, int value) {
        value &= 0xFF;

        if (cartridge != null) {
            if (cartridge.writeChr(address, value)) {
                return;
            }
        }

        if (address <= 0x1FFF) {
            var patternTableIndex = (address & 0x1000) >> 12;
            patternTables[patternTableIndex].write(address & 0x0FFF, value);
            return;
        }

        if (address <= 0x3EFF) {
            address &= 0x0FFF;
            nameTableManager.write(address, value);
            return;
        }

        paletteRAM.write(address, value);
    }
}
