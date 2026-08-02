package no.clueless.emulation.impl.ppu;

import static no.clueless.emulation.impl.Masks.BOTTOM_5_BITS;
import static no.clueless.emulation.impl.Masks.MASK_8BIT;

public class PPUStatus {
    private int register;

    public int getRegister() {
        return register & 0xFF;
    }

    public void setRegister(int register) {
        this.register = register & MASK_8BIT;
    }

    public int getUnused() {
        return (register & 0x01);
    }

    public void setUnused(int unused) {
        this.register = (this.register & BOTTOM_5_BITS) | (unused & BOTTOM_5_BITS);
    }

    public boolean isSpriteOverflow() {
        return (register & 0x20) != 0;
    }

    public void setSpriteOverflow(boolean overflow) {
        if (overflow) {
            register |= 0x20;
        } else {
            register &= ~0x20;
        }
    }

    public boolean isSpriteZeroHit() {
        return (register & 0x40) != 0;
    }

    public void setSpriteZeroHit(boolean hit) {
        if (hit) {
            register |= 0x40;
        } else {
            register &= ~0x40;
        }
    }

    public boolean isVerticalBlank() {
        return (register & 0x80) != 0;
    }

    public void setVerticalBlank(boolean verticalBlank) {
        if (verticalBlank) {
            register |= 0x80;
        } else {
            register &= ~0x80;
        }
    }
}
