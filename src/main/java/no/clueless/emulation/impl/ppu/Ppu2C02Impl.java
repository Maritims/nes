package no.clueless.emulation.impl.ppu;

import no.clueless.emulation.Cartridge;
import no.clueless.emulation.FrameBuffer;
import no.clueless.emulation.Ppu2C02;

import java.awt.*;

public class Ppu2C02Impl implements Ppu2C02 {
    // region Registers
    private final PPUCtrl   control;
    private final PPUMask   mask;
    private final PPUStatus status;
    private       int       oamaddr;
    private       OAM       oamdata;
    private       int       ppuscroll;
    private       int       ppuaddr;
    private       int       ppudata;
    private       int       oamdma;

    private final LoopyRegister vramAddress;
    private final LoopyRegister tempVramAddress = new LoopyRegister();

    private int fineX;
    private int addressLatch;
    // endregion

    private final PatternTable[] patternTables = new PatternTable[]{new PatternTable(), new PatternTable()};
    private final int[][]        nameTables    = new int[2][1024];
    private final PaletteRAM     paletteRAM    = new PaletteRAM();
    private final FrameBuffer    frameBuffer;
    private final int[]          palette       = new int[64];

    private int     scanLine = 0;
    private int     cycle    = 0;
    private boolean oddFrame = false;

    /**
     * The PPU data buffer.
     */
    private int dataBuffer;

    private Cartridge cartridge;
    private boolean   nmi;

    public Ppu2C02Impl(PPUCtrl control, PPUMask mask, PPUStatus status, LoopyRegister vramAddress, FrameBuffer frameBuffer) {
        this.control     = control;
        this.mask        = mask;
        this.status      = status;
        this.vramAddress = vramAddress;
        this.frameBuffer = frameBuffer;

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
        backgroundShifterPatternLow    = (backgroundShifterPatternLow & 0xFF00) | backgroundNextTileLsb;
        backgroundShifterPatternHigh   = (backgroundShifterPatternHigh & 0xFF00) | backgroundNextTileMsb;
        backgroundShifterAttributeLow  = (backgroundShifterAttributeLow & 0xFF00) | ((backgroundNextTileAttribute & 0b01) != 0 ? 0xFF : 0x00);
        backgroundShifterAttributeHigh = (backgroundShifterAttributeHigh & 0xFF00) | ((backgroundNextTileAttribute & 0b10) != 0 ? 0xFF : 0x00);
    }

    public void updateShifters() {
        if (mask.isRenderBackground()) {
            backgroundShifterPatternLow <<= 1;
            backgroundShifterPatternHigh <<= 1;
            backgroundShifterAttributeLow <<= 1;
            backgroundShifterAttributeHigh <<= 1;
        }
    }
    // endregion

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
    }

    int getRgbFromPalette(int palette, int pixel) {
        return this.palette[readVideoMemory(0x3F00 + (palette << 2) + pixel) & 0x3F];
    }

    @Override
    public void clock() {
        if (scanLine >= -1 && scanLine < 240) {
            if (scanLine == 0 && cycle == 0 && oddFrame && (mask.isRenderBackground() || mask.isRenderSprites())) {
                cycle = 1;
            }

            if (scanLine == -1 && cycle == 1) {
                status.setVerticalBlank(false);
                status.setSpriteZeroHit(false);
                status.setSpriteOverflow(false);
            }

            if ((cycle >= 2 && cycle <= 257) || (cycle >= 321 && cycle < 338)) {
                // Shift registers to the left to feed the pixel to the screen.
                updateShifters();

                // Run the background fetcher pipeline to load the next tile's pattern and attribute data.
                switch ((cycle - 1) % 8) {
                    case 0:
                        loadBackgroundShifters();
                        backgroundNextTileId = readVideoMemory(0x2000 | (vramAddress.getRegister() & 0x0FFF));
                        break;
                    case 2:
                        backgroundNextTileAttribute = readVideoMemory(0x23C0
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
                        backgroundNextTileAttribute &= 0x03;
                        break;
                    case 4:
                        backgroundNextTileLsb = readVideoMemory((control.getBackgroundPatternTableAddress() << 12)
                                + ((backgroundNextTileId << 4) & 0xFFFF)
                                + (vramAddress.getFineY()));
                        break;
                    case 6:
                        backgroundNextTileMsb = readVideoMemory((control.getBackgroundPatternTableAddress() << 12)
                                + ((backgroundNextTileId << 4) & 0xFFFF)
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
                backgroundNextTileId = readVideoMemory(0x2000 | (vramAddress.getRegister() & 0x0FFF));
            }

            if (scanLine == -1 && cycle >= 280 && cycle < 305) {
                if (mask.isRenderSprites() || mask.isRenderBackground()) {
                    vramAddress.transferVerticalBits(tempVramAddress);
                }
            }

            if (cycle == 257 && scanLine >= 0) {

            }

            if (cycle == 340) {

            }
        }

        if (scanLine == 240) {
            // The post render scanline. Nothing happens here.
        }

        if (scanLine >= 241 && scanLine <= 260 && cycle == 1) {
            status.setVerticalBlank(true);
            if (control.getEnableNmi()) {
                nmi = true;
            }
        }

        var backgroundPixel   = 0x00;
        var backgroundPalette = 0x00;

        if (mask.isRenderBackground()) {
            if (mask.isRenderBackgroundLeft() || cycle >= 9) {
                var bitMux = 0x8000 >> fineX;

                var p0Pixel = (backgroundShifterPatternLow & bitMux) > 0 ? 1 : 0;
                var p1Pixel = (backgroundShifterPatternHigh & bitMux) > 0 ? 1 : 0;

                backgroundPixel = (p1Pixel << 1) | p0Pixel;

                var backgroundPalette0 = (backgroundShifterAttributeLow & bitMux) > 0 ? 1 : 0;
                var backgroundPalette1 = (backgroundShifterAttributeHigh & bitMux) > 0 ? 1 : 0;

                backgroundPalette = (backgroundPalette0 << 1) | backgroundPalette1;
            }
        }

        var pixel   = 0x00;
        var palette = 0x00;

        if (backgroundPixel == 0) {
            pixel   = 0x00;
            palette = 0x00;
        } else {
            pixel   = backgroundPixel;
            palette = backgroundPalette;
        }

        var x   = cycle - 1;
        var y   = scanLine;
        var rgb = getRgbFromPalette(palette, pixel);
        frameBuffer.setPixel(x, y, rgb);
        frameBuffer.render();

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
        fineX                          = 0;
        addressLatch                   = 0;
        dataBuffer                     = 0;
        scanLine                       = 0;
        cycle                          = 0;
        backgroundNextTileId           = 0;
        backgroundNextTileAttribute    = 0;
        backgroundNextTileLsb          = 0;
        backgroundNextTileMsb          = 0;
        backgroundShifterPatternLow    = 0;
        backgroundShifterPatternHigh   = 0;
        backgroundShifterAttributeLow  = 0;
        backgroundShifterAttributeHigh = 0;
        oddFrame                       = false;
        status.setRegister(0);
        mask.setRegister(0);
        control.setRegister(0);
        vramAddress.setRegister(0);
        tempVramAddress.setRegister(0);
    }

    @Override
    public int readRegister(int address) {
        return switch (address) {
            case 0x0002 -> {
                // Only the 3 bits furthest to the left in the PPUSTATUS register contain status information.
                // However, when reading the PPUSTATUS register, the bottom 5 bits is expected to contain data from the previous PPU bus operation.
                var data = (status.getRegister() & 0xE0) | (dataBuffer & 0x1F);

                // Clear the VBLANK flag.
                status.setVerticalBlank(false);

                // Clear the write-latch.
                addressLatch = 0;

                yield data;
            }
            case 0x0004 -> oamdata.read(oamaddr);
            case 0x0007 -> {
                // Reading from the PPUDATA register is delayed by one cycle.
                // Rather than returning the data in the register, data is returned from an internal data buffer.
                // The buffer is updated on every read from the PPUDATA register, but only after the previous contents have been returned to the CPU.
                var data = dataBuffer;
                dataBuffer = readVideoMemory(vramAddress.getRegister());

                // The $3F00-$3FFF range of VRAM contains palette data on later PPUs, specifically the 2C02G, 2C02H and PAL PPUs.
                if (vramAddress.getRegister() >= 0x3F00) {
                    data = dataBuffer;
                }

                // The VRAM address is incremented after each read from the PPUDATA register.
                var increment = control.getIncrementMode() == 0 ? 1 : 32;
                this.vramAddress.setRegister(this.vramAddress.getRegister() + increment);

                yield data;
            }
            default -> 0x00;
        };
    }

    @Override
    public void writeRegister(int address, int data) {
        address &= 0xFFFF;
        data &= 0xFF;

        switch (address) {
            case 0x2000:
                control.setRegister(data);
                tempVramAddress.setNameTableX((control.getNameTableX() & 0x400) != 0);
                tempVramAddress.setNameTableY((control.getNameTableY() & 0x800) != 0);
                break;
            case 0x2001:
                mask.setRegister(data);
                break;
            case 0x2003:
                oamaddr = data;
                break;
            case 0x2004:
                oamdata.write(oamaddr, data);
                break;
            case 0x2005:
                if (addressLatch == 0) {
                    fineX = data & 0x07;
                    tempVramAddress.setCoarseX(data >> 3);
                    addressLatch = 1;
                } else {
                    tempVramAddress.setFineY(data & 0x07);
                    tempVramAddress.setCoarseY(data >> 3);
                    addressLatch = 0;
                }
                break;
            case 0x2006:
                if (addressLatch == 0) {
                    tempVramAddress.setRegister(((data & 0x3F) << 8) | (tempVramAddress.getRegister() & 0x00FF));
                    addressLatch = 1;
                } else {
                    tempVramAddress.setRegister((tempVramAddress.getRegister() & 0xFF00) | data);
                    vramAddress.setRegister(tempVramAddress.getRegister());
                    addressLatch = 0;
                }
                break;
            case 0x2007:
                writeVideoMemory(vramAddress.getRegister(), data);

                var increment = control.getIncrementMode() == 0 ? 1 : 32;
                vramAddress.setRegister(vramAddress.getRegister() + increment);
                break;
        }
    }

    @Override
    public void handleNmi() {
        nmi = false;
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

        if (address <= 0x1FFF) {
            var patternTableIndex = (address & 0x1000) >> 12;
            data = patternTables[patternTableIndex].read(address & 0x0FFF);
        } else if (address <= 0x3EFF) {
            if (cartridge != null) {
                address &= 0x0FFF;

                if (cartridge.isMirroredVertically()) {
                    if (address <= 0x03FF) {
                        data = nameTables[0][address & 0x03FF];
                    } else if (address <= 0x07FF) {
                        data = nameTables[1][address & 0x03FF];
                    } else if (address <= 0x0BFF) {
                        data = nameTables[0][address & 0x03FF];
                    } else {
                        data = nameTables[1][address & 0x03FF];
                    }
                } else {
                    if (address <= 0x03FF) {
                        data = nameTables[0][address & 0x03FF];
                    } else if (address <= 0x07FF) {
                        data = nameTables[0][address & 0x03FF];
                    } else if (address <= 0x0BFF) {
                        data = nameTables[1][address & 0x03FF];
                    } else {
                        data = nameTables[1][address & 0x03FF];
                    }
                }
            }
        } else {
            data = paletteRAM.read(address);
        }

        return data;
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

            if (cartridge != null) {
                if (cartridge.isMirroredVertically()) {
                    if (address <= 0x03FF) {
                        nameTables[0][address & 0x03FF] = value;
                    } else if (address <= 0x07FF) {
                        nameTables[1][address & 0x03FF] = value;
                    } else if (address <= 0x0BFF) {
                        nameTables[0][address & 0x03FF] = value;
                    } else {
                        nameTables[1][address & 0x03FF] = value;
                    }
                } else {
                    if (address <= 0x03FF) {
                        nameTables[0][address & 0x03FF] = value;
                    } else if (address <= 0x07FF) {
                        nameTables[0][address & 0x03FF] = value;
                    } else if (address <= 0x0BFF) {
                        nameTables[1][address & 0x03FF] = value;
                    } else {
                        nameTables[1][address & 0x03FF] = value;
                    }
                }
            }
            return;
        }

        paletteRAM.write(address, value);
    }
}
