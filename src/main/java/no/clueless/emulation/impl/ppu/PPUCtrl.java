package no.clueless.emulation.impl.ppu;

import static no.clueless.emulation.impl.Masks.MASK_8BIT;

public class PPUCtrl {
    private int register;

    public int getRegister() {
        return register & MASK_8BIT;
    }

    public void setRegister(int register) {
        this.register = register & MASK_8BIT;
    }

    public int getNameTableX() {
        return register & 0x01;
    }

    public void setNameTableX(boolean value) {
        if (value) {
            register |= 0x1;
        } else {
            register &= ~0x1;
        }
    }

    public int getNameTableY() {
        return (register & 0x02) >> 1;
    }

    public void setNameTableY(boolean value) {
        if (value) {
            register |= 0x02;
        } else {
            register &= ~0x02;
        }
    }

    public int getIncrementMode() {
        return (register & 0x4) >> 2;
    }

    public void setIncrementMode(boolean incrementMode) {
        if (incrementMode) {
            register |= 0x4;
        } else {
            register &= ~0x4;
        }
    }

    public int getSpritePatternTableAddress() {
        return (register & 0x8) >> 3;
    }

    public void setSpritePatternTableAddress(boolean value) {
        if (value) {
            register |= 0x8;
        } else {
            register &= ~0x8;
        }
    }

    public int getBackgroundPatternTableAddress() {
        return (register & 0x10) >> 4;
    }

    public void setBackgroundPatternTableAddress(boolean value) {
        if (value) {
            register |= 0x10;
        } else {
            register &= ~0x10;
        }
    }

    public int getSpriteSize() {
        return (register & 0x20) >> 5;
    }

    /**
     * Convenience function for retrieving the sprite height based on the sprite size flag.
     *
     * @return 16 if the sprite size flag is not set, otherwise 8.
     */
    public int getSpriteHeight() {
        return getSpriteSize() == 0 ? 8 : 16;
    }

    public void setSpriteSize(boolean isSpriteSize) {
        if (isSpriteSize) {
            register |= 0x20;
        } else {
            register &= ~0x20;
        }
    }

    public int getSlaveMode() {
        return (register & 0x40) >> 6;
    }

    public void setSlaveMode(boolean isSlaveMode) {
        if (isSlaveMode) {
            register |= 0x40;
        } else {
            register &= ~0x40;
        }
    }

    public boolean getEnableNmi() {
        return (register & 0x80) != 0;
    }

    public void setEnableNmi(boolean enableNmi) {
        if (enableNmi) {
            register |= 0x80;
        } else {
            register &= ~0x80;
        }
    }
}
