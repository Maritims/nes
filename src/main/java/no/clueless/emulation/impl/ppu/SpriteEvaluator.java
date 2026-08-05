package no.clueless.emulation.impl.ppu;

import no.clueless.emulation.Ppu2C02;

import java.util.Arrays;

public final class SpriteEvaluator {
    private final Ppu2C02 ppu;

    private final int[]   spriteShifterPatternLow  = new int[8];
    private final int[]   spriteShifterPatternHigh = new int[8];
    private final int[]   spriteXLatches           = new int[8];
    private       int     spriteCount;
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

    public SpriteEvaluator(Ppu2C02 ppu) {
        this.ppu = ppu;
    }

    public boolean isSecondaryOAMFull() {
        return isSecondaryOAMFull;
    }

    public void resetShifters() {
        Arrays.fill(spriteShifterPatternLow, 0x00);
        Arrays.fill(spriteShifterPatternHigh, 0x00);
    }

    public void updateShifters() {
        if (ppu.getMask().isRenderSprites() && ppu.getCycle() >= 1 && ppu.getCycle() <= 257) {
            for (var i = 0; i < spriteCount; i++) {
                if (spriteXLatches[i] > 0) {
                    spriteXLatches[i]--;
                } else {
                    spriteShifterPatternLow[i] <<= 1;
                    spriteShifterPatternHigh[i] <<= 1;
                }
            }
        }
    }

    public PixelInformation getFinalPixelAndPalette() {
        var foregroundPixel    = 0x00;
        var foregroundPalette  = 0x00;
        var foregroundPriority = 0x00;

        if (ppu.getMask().isRenderSprites()) {
            if (ppu.getMask().isRenderSpritesLeft() || (ppu.getCycle() >= 9)) {
                isSpriteZeroBeingRendered = false;

                for (var i = 0; i < spriteCount; i++) {
                    var sprite = ppu.getSecondaryOAM().getSprite(i);
                    if (sprite.x() == 0) {
                        var foregroundPixelLow  = (spriteShifterPatternLow[i] & 0x80) > 0 ? 1 : 0;
                        var foregroundPixelHigh = (spriteShifterPatternHigh[i] & 0x80) > 0 ? 1 : 0;
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

    public void detectSpriteZeroCollision() {
        if (isSpriteZeroHitPossible && isSpriteZeroBeingRendered) {
            if (ppu.getMask().isRenderBackground() && ppu.getMask().isRenderSprites()) {
                if (!ppu.getMask().isRenderBackgroundLeft() && !ppu.getMask().isRenderSpritesLeft()) {
                    if (ppu.getCycle() >= 9 && ppu.getCycle() < 258) {
                        ppu.getStatus().setSpriteZeroHit(true);
                    }
                } else {
                    if (ppu.getCycle() >= 1 && ppu.getCycle() < 258) {
                        ppu.getStatus().setSpriteZeroHit(true);
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

    /**
     * <p>During all visible scan lines, the PPU scans through OAM to determine which sprites to render on the next scan line.</p>
     * <p>Sprites found to be within range are copied into the secondary OAM, which is then used to initialize eight internal sprite output units.</p>
     */
    public void onPpuCycle() {
        var scanLine     = ppu.getScanLine();
        var cycle        = ppu.getCycle();
        var spriteHeight = ppu.getControl().getSpriteSize() == 0 ? 8 : 16;

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
                spriteCount           = 0;
            }

            // The secondary OAM can only be written to on even cycles.
            if (cycle % 2 == 0) {
                var byteIndex = (cycle / 2) - 1;
                ppu.getSecondaryOAM().setByte(byteIndex, 0xFF);
            }
        }

        // Cycles 65-256: Sprite evaluation
        else if (cycle >= 65 && cycle <= 256) {
            if (cycle % 2 == 1) {
                // On odd cycles, data is read from (primary) OAM.
                spriteLatch = ppu.getPrimaryOAM().get(n, m);
            } else {
                // On even cycles, data is written to secondary OAM (unless secondary OAM is full, in which case it will read the value in secondary OAM instead).
                if (isSecondaryOAMFull()) {
                    // Step 3
                    if (isSpriteInRange(spriteLatch, scanLine, spriteHeight)) {
                        // Step 3a: If the value is in range, set the sprite overflow flag in $2002.
                        ppu.getStatus().setSpriteOverflow(true);
                    }

                    // Step 3a/3b: The hardware bug increment. Increment n and m simultaneously (m masked to 3).
                    n = (n + 1) & 0x3F;
                    m = (m + 1) & 0x3;
                } else {
                    // 1. Starting at n = 0, read a sprite's Y-coordinate (OAM[n][0], copying it to the next open slot in secondary OAM (unless 8 sprites have been found, in which case the write is ignored).
                    ppu.getSecondaryOAM().set(spriteCount, secondaryOamByteIndex, spriteLatch);

                    if (m == 0) {
                        // 1a. If Y-coordinate is in range, copy remaining bytes of sprite data (OAM[n][1] thru OAM[n][3]) into secondary OAM.
                        if (isSpriteInRange(spriteLatch, scanLine, spriteHeight)) {
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
                            spriteCount++;

                            if (spriteCount == 8) {
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
            // 1-4: Read the Y-coordinate, tile number, attributes, and X-coordinate of the selected sprite from secondary OAM
            // 5-8: Read the X-coordinate of the selected sprite from secondary OAM 4 times (while the PPU fetches the sprite tile data)
            // For the first empty sprite slot, this will consist of sprite #63's Y-coordinate followed by 3 $FF bytes; for subsequent empty sprite slots, this will be four $FF bytes

            var offset      = cycle - 257; // Cycles through 257-320 takes exactly 64 cycles.
            var spriteIndex = offset / 8; // Secondary OAM holds up to 8 sprites.
            var subCycle    = offset % 8;

            if (spriteIndex < spriteCount) {
                if (subCycle < 4) {
                    // TODO: Load into shifters.
                } else {
                    // TODO: Table fetches.
                }
            } else {
                if (spriteIndex == spriteCount) {
                    var dummyByte = (subCycle == 0) ? ppu.getPrimaryOAM().get(63, 0) : 0xFF;
                } else {
                    var dummyByte = 0xFF;
                }
            }
        } else if (cycle == 340) {
            for (var i = 0; i < spriteCount; i++) {
                var sprite                   = ppu.getSecondaryOAM().getSprite(i);
                var spritePatternBitsLow     = 0;
                var spritePatternBitsHigh    = 0;
                var spritePatternAddressLow  = 0;
                var spritePatternAddressHigh = 0;

                if (ppu.getControl().getSpriteSize() == 0) {
                    if ((sprite.attributes() & 0x80) == 0) {
                        // Sprite is not flipped vertically.
                        spritePatternAddressLow = (ppu.getControl().getSpritePatternTableAddress() << 12) // Which pattern table?
                                | (sprite.tileIndex() << 4) // Which cell?
                                | (scanLine - sprite.y()); // Which row in cell?
                    } else {
                        // Sprite is flipped vertically.
                        spritePatternAddressLow = (ppu.getControl().getSpritePatternTableAddress() << 12) // Which pattern table?
                                | (sprite.tileIndex() << 4) // Which cell?
                                | (7 - (scanLine - sprite.y())); // Which row in cell?
                    }
                } else {
                    // TODO: 8x16 mode.
                }

                spritePatternAddressHigh = spritePatternAddressLow + 8;
                spritePatternBitsLow     = ppu.readVideoMemory(spritePatternAddressLow);
                spritePatternBitsHigh    = ppu.readVideoMemory(spritePatternAddressHigh);

                if ((sprite.attributes() & 0x40) == 0) {
                    // TODO: Flip horizontally.
                }

                spriteShifterPatternLow[i]  = spritePatternBitsLow;
                spriteShifterPatternHigh[i] = spritePatternBitsHigh;
            }
        }
    }
}
