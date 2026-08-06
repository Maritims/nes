package no.clueless.emulation.impl.ppu;

import no.clueless.emulation.impl.ppu.register.PpuRegisters;

import java.util.function.Function;

import static no.clueless.emulation.impl.Masks.*;
import static no.clueless.emulation.impl.PpuMemoryMap.ATTRIBUTE_TABLE_0_START;

public class BackgroundPipeline {
    private final PpuRegisters               registers;
    private final Function<Integer, Integer> readBus;

    private int nextTileId           = 0x00;
    private int nextTileAttribute    = 0x00;
    private int nextTileLsb          = 0x00;
    private int nextTileMsb          = 0x00;
    private int shifterPatternLow    = 0x0000;
    private int shifterPatternHigh   = 0x0000;
    private int shifterAttributeLow  = 0x0000;
    private int shifterAttributeHigh = 0x0000;

    public BackgroundPipeline(PpuRegisters registers, Function<Integer, Integer> readBus) {
        this.registers = registers;
        this.readBus   = readBus;
    }

    public int getShifterPatternLow() {
        return shifterPatternLow;
    }

    public int getShifterPatternHigh() {
        return shifterPatternHigh;
    }

    public int getShifterAttributeLow() {
        return shifterAttributeLow;
    }

    public int getShifterAttributeHigh() {
        return shifterAttributeHigh;
    }

    public void resetShifters() {
        nextTileId           = 0x00;
        nextTileAttribute    = 0x00;
        nextTileLsb          = 0x00;
        nextTileMsb          = 0x00;
        shifterPatternLow    = 0x0000;
        shifterPatternHigh   = 0x0000;
        shifterAttributeLow  = 0x0000;
        shifterAttributeHigh = 0x0000;
    }

    public void loadShifters() {
        shifterPatternLow    = (shifterPatternLow & 0xFF00) | (nextTileLsb & 0x00FF);
        shifterPatternHigh   = (shifterPatternHigh & 0xFF00) | (nextTileMsb & 0x00FF);
        shifterAttributeLow  = (shifterAttributeLow & 0xFF00) | ((nextTileAttribute & 0b01) != 0 ? 0xFF : 0x00);
        shifterAttributeHigh = (shifterAttributeHigh & 0xFF00) | ((nextTileAttribute & 0b10) != 0 ? 0xFF : 0x00);
    }

    public void updateShifters() {
        if (registers.mask().isRenderBackground()) {
            shifterPatternLow    = (shifterPatternLow << 1) & MASK_16BIT;
            shifterPatternHigh   = (shifterPatternHigh << 1) & MASK_16BIT;
            shifterAttributeLow  = (shifterAttributeLow << 1) & MASK_16BIT;
            shifterAttributeHigh = (shifterAttributeHigh << 1) & MASK_16BIT;
        }
    }

    public void onTick(int cycle, int scanLine) {
        if ((cycle >= 2 && cycle <= 257) || (cycle >= 321 && cycle < 338)) {
            // Shift registers to the left to feed the pixel to the screen.
            updateShifters();

            // Run the background fetcher pipeline to load the next tile's pattern and attribute data.
            switch ((cycle - 1) % 8) {
                case 0:
                    loadShifters();
                    nextTileId = readBus.apply(0x2000 | (registers.vramAddress().getRegister() & MASK_12BIT));
                    break;
                case 2:
                    nextTileAttribute = readBus.apply(ATTRIBUTE_TABLE_0_START
                            | (registers.vramAddress().getNameTableY() << 11)
                            | (registers.vramAddress().getNameTableX() << 10)
                            | ((registers.vramAddress().getCoarseY() >> 2) << 3)
                            | (registers.vramAddress().getCoarseX() >> 2));

                    if ((registers.vramAddress().getCoarseY() & 0x02) != 0) {
                        nextTileAttribute >>= 4;
                    }
                    if ((registers.vramAddress().getCoarseX() & 0x02) != 0) {
                        nextTileAttribute >>= 2;
                    }
                    nextTileAttribute &= BOTTOM_2_BITS;
                    break;
                case 4:
                    nextTileLsb = readBus.apply((registers.control().getBackgroundPatternTableAddress() << 12)
                            + ((nextTileId << 4) & MASK_16BIT)
                            + (registers.vramAddress().getFineY()));
                    break;
                case 6:
                    nextTileMsb = readBus.apply((registers.control().getBackgroundPatternTableAddress() << 12)
                            + ((nextTileId << 4) & MASK_16BIT)
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
            loadShifters();

            if (registers.mask().isRenderSprites() || registers.mask().isRenderBackground()) {
                registers.vramAddress().transferHorizontalBits(registers.tempVramAddress());
            }
        }

        if (cycle == 338 || cycle == 340) {
            nextTileId = readBus.apply(0x2000 | (registers.vramAddress().getRegister() & MASK_12BIT));
        }

        if (scanLine == -1 && cycle >= 280 && cycle < 305) {
            if (registers.mask().isRenderSprites() || registers.mask().isRenderBackground()) {
                registers.vramAddress().transferVerticalBits(registers.tempVramAddress());
            }
        }
    }
}
