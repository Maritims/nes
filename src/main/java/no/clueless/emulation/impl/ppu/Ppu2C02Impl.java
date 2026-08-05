package no.clueless.emulation.impl.ppu;

import no.clueless.emulation.Cartridge;
import no.clueless.emulation.gui.FrameBuffer;
import no.clueless.emulation.Ppu2C02;
import no.clueless.emulation.impl.PpuMemoryMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static no.clueless.emulation.impl.CpuMemoryMap.PPU_REGISTER_START;
import static no.clueless.emulation.impl.CpuMemoryMap.PRG_ROM_START;
import static no.clueless.emulation.impl.Masks.*;
import static no.clueless.emulation.impl.PpuMemoryMap.*;

public class Ppu2C02Impl implements Ppu2C02 {
    private static final Logger log = LoggerFactory.getLogger(Ppu2C02Impl.class);

    private final PPUCtrl   control;
    private final PPUMask   mask;
    private final PPUStatus status;
    private       int       oamaddr;
    private final OAM       primaryOAM   = OAM.PRIMARY;
    private final OAM       secondaryOAM = OAM.SECONDARY;

    private final LoopyRegister vramAddress;
    private final LoopyRegister tempVramAddress = new LoopyRegister();

    private int     fineX;
    private int     addressLatch;
    private boolean isFrameComplete;

    private final FrameBuffer     frameBuffer;
    private final PatternTable[]  patternTables = new PatternTable[]{new PatternTable(), new PatternTable()};
    private final int[][]         nameTables    = new int[2][1024];
    private final int[]           paletteTable  = new int[32];
    private final int[]           palette       = new int[64];
    private final SpriteEvaluator spriteEvaluator;

    private int       scanLine = 0;
    private int       cycle    = 0;
    private boolean   oddFrame = false;
    private int       ppuDataBuffer;
    private Cartridge cartridge;
    private boolean   nmi;

    public Ppu2C02Impl(PPUCtrl control, PPUMask mask, PPUStatus status, LoopyRegister vramAddress, FrameBuffer frameBuffer) {
        this.control         = control;
        this.mask            = mask;
        this.status          = status;
        this.vramAddress     = vramAddress;
        this.frameBuffer     = frameBuffer;
        this.spriteEvaluator = new SpriteEvaluator(this);

        palette[0x00] = frameBuffer.convertRgbToInt(84, 84, 84);
        palette[0x01] = frameBuffer.convertRgbToInt(0, 30, 116);
        palette[0x02] = frameBuffer.convertRgbToInt(8, 16, 144);
        palette[0x03] = frameBuffer.convertRgbToInt(48, 0, 136);
        palette[0x04] = frameBuffer.convertRgbToInt(68, 0, 100);
        palette[0x05] = frameBuffer.convertRgbToInt(92, 0, 48);
        palette[0x06] = frameBuffer.convertRgbToInt(84, 4, 0);
        palette[0x07] = frameBuffer.convertRgbToInt(60, 24, 0);
        palette[0x08] = frameBuffer.convertRgbToInt(32, 42, 0);
        palette[0x09] = frameBuffer.convertRgbToInt(8, 58, 0);
        palette[0x0A] = frameBuffer.convertRgbToInt(0, 64, 0);
        palette[0x0B] = frameBuffer.convertRgbToInt(0, 60, 0);
        palette[0x0C] = frameBuffer.convertRgbToInt(0, 50, 60);
        palette[0x0D] = frameBuffer.convertRgbToInt(0, 0, 0);
        palette[0x0E] = frameBuffer.convertRgbToInt(0, 0, 0);
        palette[0x0F] = frameBuffer.convertRgbToInt(0, 0, 0);

        palette[0x10] = frameBuffer.convertRgbToInt(152, 150, 152);
        palette[0x11] = frameBuffer.convertRgbToInt(8, 76, 196);
        palette[0x12] = frameBuffer.convertRgbToInt(48, 50, 236);
        palette[0x13] = frameBuffer.convertRgbToInt(92, 30, 228);
        palette[0x14] = frameBuffer.convertRgbToInt(136, 20, 176);
        palette[0x15] = frameBuffer.convertRgbToInt(160, 20, 100);
        palette[0x16] = frameBuffer.convertRgbToInt(152, 34, 32);
        palette[0x17] = frameBuffer.convertRgbToInt(120, 60, 0);
        palette[0x18] = frameBuffer.convertRgbToInt(84, 90, 0);
        palette[0x19] = frameBuffer.convertRgbToInt(40, 114, 0);
        palette[0x1A] = frameBuffer.convertRgbToInt(8, 124, 0);
        palette[0x1B] = frameBuffer.convertRgbToInt(0, 118, 40);
        palette[0x1C] = frameBuffer.convertRgbToInt(0, 102, 120);
        palette[0x1D] = frameBuffer.convertRgbToInt(0, 0, 0);
        palette[0x1E] = frameBuffer.convertRgbToInt(0, 0, 0);
        palette[0x1F] = frameBuffer.convertRgbToInt(0, 0, 0);

        palette[0x20] = frameBuffer.convertRgbToInt(236, 238, 236);
        palette[0x21] = frameBuffer.convertRgbToInt(76, 154, 236);
        palette[0x22] = frameBuffer.convertRgbToInt(120, 124, 236);
        palette[0x23] = frameBuffer.convertRgbToInt(176, 98, 236);
        palette[0x24] = frameBuffer.convertRgbToInt(228, 84, 236);
        palette[0x25] = frameBuffer.convertRgbToInt(236, 88, 180);
        palette[0x26] = frameBuffer.convertRgbToInt(236, 106, 100);
        palette[0x27] = frameBuffer.convertRgbToInt(212, 136, 32);
        palette[0x28] = frameBuffer.convertRgbToInt(160, 170, 0);
        palette[0x29] = frameBuffer.convertRgbToInt(116, 196, 0);
        palette[0x2A] = frameBuffer.convertRgbToInt(76, 208, 32);
        palette[0x2B] = frameBuffer.convertRgbToInt(56, 204, 108);
        palette[0x2C] = frameBuffer.convertRgbToInt(56, 180, 204);
        palette[0x2D] = frameBuffer.convertRgbToInt(60, 60, 60);
        palette[0x2E] = frameBuffer.convertRgbToInt(0, 0, 0);
        palette[0x2F] = frameBuffer.convertRgbToInt(0, 0, 0);

        palette[0x30] = frameBuffer.convertRgbToInt(236, 238, 236);
        palette[0x31] = frameBuffer.convertRgbToInt(168, 204, 236);
        palette[0x32] = frameBuffer.convertRgbToInt(188, 188, 236);
        palette[0x33] = frameBuffer.convertRgbToInt(212, 178, 236);
        palette[0x34] = frameBuffer.convertRgbToInt(236, 174, 236);
        palette[0x35] = frameBuffer.convertRgbToInt(236, 174, 212);
        palette[0x36] = frameBuffer.convertRgbToInt(236, 180, 176);
        palette[0x37] = frameBuffer.convertRgbToInt(228, 196, 144);
        palette[0x38] = frameBuffer.convertRgbToInt(204, 210, 120);
        palette[0x39] = frameBuffer.convertRgbToInt(180, 222, 120);
        palette[0x3A] = frameBuffer.convertRgbToInt(168, 226, 144);
        palette[0x3B] = frameBuffer.convertRgbToInt(152, 226, 180);
        palette[0x3C] = frameBuffer.convertRgbToInt(160, 214, 228);
        palette[0x3D] = frameBuffer.convertRgbToInt(160, 162, 160);
        palette[0x3E] = frameBuffer.convertRgbToInt(0, 0, 0);
        palette[0x3F] = frameBuffer.convertRgbToInt(0, 0, 0);
    }

    public Ppu2C02Impl(FrameBuffer frameBuffer) {
        this(new PPUCtrl(), new PPUMask(), new PPUStatus(), new LoopyRegister(), frameBuffer);
    }

    private void resetRegisters() {
        control.setRegister(0);
        mask.setRegister(0);
        status.setRegister(0);
        vramAddress.setRegister(0);
        tempVramAddress.setRegister(0);
    }

    private void resetShifters() {
        backgroundNextTileId           = 0;
        backgroundNextTileAttribute    = 0;
        backgroundNextTileLsb          = 0;
        backgroundNextTileMsb          = 0;
        backgroundShifterPatternLow    = 0;
        backgroundShifterPatternHigh   = 0;
        backgroundShifterAttributeLow  = 0;
        backgroundShifterAttributeHigh = 0;

        spriteEvaluator.resetShifters();
    }

    // region Background rendering
    private int backgroundNextTileId           = 0x00;
    private int backgroundNextTileAttribute    = 0x00;
    private int backgroundNextTileLsb          = 0x00;
    private int backgroundNextTileMsb          = 0x00;
    private int backgroundShifterPatternLow    = 0x0000;
    private int backgroundShifterPatternHigh   = 0x0000;
    private int backgroundShifterAttributeLow  = 0x0000;
    private int backgroundShifterAttributeHigh = 0x0000;

    public void loadBackgroundShifters() {
        backgroundShifterPatternLow    = (backgroundShifterPatternLow & 0xFF00) | (backgroundNextTileLsb & 0x00FF);
        backgroundShifterPatternHigh   = (backgroundShifterPatternHigh & 0xFF00) | (backgroundNextTileMsb & 0x00FF);
        backgroundShifterAttributeLow  = (backgroundShifterAttributeLow & 0xFF00) | ((backgroundNextTileAttribute & 0b01) != 0 ? 0xFF : 0x00);
        backgroundShifterAttributeHigh = (backgroundShifterAttributeHigh & 0xFF00) | ((backgroundNextTileAttribute & 0b10) != 0 ? 0xFF : 0x00);
    }
    // endregion

    public void updateShifters() {
        if (mask.isRenderBackground()) {
            backgroundShifterPatternLow    = (backgroundShifterPatternLow << 1) & MASK_16BIT;
            backgroundShifterPatternHigh   = (backgroundShifterPatternHigh << 1) & MASK_16BIT;
            backgroundShifterAttributeLow  = (backgroundShifterAttributeLow << 1) & MASK_16BIT;
            backgroundShifterAttributeHigh = (backgroundShifterAttributeHigh << 1) & MASK_16BIT;
        }

        spriteEvaluator.updateShifters();
    }

    @Override
    public PPUCtrl getControl() {
        return control;
    }

    @Override
    public PPUMask getMask() {
        return mask;
    }

    @Override
    public PPUStatus getStatus() {
        return status;
    }

    @Override
    public OAM getPrimaryOAM() {
        return primaryOAM;
    }

    @Override
    public OAM getSecondaryOAM() {
        return secondaryOAM;
    }

    @Override
    public int getScanLine() {
        return scanLine;
    }

    @Override
    public int getCycle() {
        return cycle;
    }

    @Override
    public boolean isNmi() {
        return nmi;
    }

    @Override
    public boolean isVerticalBlank() {
        return status.isVerticalBlank();
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
    }

    private int getFinalPixelColor() {
        var backgroundPixel   = 0x00;
        var backgroundPalette = 0x00;

        if (mask.isRenderBackground()) {
            if (mask.isRenderBackgroundLeft() || cycle >= 9) {
                var bitMux = PRG_ROM_START >> fineX;

                var p0Pixel = (backgroundShifterPatternLow & bitMux) > 0 ? 1 : 0;
                var p1Pixel = (backgroundShifterPatternHigh & bitMux) > 0 ? 1 : 0;

                backgroundPixel = (p1Pixel << 1) | p0Pixel;

                var backgroundPalette0 = (backgroundShifterAttributeLow & bitMux) > 0 ? 1 : 0;
                var backgroundPalette1 = (backgroundShifterAttributeHigh & bitMux) > 0 ? 1 : 0;

                backgroundPalette = (backgroundPalette1 << 1) | backgroundPalette0;
            }
        }

        var foregroundPixelAndPalette = spriteEvaluator.getFinalPixelAndPalette();
        var foregroundPixel           = foregroundPixelAndPalette.pixel();
        var foregroundPalette         = foregroundPixelAndPalette.palette();
        var foregroundPriority        = foregroundPixelAndPalette.priority();

        var pixel   = 0x00;
        var palette = 0x00;

        if (backgroundPixel == 0 && foregroundPixel == 0) {
            // Both pixels are transparent, no one wins.
        } else if (backgroundPixel == 0 && foregroundPixel > 0) {
            // The background pixel is transparent, but the foreground pixel is visible.
            // The foreground pixel wins!
            pixel   = foregroundPixel;
            palette = foregroundPalette;
        } else if (backgroundPixel > 0 && foregroundPixel == 0) {
            // The background pixel is visible, but the foreground pixel is transparent.
            // The background pixel wins!
            pixel   = backgroundPixel;
            palette = backgroundPalette;
        } else if (backgroundPixel > 0 && foregroundPixel > 0) {
            if (foregroundPriority > 0) {
                // The foreground pixel is more important.
                pixel   = foregroundPixel;
                palette = foregroundPalette;
            } else {
                // The background pixel is more important.
                pixel   = backgroundPixel;
                palette = backgroundPalette;
            }

            spriteEvaluator.detectSpriteZeroCollision();
        }/* else {
            pixel   = backgroundPixel;
            palette = backgroundPalette;
        }*/

        //noinspection UnnecessaryLocalVariable
        var rgb = this.palette[readVideoMemory(PALETTE_RAM_START + (palette << 2) + pixel) & 0x3F];
        return rgb;
    }

    @Override
    public void clock() {
        spriteEvaluator.onPpuCycle();

        if (scanLine >= -1 && scanLine < 240) {
            if (scanLine == 0 && cycle == 0 && oddFrame && (mask.isRenderBackground() || mask.isRenderSprites())) {
                cycle = 1;
            }

            if (scanLine == -1 && cycle == 1) {
                status.setVerticalBlank(false);
                status.setSpriteZeroHit(false);
                status.setSpriteOverflow(false);
                isFrameComplete = false;

                spriteEvaluator.resetShifters();
            }

            if ((cycle >= 2 && cycle <= 257) || (cycle >= 321 && cycle < 338)) {
                // Shift registers to the left to feed the pixel to the screen.
                updateShifters();

                // Run the background fetcher pipeline to load the next tile's pattern and attribute data.
                switch ((cycle - 1) % 8) {
                    case 0:
                        loadBackgroundShifters();
                        backgroundNextTileId = readVideoMemory(PPU_REGISTER_START | (vramAddress.getRegister() & MASK_12BIT));
                        break;
                    case 2:
                        backgroundNextTileAttribute = readVideoMemory(ATTRIBUTE_TABLE_0_START
                                | (vramAddress.getNameTableY() & 0x0C00)
                                | vramAddress.getNameTableX()
                                | ((vramAddress.getCoarseY() >> 2) << 3)
                                | (vramAddress.getCoarseX() >> 2));

                        if ((vramAddress.getCoarseY() & 0x02) != 0) {
                            backgroundNextTileAttribute >>= 4;
                        }
                        if ((vramAddress.getCoarseX() & 0x02) != 0) {
                            backgroundNextTileAttribute >>= 2;
                        }
                        backgroundNextTileAttribute &= BOTTOM_2_BITS;
                        break;
                    case 4:
                        backgroundNextTileLsb = readVideoMemory((control.getBackgroundPatternTableAddress() << 12)
                                + ((backgroundNextTileId << 4) & MASK_16BIT)
                                + (vramAddress.getFineY()));
                        break;
                    case 6:
                        backgroundNextTileMsb = readVideoMemory((control.getBackgroundPatternTableAddress() << 12)
                                + ((backgroundNextTileId << 4) & MASK_16BIT)
                                + (vramAddress.getFineY() + 8));
                        break;
                    case 7:
                        if (mask.isRenderSprites() || mask.isRenderBackground()) {
                            vramAddress.incrementX();
                        }
                        break;
                }
            }

            if (cycle == 256) {
                if (mask.isRenderSprites() || mask.isRenderBackground()) {
                    vramAddress.IncrementY();
                }
            }

            if (cycle == 257) {
                loadBackgroundShifters();

                if (mask.isRenderSprites() || mask.isRenderBackground()) {
                    vramAddress.transferHorizontalBits(tempVramAddress);
                }
            }

            if (cycle == 338 || cycle == 340) {
                backgroundNextTileId = readVideoMemory(PPU_REGISTER_START | (vramAddress.getRegister() & MASK_12BIT));
            }

            if (scanLine == -1 && cycle >= 280 && cycle < 305) {
                if (mask.isRenderSprites() || mask.isRenderBackground()) {
                    vramAddress.transferVerticalBits(tempVramAddress);
                }
            }
        }

        // Vertical blanking lines.
        if (scanLine >= 241 && scanLine <= 260) {
            if (scanLine == 241 && cycle == 1) {
                status.setVerticalBlank(true);
                if (control.getEnableNmi()) {
                    nmi = true;
                }

                isFrameComplete = true;
            }
        }

        frameBuffer.setPixel(cycle - 1, scanLine, getFinalPixelColor());

        cycle++;

        if (cycle >= 341) {
            cycle = 0;
            scanLine++;
            if (scanLine >= 261) {
                scanLine = -1;
                oddFrame = !oddFrame;
            }
        }
    }

    @Override
    public void reset() {
        fineX           = 0;
        addressLatch    = 0;
        ppuDataBuffer   = 0;
        scanLine        = 0;
        cycle           = 0;
        isFrameComplete = false;
        oddFrame        = false;

        resetShifters();
        resetRegisters();
    }

    /**
     * Reading from the PPUSTATUS register will clear the VBLANK flag in {@link #control}.
     * <p>
     * Only the 3 bits furthest to the left in the PPUSTATUS register contain status information.
     * However, when reading the PPUSTATUS register, the bottom 5 bits is expected to contain data from the previous PPU bus operation.
     */
    private int readPpuStatus() {
        var data = (status.getRegister() & BYTE_TOP_3_BITS) | (ppuDataBuffer & BOTTOM_5_BITS);


        // Clear the VBLANK flag.
        status.setVerticalBlank(false);

        // TEMPORARY: We have ot mock this to avoid ending up in an infinite loop on the Super Mario Bros. start screen.
        status.setSpriteZeroHit(true);

        // Clear the write-latch.
        addressLatch = 0;

        return data;
    }

    @Override
    public void writePrimaryOAM(int address, int value) {
        primaryOAM.write(address, value);
    }

    /**
     * Reading from the PPUDATA register will add either 1 (horizontal progression) or 32 (vertical progression) to {@link #vramAddress} depending on the return value of the {@link PPUCtrl#getIncrementMode()}.
     * <p>
     * Reading from the PPUDATA register is delayed by one cycle, but rather than returning the data in the register, data is returned from an internal data buffer, {@link #ppuDataBuffer}.
     * The buffer is updated on every read from the PPUDATA register, but only after the previous contents have been returned to the CPU.
     */
    private int readPpuData() {
        var data = ppuDataBuffer;
        ppuDataBuffer = readVideoMemory(vramAddress.getRegister());

        if (vramAddress.getRegister() >= PALETTE_RAM_START) {
            data = ppuDataBuffer;
        }

        // The VRAM address is incremented after each read from the PPUDATA register.
        var increment = control.getIncrementMode() == 0 ? 1 : 32;
        this.vramAddress.setRegister(this.vramAddress.getRegister() + increment);

        return data;
    }

    /**
     * Only the registers {@link PpuMemoryMap#PPUSTATUS}, {@link PpuMemoryMap#OAMDATA} and {@link PpuMemoryMap#PPUDATA} are readable.
     */
    @Override
    public int readRegister(int address) {
        return switch (address) {
            case PPUSTATUS -> readPpuStatus();
            case OAMDATA -> primaryOAM.read(oamaddr);
            case PPUDATA -> readPpuData();
            default -> {
                log.warn("Unknown register: {}", "%04X".formatted(address));
                yield 0x00;
            }
        };
    }

    /**
     * TODO: Add documentation.
     */
    private void writePpuCtrl(int data) {
        control.setRegister(data);
        tempVramAddress.setNameTableX((control.getNameTableX() & 0x400) != 0);
        tempVramAddress.setNameTableY((control.getNameTableY() & 0x800) != 0);
    }

    /**
     * TODO: Add documentation.
     */
    private void writePpuScroll(int data) {
        if (addressLatch == 0) {
            fineX = data & 0x07;
            tempVramAddress.setCoarseX(data >> 3);
            addressLatch = 1;
        } else {
            tempVramAddress.setFineY(data & 0x07);
            tempVramAddress.setCoarseY(data >> 3);
            addressLatch = 0;
        }
    }

    /**
     * TODO: Add documentation.
     */
    private void writePpuAddr(int data) {
        if (addressLatch == 0) {
            tempVramAddress.setRegister(((data & 0x3F) << 8) | (tempVramAddress.getRegister() & 0x00FF));
            addressLatch = 1;
        } else {
            tempVramAddress.setRegister((tempVramAddress.getRegister() & 0xFF00) | data);
            vramAddress.setRegister(tempVramAddress.getRegister());
            addressLatch = 0;
        }
    }

    /**
     * Writes to the PPUDATA register will add either 1 (horizontal progression) or 32 (vertical progression) to {@link #vramAddress} depending on the return value of the {@link PPUCtrl#getIncrementMode()}.
     */
    private void writePpuData(int data) {
        writeVideoMemory(vramAddress.getRegister(), data);
        var increment = control.getIncrementMode() == 0 ? 1 : 32;
        vramAddress.setRegister(vramAddress.getRegister() + increment);
    }

    @Override
    public void writeRegister(int address, int data) {
        address &= MASK_16BIT;
        data &= MASK_8BIT;

        switch (address) {
            case PPUCTRL:
                writePpuCtrl(data);
                break;
            case PPUMASK:
                mask.setRegister(data);
                break;
            case OAMADDR:
                oamaddr = data;
                break;
            case OAMDATA:
                primaryOAM.write(oamaddr, data);
                break;
            case PPUSCROLL:
                writePpuScroll(data);
                break;
            case PPUADDR:
                writePpuAddr(data);
                break;
            case PPUDATA:
                writePpuData(data);
                break;
            default:
                log.warn("Unknown register: {}", "%04X".formatted(address));
                break;
        }
    }

    @Override
    public void clearNmi() {
        nmi = false;
    }

    @Override
    public boolean isFrameComplete() {
        return isFrameComplete;
    }

    @Override
    public void setFrameComplete(boolean frameComplete) {
        isFrameComplete = false;
    }

    @Override
    public FrameBuffer getFrameBuffer() {
        return frameBuffer;
    }

    public int readVideoMemory(int address) {
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
            data = paletteTable[address] & (mask.isGrayscale() ? 0x30 : 0x3F);
        }

        return data;
    }

    private void writeVideoMemory(int address, int data) {
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
            paletteTable[address] = data;
        }
    }
}
