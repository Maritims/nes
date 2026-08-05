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
    private       int     n;
    private       int     m;
    private       int     oamLatch;
    private       boolean isSecondaryOAMFull;

    public SpriteEvaluator(Ppu2C02 ppu) {
        this.ppu = ppu;
    }

    public void resetShifters() {
        Arrays.fill(spriteShifterPatternLow, 0x00);
        Arrays.fill(spriteShifterPatternHigh, 0x00);
        Arrays.fill(spriteXLatches, 0x00);
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
                    if (sprite.getX() == 0) {
                        var foregroundPixelLow  = (spriteShifterPatternLow[i] & 0x80) > 0 ? 1 : 0;
                        var foregroundPixelHigh = (spriteShifterPatternHigh[i] & 0x80) > 0 ? 1 : 0;
                        foregroundPixel = (foregroundPixelHigh << 1) | foregroundPixelLow;

                        foregroundPalette  = (sprite.getAttributes() & 0x30) + 0x04;
                        foregroundPriority = (sprite.getAttributes() & 0x20) == 0 ? 1 : 0;

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

    // During all visible scanlines, the PPU scans through OAM to determine which sprites to render on the next scanline.
    // Sprites found to be within range are copied into the secondary OAM, which is then used to initialize eight internal sprite output units.
    public void onPpuCycle() {
        if (ppu.getScanLine() >= 0 && ppu.getScanLine() <= 239) {
            if (ppu.getCycle() >= 1 && ppu.getCycle() <= 64) {
                if (ppu.getCycle() == 1) {
                    n                  = 0;
                    m                  = 0;
                    spriteCount        = 0;
                    isSecondaryOAMFull = false;
                }

                // Secondary OAM (32-byte buffer for current sprites on scanline) is initialized to $FF - attempting to read $2004 will return $FF.
                ppu.getSecondaryOAM().fill(0xFF);
            }

            if (ppu.getCycle() >= 65 && ppu.getCycle() <= 256) {
                if (ppu.getCycle() == 65) {
                    n                  = 0;
                    m                  = 0;
                    spriteCount        = 0;
                    isSecondaryOAMFull = false;
                }

                if (ppu.getCycle() % 2 == 1) {
                    // On odd cycles, data is read from (primary) OAM.
                    oamLatch = ppu.getPrimaryOAM().get(n, m);
                } else {
                    // On even cycles, data is written to secondary OAM (unless secondary OAM is full, in which case it will read the value in secondary OAM instead).
                    if (isSecondaryOAMFull) {
                        // Step 3
                        var spriteY      = oamLatch;
                        var spriteHeight = ppu.getControl().getSpriteSize() == 0 ? 8 : 16;

                        if (ppu.getScanLine() >= spriteY && ppu.getScanLine() < spriteY + spriteHeight) {
                            // Step 3a: If the value is in range, set the sprite overflow flag in $2002.
                            ppu.getStatus().setSpriteOverflow(true);
                        }

                        // Step 3a/3b: The hardware bug increment. Increment n and m simultaneously (m masked to 3).
                        n = (n + 1) & 0x3F;
                        m = (m + 1) & 0x3;
                    } else {
                        if (m == 0) {
                            // Step 1: Starting at n = 0, read a sprite's Y-coordinate (OAM[n][0])
                            var spriteY      = oamLatch;
                            var spriteHeight = ppu.getControl().getSpriteSize() == 0 ? 8 : 16;

                            // Step 1a: If Y-coordinate is in range, copy it to the next open slot in secondary OAM
                            if (ppu.getScanLine() >= spriteY && ppu.getScanLine() < spriteY + spriteHeight) {
                                ppu.getSecondaryOAM().set(spriteCount, 0, spriteY);
                                m = 1; // Prepare to copy remaining bytes (OAM[n][1] thru OAM[n][3])
                            } else {
                                // Step 2 & 2b: Out of range, increment n and keep m = 0
                                n = (n + 1) & 0x3F;
                                m = 0;
                            }
                        } else {
                            // Step 1a: Copy remaining bytes of sprite data (OAM[n][1] thru OAM[n][3]) into secondary OAM
                            ppu.getSecondaryOAM().set(spriteCount, m, oamLatch);

                            if (m < 3) {
                                m++;
                            } else {
                                // Finished copying all 4 bytes of this sprite
                                spriteCount++;
                                n = (n + 1) & 0x3F;
                                m = 0;

                                // Step 2c: If exactly 8 sprites have been found, disable writes because it is full
                                if (spriteCount == 8) {
                                    isSecondaryOAMFull = true;
                                }
                            }
                        }
                    }
                }

                if (ppu.getCycle() >= 257 && ppu.getCycle() <= 320) {
                    // 1-4: Read the Y-coordinate, tile number, attributes, and X-coordinate of the selected sprite from secondary OAM
                    // 5-8: Read the X-coordinate of the selected sprite from secondary OAM 4 times (while the PPU fetches the sprite tile data)
                    // For the first empty sprite slot, this will consist of sprite #63's Y-coordinate followed by 3 $FF bytes; for subsequent empty sprite slots, this will be four $FF bytes

                    var offset      = ppu.getCycle() - 257; // Cycles through 257-320 takes exactly 64 cycles.
                    var spriteIndex = offset / 8; // Secondary OAM holds up to 8 sprites.
                    var subCycle    = offset % 8;

                    if (spriteIndex < spriteCount) {
                        if (subCycle < 4) {
                            var spriteByte = ppu.getSecondaryOAM().get(spriteIndex, subCycle);
                            // TODO: Load into shifters.
                        } else {
                            var spriteX = ppu.getSecondaryOAM().get(spriteIndex, 3);
                            // TODO: Table fetches.
                        }
                    } else {
                        if (spriteIndex == spriteCount) {
                            var dummyByte = (subCycle == 0) ? ppu.getPrimaryOAM().get(63, 0) : 0xFF;
                        } else {
                            var dummyByte = 0xFF;
                        }
                    }
                }
            }

            if (ppu.getCycle() == 340) {
                for (var i = 0; i < spriteCount; i++) {
                    var sprite                   = ppu.getSecondaryOAM().getSprite(i);
                    var spritePatternBitsLow     = 0;
                    var spritePatternBitsHigh    = 0;
                    var spritePatternAddressLow  = 0;
                    var spritePatternAddressHigh = 0;

                    if (ppu.getControl().getSpriteSize() == 0) {
                        if ((sprite.getAttributes() & 0x80) == 0) {
                            // Sprite is not flipped vertically.
                            spritePatternAddressLow = (ppu.getControl().getSpritePatternTableAddress() << 12) // Which pattern table?
                                    | (sprite.getTileIndex() << 4) // Which cell?
                                    | (ppu.getScanLine() - sprite.getY()); // Which row in cell?
                        } else {
                            // Sprite is flipped vertically.
                            spritePatternAddressLow = (ppu.getControl().getSpritePatternTableAddress() << 12) // Which pattern table?
                                    | (sprite.getTileIndex() << 4) // Which cell?
                                    | (7 - (ppu.getScanLine() - sprite.getY())); // Which row in cell?
                        }
                    } else {
                        // TODO: 8x16 mode.
                    }

                    spritePatternAddressHigh = spritePatternAddressLow + 8;
                    spritePatternBitsLow     = ppu.readVideoMemory(spritePatternAddressLow);
                    spritePatternBitsHigh    = ppu.readVideoMemory(spritePatternAddressHigh);

                    if ((sprite.getAttributes() & 0x40) == 0) {
                        // TODO: Flip horizontally.
                    }

                    spriteShifterPatternLow[i]  = spritePatternBitsLow;
                    spriteShifterPatternHigh[i] = spritePatternBitsHigh;
                }
            }
        }
    }
}
