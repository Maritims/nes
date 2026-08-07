package no.clueless.emulation.impl.ppu;

import no.clueless.emulation.impl.ppu.register.PpuBus;
import no.clueless.emulation.impl.ppu.register.PpuRegisters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public final class SpriteEvaluator {
    private static final Logger       log = LoggerFactory.getLogger(SpriteEvaluator.class);
    private final        PpuRegisters registers;
    private final        PpuBus       ppuBus;

    private final int[]   shifterPatternLow  = new int[8];
    private final int[]   shifterPatternHigh = new int[8];
    private final int[]   spriteXLatches     = new int[8];
    private       int     spriteCount;
    private       int     evaluatedSpriteCount;
    private       boolean isSpriteZeroHitPossibleOnNextLine;
    private       boolean isSpriteZeroHitPossible;
    private       boolean isSpriteZeroBeingRendered;
    private       boolean isSecondaryOAMFull;

    /**
     * An integer representing the sprite number (0-63).
     */
    private int n;
    /**
     * An integer representing the byte within the sprite (0-3).
     */
    private int m;
    /**
     * An integer pointing to the current byte being written in secondary OAM (0-3).
     */
    private int secondaryOamByteIndex;
    /**
     * An 8-bit buffer for OAM reads.
     */
    private int spriteLatch;

    public SpriteEvaluator(PpuRegisters registers, PpuBus ppuBus) {
        this.registers = registers;
        this.ppuBus    = ppuBus;
    }

    public int getShifterPatternLow(int spriteIndex) {
        return shifterPatternLow[spriteIndex];
    }

    public int getShifterPatternHigh(int spriteIndex) {
        return shifterPatternHigh[spriteIndex];
    }

    public boolean isSpriteZeroHitPossible() {
        return isSpriteZeroHitPossible;
    }

    public boolean isSpriteZeroBeingRendered() {
        return isSpriteZeroBeingRendered;
    }

    public int getSpriteCount() {
        return spriteCount;
    }

    public boolean isSecondaryOAMFull() {
        return isSecondaryOAMFull;
    }

    public void latchSpriteZeroHitPossible() {
        isSpriteZeroHitPossible = isSpriteZeroHitPossibleOnNextLine;
    }

    public void resetShifters() {
        Arrays.fill(shifterPatternLow, 0x00);
        Arrays.fill(shifterPatternHigh, 0x00);
    }

    public void updateShifters(int cycle) {
        if (registers.mask().isRenderSprites() && cycle >= 1 && cycle <= 257) {
            for (var i = 0; i < spriteCount; i++) {
                if (spriteXLatches[i] > 0) {
                    spriteXLatches[i]--;
                } else {
                    shifterPatternLow[i] <<= 1;
                    shifterPatternHigh[i] <<= 1;
                }
            }
        }
    }

    public PixelInformation getFinalPixelAndPalette(int cycle) {
        var foregroundPixel    = 0x00;
        var foregroundPalette  = 0x00;
        var foregroundPriority = 0x00;

        if (registers.mask().isRenderSprites()) {
            if (registers.mask().isRenderSpritesLeft() || (cycle >= 9)) {
                isSpriteZeroBeingRendered = false;

                for (var i = 0; i < spriteCount; i++) {
                    var sprite = registers.secondaryOAM().getSprite(i);
                    if (spriteXLatches[i] == 0) {
                        var foregroundPixelLow  = (shifterPatternLow[i] & 0x80) > 0 ? 1 : 0;
                        var foregroundPixelHigh = (shifterPatternHigh[i] & 0x80) > 0 ? 1 : 0;
                        foregroundPixel = (foregroundPixelHigh << 1) | foregroundPixelLow;

                        foregroundPalette  = (sprite.attributes() & 0x30) + 0x04;
                        foregroundPriority = (sprite.attributes() & 0x20) == 0 ? 1 : 0;

                        if (foregroundPixel != 0) {
                            if (i == 0) {
                                isSpriteZeroBeingRendered = true;
                                break;
                            }
                        }
                    }
                }
            }
        }

        return new PixelInformation(foregroundPixel, foregroundPalette, foregroundPriority);
    }

    public void detectSpriteZeroCollision(int cycle) {
        if (isSpriteZeroHitPossible && isSpriteZeroBeingRendered) {
            if (registers.mask().isRenderBackground() && registers.mask().isRenderSprites()) {
                if (!registers.mask().isRenderBackgroundLeft() && !registers.mask().isRenderSpritesLeft()) {
                    if (cycle >= 9 && cycle < 258) {
                        registers.status().setSpriteZeroHit(true);
                    }
                } else {
                    if (cycle >= 1 && cycle < 258) {
                        registers.status().setSpriteZeroHit(true);
                    }
                }
            }
        }
    }

    /**
     * Checks whether a sprite intersects with a scan line.
     *
     * @return True if the sprite intersects with the scan line, otherwise false.
     */
    boolean isSpriteInRange(int spriteY, int scanLine, int spriteHeight) {
        return scanLine >= spriteY && scanLine < spriteY + spriteHeight;
    }

    private void fetchSpritePatternData(int spriteIndex, int scanLine) {
        var sprite                = registers.secondaryOAM().getSprite(spriteIndex);
        var spritePatternBitsLow  = 0;
        var spritePatternBitsHigh = 0;
        var row                   = scanLine - sprite.y();
        var addressLow            = 0;

        if (registers.control().getSpriteSize() == 0) {
            // 8x8 sprites.

            var table = registers.control().getSpritePatternTableAddress();

            if (sprite.isFlippedVertically()) {
                row = 7 - row;
            }

            addressLow = table | (sprite.tileIndex() << 4) | (row & 7);
        } else {
            // 16x16 sprites.

            var topTile    = sprite.tileIndex() & 0xFE;
            var bottomTile = sprite.tileIndex() | 1;
            var table      = (sprite.tileIndex() & 1) << 12;

            if (sprite.isFlippedVertically()) {
                var tile = row < 8 ? topTile : bottomTile;
                addressLow = table | (tile << 4) | (row & 7);
            } else {
                row = 15 - row;
                var tile = row < 8 ? topTile : bottomTile;
                addressLow = table | (tile << 4) | (row & 7);
            }
        }

        spritePatternBitsLow  = ppuBus.read(addressLow);
        spritePatternBitsHigh = ppuBus.read(addressLow + 8);

        if (sprite.isFlippedHorizontally()) {
            spritePatternBitsLow  = Integer.reverse(spritePatternBitsLow) >>> 24;
            spritePatternBitsHigh = Integer.reverse(spritePatternBitsHigh) >>> 24;
        }

        shifterPatternLow[spriteIndex]  = spritePatternBitsLow;
        shifterPatternHigh[spriteIndex] = spritePatternBitsHigh;
    }

    /**
     * <p>During all visible scan lines, the PPU scans through OAM to determine which sprites to render on the next scan line.</p>
     * <p>Sprites found to be within range are copied into the secondary OAM, which is then used to initialize eight internal sprite output units.</p>
     */
    public void evaluate(int cycle, int scanLine) {
        var spriteHeight = registers.control().getSpriteSize() == 0 ? 8 : 16;

        if (scanLine < 0 || scanLine > 239) {
            // Invisible scan line.
            return;
        }

        // Cycles 1-64: Secondary OAM (32-byte buffer for current sprites on scanline) is initialized to $FF - attempting to read $2004 will return $FF.
        if (cycle >= 1 && cycle <= 64) {
            // Reset internal state for the new scan line.
            if (cycle == 1) {
                n                     = 0;
                m                     = 0;
                secondaryOamByteIndex = 0;
                isSecondaryOAMFull    = false;
                evaluatedSpriteCount  = 0;
            }

            // The secondary OAM can only be written to on even cycles.
            if (cycle % 2 == 0) {
                var byteIndex = (cycle / 2) - 1;
                registers.secondaryOAM().setByte(byteIndex, 0xFF);
            }
        }

        // Cycles 65-256: Sprite evaluation
        else if (cycle >= 65 && cycle <= 256) {
            if (cycle == 65) {
                isSpriteZeroHitPossibleOnNextLine = false;
            }

            if (cycle % 2 == 1) {
                // On odd cycles, data is read from (primary) OAM.
                spriteLatch = registers.primaryOAM().get(n, m);
            } else {
                // On even cycles, data is written to secondary OAM (unless secondary OAM is full, in which case it will read the value in secondary OAM instead).
                if (isSecondaryOAMFull()) {
                    // Step 3
                    if (isSpriteInRange(spriteLatch, scanLine, spriteHeight)) {
                        // Step 3a: If the value is in range, set the sprite overflow flag in $2002.
                        registers.status().setSpriteOverflow(true);
                    }

                    // Step 3a/3b: The hardware bug increment. Increment n and m simultaneously (m masked to 3).
                    n = (n + 1) & 0x3F;
                    m = (m + 1) & 0x3;
                } else {
                    // 1. Starting at n = 0, read a sprite's Y-coordinate (OAM[n][0], copying it to the next open slot in secondary OAM (unless 8 sprites have been found, in which case the write is ignored).
                    registers.secondaryOAM().set(evaluatedSpriteCount, secondaryOamByteIndex, spriteLatch);

                    if (m == 0) {
                        // 1a. If Y-coordinate is in range, copy remaining bytes of sprite data (OAM[n][1] thru OAM[n][3]) into secondary OAM.
                        if (isSpriteInRange(spriteLatch, scanLine, spriteHeight)) {
                            if (n == 0) {
                                //log.debug("Sprite zero hit on scanline {}", scanLine);
                                isSpriteZeroHitPossibleOnNextLine = true;
                            }

                            // Set m and secondaryOamByteIndex in preparation for the next write cycle.
                            m                     = 1;
                            secondaryOamByteIndex = 1;
                        } else {
                            // The sprite is not in range, increment n to check next sprite.
                            n = (n + 1) & 0x3F;
                            if (n == 0) {
                                // n has overflowed back to zero. All sprites have been checked.
                                isSecondaryOAMFull = true;
                            }
                        }
                    } else {
                        // Increment m and secondaryOamByteIndex in preparation for the next write cycle.
                        m++;
                        secondaryOamByteIndex++;

                        if (m == 4) {
                            // We're done with this sprite.
                            // Reset the internal variables and increment spriteCount.
                            m                     = 0;
                            secondaryOamByteIndex = 0;
                            n                     = (n + 1) & 0x3F;
                            evaluatedSpriteCount++;

                            if (evaluatedSpriteCount == 8) {
                                // We've found 8 sprites and that's all the NES supports. It's time to stop.
                                isSecondaryOAMFull = true;
                            }
                            if (n == 0) {
                                // n has overflowed back to zero. All sprites have been evaluated.
                                isSecondaryOAMFull = true;
                            }
                        }
                    }
                }
            }
        }

        // Cycles 257-320: Sprite fetches (8 sprites total, 8 cycles per sprite)
        else if (cycle >= 257 && cycle <= 320) {
            var offset      = cycle - 257; // Cycles through 257-320 take exactly 64 cycles.
            var spriteIndex = offset / 8; // Secondary OAM holds up to 8 sprites.
            var subCycle    = offset % 8;

            if (offset == 0) {
                spriteCount = evaluatedSpriteCount;
            }

            if (spriteIndex < spriteCount) {
                switch (subCycle) {
                    case 0 -> spriteLatch = registers.secondaryOAM().get(spriteIndex, 0); // Read the Y coordinate of the selected sprite from secondary OAM.
                    case 1 -> spriteLatch = registers.secondaryOAM().get(spriteIndex, 1); // Read the tile number of the selected sprite from secondary OAM.
                    case 2 -> spriteLatch = registers.secondaryOAM().get(spriteIndex, 2); // Read the attributes of the selected sprite from secondary OAM.
                    case 3 -> {
                        spriteLatch                 = registers.secondaryOAM().get(spriteIndex, 3);
                        spriteXLatches[spriteIndex] = spriteLatch;
                    } // Read the X coordinate of the selected sprite from secondary OAM.
                    case 4, 5, 6,
                         7 -> {
                        // Read the X coordinate of the selected sprite from secondary OAM 4 times
                        spriteLatch = registers.secondaryOAM().get(spriteIndex, 3);
                        // ..while the PPU fetches the sprite tile data
                        fetchSpritePatternData(spriteIndex, scanLine);
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + subCycle);
                }
            } else {
                if (spriteIndex == spriteCount) {
                    if (subCycle == 0) {
                        // For the first empty sprite slot, this will consist of sprite #63's Y-coordinate
                        spriteLatch = registers.primaryOAM().get(63, 0);
                    } else {
                        // ..followed by 3 $FF bytes
                        spriteLatch = 0xFF;
                    }
                } else {
                    // ..for subsequent empty sprite slots, this will be four $FF bytes
                    spriteLatch = 0xFF;
                }
            }
        }
    }
}
