package no.clueless.emulation.impl.ppu;

import no.clueless.emulation.Cartridge;
import no.clueless.emulation.FrameBuffer;
import no.clueless.emulation.Ppu2C02;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ppu2C02Impl implements Ppu2C02 {
    private static final Logger log = LoggerFactory.getLogger(Ppu2C02Impl.class);
    /**
     * The PPUCTRL register ($2000, VPHB SINN).
     */
    private final PPUCTRL       ppuctrl;
    /**
     * The PPUMASK register ($2001, BGRs bMmG).
     */
    private final PPUMASK       ppumask;
    /**
     * The PPUSTATUS register ($2002, VSO- ----).
     */
    private final PPUSTATUS     ppustatus;
    /**
     * The OAM read/write address ($2003).
     */
    private       int       oamaddr;
    /**
     * The OAM data ($2004).
     */
    private       OAM       oamdata;
    /**
     * The PPUSCROLL register ($2005, XXXX XXXX YYYY YYYY).
     */
    private       int       ppuscroll;
    /**
     * The VRAM address ($2006).
     */
    private       int       ppuaddr;
    /**
     * The VRAM data ($2007).
     */
    private       int       ppudata;
    /**
     * The OAM DMA high address ($4014).
     */
    private       int       oamdma;

    /**
     * The current VRAM address. In the hardware this is read from the internal 'v' register outside rendering. In software it is more practical with a dedicated field.
     */
    private final LoopyRegister v;
    /**
     * The temporary VRAM address before it is transferred to the 'v' register. In the hardware this is read from the internal 't' register outside rendering. In software it is more practical with a dedicated field.
     */
    private final LoopyRegister t = new LoopyRegister();

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

    public Ppu2C02Impl(PPUCTRL ppuctrl, PPUMASK ppumask, PPUSTATUS ppustatus, LoopyRegister v, FrameBuffer frameBuffer) {
        this.ppuctrl     = ppuctrl;
        this.ppumask     = ppumask;
        this.ppustatus   = ppustatus;
        this.v           = v;
        this.frameBuffer = frameBuffer;
    }

    public Ppu2C02Impl(FrameBuffer frameBuffer) {
        this(new PPUCTRL(), new PPUMASK(), new PPUSTATUS(), new LoopyRegister(), frameBuffer);
    }

    @Override
    public boolean isNmi() {
        return nmi;
    }

    public void setScanLine(int scanLine) {
        this.scanLine = scanLine;
    }

    public void setCycle(int cycle) {
        this.cycle = cycle;
    }

    @Override
    public void connectToCartridge(Cartridge cartridge) {
        this.cartridge = cartridge;

        if (this.cartridge.isMirroredVertically()) {
            nameTableManager.setMirroring(Mirroring.VERTICAL);
        } else {
            nameTableManager.setMirroring(Mirroring.HORIZONTAL);
        }
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

    @Override
    public void clock() {
        // region Phase 1
        if (scanLine >= -1 && scanLine < 240) {
            if (scanLine == -1 && cycle == 1) {
                ppustatus.setVblank(false);
                ppustatus.setSprite0Hit(false);
                ppustatus.setSpriteOverflow(false);
            }
        }

        if (scanLine >= -1 && cycle >= 280 && cycle <= 304) {
            if (ppumask.isBackgroundRenderingEnabled() || ppumask.isSpriteRenderingEnabled()) {
                v.transferVerticalBits(t);
            }
        }
        // endregion

        // region Phase 2
        var isRenderingEnabled = ppumask.isBackgroundRenderingEnabled() || ppumask.isSpriteRenderingEnabled();

        if (isRenderingEnabled) {
            if (cycle >= 1 && cycle <= 256 && scanLine >= 0 && scanLine <= 239) {
                // Shift registers to the left to feed the pixel to the screen.
                fetcher.shiftRegistersLeft();

                // Run the background fetcher pipeline to load the next tile's pattern and attribute data.
                fetcher.performSequence(cycle, v, nameTableManager, cartridge, ppuctrl.getBackgroundPatternTableAddress());

                // Render
                var pixelColorIndex = fetcher.getPixelColorIndex(fineX);
                var finalPixelColor = getFinalPixelColor(pixelColorIndex);

                frameBuffer.setPixel(cycle - 1, scanLine, finalPixelColor);
            }

            if (scanLine >= -1 && scanLine <= 239) {
                if (scanLine >= 0 && cycle == 256) {
                    // Increment fine Y since we've now finished rendering an entire row of pixels.
                    v.incrementFineY();
                } else if (cycle == 257) {
                    // Transfer the horizontal bits so that the next scanline starts at the correct left horizontal offset.
                    v.transferHorizontalBits(t);
                } else if (cycle >= 321 && cycle <= 340) {
                    // Pre-fetch the first two tiles for the next scanline.
                    fetcher.shiftRegistersLeft();
                    fetcher.performSequence(cycle, v, nameTableManager, cartridge, ppuctrl.getBackgroundPatternTableAddress());
                }
            }
        }
        // endregion

        // region Phase 3
        if (scanLine == 240) {
            // The post render scanline. Nothing happens here.
        }
        // endregion

        // region Phase 4
        if (scanLine >= 241 && scanLine <= 260 && cycle == 1) {
            ppustatus.setVblank(true);
            if (ppuctrl.isNmiEnabled()) {
                nmi = true;
            }
        }
        // endregion

        cycle++;
        if (cycle >= 341) {
            cycle = 0;
            scanLine++;
            if (scanLine > 261) {
                scanLine = -1;
                frameBuffer.render();
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
        v.write(0);
        t.write(0);

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
                var vramAddress = v.read() & 0x3FFF;
                var data        = dataBuffer;
                readVideoMemory(vramAddress);

                // The $3F00-$3FFF range of VRAM contains palette data on later PPUs, specifically the 2C02G, 2C02H and PAL PPUs.
                if (vramAddress >= 0x3F00) {
                    data       = paletteRAM.read(vramAddress);
                    dataBuffer = nameTableManager.read(vramAddress);
                }

                // The VRAM address is incremented after each read from the PPUDATA register.
                var increment = ppuctrl.getIncrementMode() == 0 ? 1 : 32;
                v.write(v.read() + increment);

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
                t.setNameTableX((ppuctrl.getNameTableX() & 0x01) != 0);
                t.setNameTableY((ppuctrl.getNameTableY() & 0x02) != 0);
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
                    t.setCoarseX(value);
                    writeLatch = 1;
                } else {
                    t.setFineY(value);
                    t.setCoarseY(value);
                    writeLatch = 0;
                }
                break;
            case 0x2006:
                if (writeLatch == 0) {
                    var highByte = (value & 0x3F) << 8;
                    var lowByte  = t.read() & 0x00FF;
                    t.write(highByte | lowByte);
                    writeLatch = 1;
                } else {
                    var t = this.t.read();
                    t = (t & 0xFF00) | value;
                    this.t.write(t);
                    var highByte = this.t.read() & 0xFF00;
                    this.t.write(highByte | value);
                    v.write(t);
                    writeLatch = 0;
                }
                break;
            case 0x2007:
                writeVideoMemory(v.read(), value);

                var increment = ppuctrl.getIncrementMode() == 0 ? 1 : 32;
                v.write(v.read() + increment);
                break;
        }
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
