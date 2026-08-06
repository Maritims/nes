package no.clueless.emulation.impl.ppu;

import no.clueless.emulation.Cartridge;
import no.clueless.emulation.Ppu2C02;
import no.clueless.emulation.impl.ppu.event.PixelListener;
import no.clueless.emulation.impl.ppu.register.PpuBus;
import no.clueless.emulation.impl.ppu.register.PpuRegisterHandler;
import no.clueless.emulation.impl.ppu.register.PpuRegisters;

import static no.clueless.emulation.impl.Masks.*;
import static no.clueless.emulation.impl.PpuMemoryMap.*;

public class Ppu2C02Impl implements Ppu2C02 {
    private final PpuRegisterHandler registerHandler;
    private final PpuRegisters       registers;
    private final PpuBus             bus;
    private final PixelCompositor    pixelCompositor;

    private boolean isFrameComplete;

    private final PixelListener   pixelListener;
    private final SpriteEvaluator spriteEvaluator;

    private int     scanLine = 0;
    private int     cycle    = 0;
    private boolean oddFrame = false;
    private boolean nmi;

    public Ppu2C02Impl(PixelListener pixelListener) {
        this.pixelListener   = pixelListener;
        this.registers       = new PpuRegisters();
        this.bus             = new PpuBus(() -> registers.mask().isGrayscale());
        this.registerHandler = new PpuRegisterHandler(registers, bus::read, bus::write);
        this.spriteEvaluator = new SpriteEvaluator(this, registers, bus, this::getCycle, this::getScanLine);
        this.pixelCompositor = new PixelCompositor(new NESPalette(), registers, bus, registerHandler, spriteEvaluator);
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
        if (registers.mask().isRenderBackground()) {
            backgroundShifterPatternLow    = (backgroundShifterPatternLow << 1) & MASK_16BIT;
            backgroundShifterPatternHigh   = (backgroundShifterPatternHigh << 1) & MASK_16BIT;
            backgroundShifterAttributeLow  = (backgroundShifterAttributeLow << 1) & MASK_16BIT;
            backgroundShifterAttributeHigh = (backgroundShifterAttributeHigh << 1) & MASK_16BIT;
        }

        spriteEvaluator.updateShifters();
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
    public boolean isVerticalBlank() {
        return registers.status().isVerticalBlank();
    }

    public void setScanLine(int scanLine) {
        this.scanLine = scanLine;
    }

    public void setCycle(int cycle) {
        this.cycle = cycle;
    }

    @Override
    public void connectToCartridge(Cartridge cartridge) {
        bus.connectToCartridge(cartridge);
    }

    @Override
    public void clock() {
        spriteEvaluator.onPpuCycle();

        if (scanLine >= -1 && scanLine < 240) {
            if (scanLine == 0 && cycle == 0 && oddFrame && (registers.mask().isRenderBackground() || registers.mask().isRenderSprites())) {
                cycle = 1;
            }

            if (scanLine == -1 && cycle == 1) {
                registers.status().setVerticalBlank(false);
                registers.status().setSpriteZeroHit(false);
                registers.status().setSpriteOverflow(false);
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
                        backgroundNextTileId = bus.read(0x2000 | (registers.vramAddress().getRegister() & MASK_12BIT));
                        break;
                    case 2:
                        backgroundNextTileAttribute = bus.read(ATTRIBUTE_TABLE_0_START
                                | (registers.vramAddress().getNameTableY() & 0x0C00)
                                | registers.vramAddress().getNameTableX()
                                | ((registers.vramAddress().getCoarseY() >> 2) << 3)
                                | (registers.vramAddress().getCoarseX() >> 2));

                        if ((registers.vramAddress().getCoarseY() & 0x02) != 0) {
                            backgroundNextTileAttribute >>= 4;
                        }
                        if ((registers.vramAddress().getCoarseX() & 0x02) != 0) {
                            backgroundNextTileAttribute >>= 2;
                        }
                        backgroundNextTileAttribute &= BOTTOM_2_BITS;
                        break;
                    case 4:
                        backgroundNextTileLsb = bus.read((registers.control().getBackgroundPatternTableAddress() << 12)
                                + ((backgroundNextTileId << 4) & MASK_16BIT)
                                + (registers.vramAddress().getFineY()));
                        break;
                    case 6:
                        backgroundNextTileMsb = bus.read((registers.control().getBackgroundPatternTableAddress() << 12)
                                + ((backgroundNextTileId << 4) & MASK_16BIT)
                                + (registers.vramAddress().getFineY() + 8));
                        break;
                    case 7:
                        if (registers.mask().isRenderSprites() || registers.mask().isRenderBackground()) {
                            registers.vramAddress().incrementX();
                        }
                        break;
                }
            }

            if (cycle == 256) {
                if (registers.mask().isRenderSprites() || registers.mask().isRenderBackground()) {
                    registers.vramAddress().IncrementY();
                }
            }

            if (cycle == 257) {
                loadBackgroundShifters();

                if (registers.mask().isRenderSprites() || registers.mask().isRenderBackground()) {
                    registers.vramAddress().transferHorizontalBits(registers.tempVramAddress());
                }
            }

            if (cycle == 338 || cycle == 340) {
                backgroundNextTileId = bus.read(0x2000 | (registers.vramAddress().getRegister() & MASK_12BIT));
            }

            if (scanLine == -1 && cycle >= 280 && cycle < 305) {
                if (registers.mask().isRenderSprites() || registers.mask().isRenderBackground()) {
                    registers.vramAddress().transferVerticalBits(registers.tempVramAddress());
                }
            }
        }

        // Vertical blanking lines.
        if (scanLine >= 241 && scanLine <= 260) {
            if (scanLine == 241 && cycle == 1) {
                registers.status().setVerticalBlank(true);
                if (registers.control().getEnableNmi()) {
                    nmi = true;
                }

                isFrameComplete = true;
            }
        }

        var finalPixelColor = pixelCompositor.compose(cycle, backgroundShifterPatternLow, backgroundShifterPatternHigh, backgroundShifterAttributeLow, backgroundShifterAttributeHigh);
        pixelListener.setPixel(cycle - 1, scanLine, finalPixelColor);

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
        scanLine        = 0;
        cycle           = 0;
        isFrameComplete = false;
        oddFrame        = false;

        resetShifters();
        registerHandler.reset();
    }

    @Override
    public void writePrimaryOAM(int address, int value) {
        registerHandler.writePrimaryOAM(address, value);
    }

    @Override
    public int readRegister(int address) {
        return registerHandler.read(address);
    }

    @Override
    public void writeRegister(int address, int data) {
        registerHandler.write(address, data);
    }

    @Override
    public int readBus(int address) {
        return bus.read(address);
    }
}
