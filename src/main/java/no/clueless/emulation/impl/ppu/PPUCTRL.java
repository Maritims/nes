package no.clueless.emulation.impl.ppu;

public class PPUCTRL {
    private int register;

    /**
     * Updates the register.
     *
     * @param register An 8-bit value representing the new value of the register.
     */
    public void write(int register) {
        this.register = register & 0xFF;
    }

    public boolean isNmiEnabled() {
        return (register & (1 << 7)) != 0;
    }

    public boolean isMasterSelected() {
        return (register & (1 << 6)) != 0;
    }

    public boolean isSlaveSelected() {
        return (register & (1 << 6)) == 0;
    }

    /**
     * Gets the sprite size.
     * @return PIXEL_8X8 if the sprite size is 8x8 pixels, PIXEL_8X16 if the sprite size is 8x16 pixels.
     */
    public SpriteSize getSpriteSize() {
        return (register & (1 << 4)) != 0 ? SpriteSize.PIXEL_8X16 : SpriteSize.PIXEL_8X8;
    }

    /**
     * Gets the address of the pattern table.
     *
     * @return 0x0000 if the pattern table is in the first 16 KB of RAM, 0x1000 if the pattern table is in the second 16 KB of RAM.
     */
    public int getBackgroundPatternTableAddress() {
        return (register & 0x10) == 0 ? 0x0000 : 0x1000;
    }

    /**
     * Gets the address of the pattern table for small sprites (8x8 pixels). Ignored for big sprites (8x16 pixels).
     *
     * @return 0x0000 if the pattern table is in the first 16 KB of RAM, 0x1000 if the pattern table is in the second 16 KB of RAM.
     */
    public int getSmallSpritePatternTableAddress() {
        return (register & 0x08) == 0 ? 0x0000 : 0x1000;
    }

    /**
     * Gets the VRAM address increment mode.
     *
     * @return HORIZONTAL if Increment mode is set to horizontal, VERTICAL if Increment mode is set to vertical.
     */
    public VramAddressIncrementMode getVramAddressIncrementMode() {
        return (register & 0x04) == 0 ? VramAddressIncrementMode.HORIZONTAL : VramAddressIncrementMode.VERTICAL;
    }

    /**
     * Gets the VRAM address increment.
     *
     * @return 1 if Increment mode is set to horizontal, 32 if Increment mode is set to vertical.
     */
    public int getVramAddressIncrement() {
        return getVramAddressIncrementMode().getIncrement();
    }

    public int getBaseNametableAddress() {
        return switch (register & 0x03) {
            case 0 -> 0x2000;
            case 1 -> 0x2400;
            case 2 -> 0x2800;
            case 3 -> 0x2C00;
            default -> throw new IllegalStateException("Invalid base nametable address");
        };
    }

    public enum VramAddressIncrementMode {
        /**
         * Increment the VRAM address by 1, essentially going across a row.
         */
        HORIZONTAL(0, 1),
        /**
         * Increment the VRAM address by 32, essentially going down a row.
         */
        VERTICAL(1, 32);

        private final int bitValue;
        private final int increment;

        VramAddressIncrementMode(int bitValue, int increment) {
            this.bitValue  = bitValue;
            this.increment = increment;
        }

        public int getBitValue() {
            return bitValue;
        }

        public int getIncrement() {
            return increment;
        }
    }

    public enum SpriteSize {
        PIXEL_8X8,
        PIXEL_8X16
    }
}
