package no.clueless.emulation.impl.ppu;

import static no.clueless.emulation.impl.Masks.MASK_12BIT;

/**
 * Represents the Loopy Register.
 * <p>Source: <a href="https://www.nesdev.org/wiki/PPU_scrolling#PPU_internal_registers">https://www.nesdev.org/wiki/PPU_scrolling#PPU_internal_registers</a></p>
 */
public class LoopyRegister {
    private int register;

    public int getRegister() {
        return register;
    }

    public void setRegister(int register) {
        this.register = register;
    }

    /**
     * Coarse X is stored in bits 1-5.
     */
    public int getCoarseX() {
        return register & 0x1F;
    }

    public void setCoarseX(int value) {
        register = (register & ~0x001F) | (value & 0x001F);
    }

    /**
     * Coarse Y is stored in bits 6-10.
     */
    public int getCoarseY() {
        return (register & 0x3E0) >> 5;
    }

    public void setCoarseY(int value) {
        register = (register & ~0x03E0) | ((value << 5) & 0x03E0);
    }

    public int getNameTableX() {
        return (register & 0x400) >> 10;
    }

    public void setNameTableX(boolean value) {
        if (value) {
            register |= 0x0400;
        } else {
            register &= ~0x0400;
        }
    }

    public void flipNameTableX() {
        register ^= 0x0400;
    }

    public int getNameTableY() {
        return (register & 0x800) >> 11;
    }

    public void setNameTableY(boolean value) {
        if (value) {
            register |= 0x0800;
        } else {
            register &= ~0x0800;
        }
    }

    public void flipNameTableY() {
        register ^= 0x0800;
    }

    public int getFineY() {
        return (register & 0x7000) >> 12;
    }

    public void setFineY(int value) {
        register = (register & ~0x7000) | ((value << 12) & 0x7000);
    }

    /**
     * The value of the nametable index is stored in bits 11-12.
     */
    public int getNameTableIndex() {
        return register & MASK_12BIT;
    }

    public int getUnused() {
        return register & 0x80;
    }

    public void setUnused(int unused) {
        this.register = unused & 0x80;
    }

    /**
     * Increments coarse X (bits 0-4). On overflow coarse X is reset to 0 and bit 10 is flipped.
     */
    public void incrementX() {
        var coarseX = getCoarseX();

        if (coarseX == 31) {
            setCoarseX(0);
            flipNameTableX();
        } else {
            setCoarseX(coarseX + 1);
        }
    }

    public void IncrementY() {
        if (getFineY() < 7) {
            setFineY(getFineY() + 1);
        } else {
            setFineY(0);

            if (getCoarseY() == 29) {
                setCoarseY(0);
                flipNameTableY();
            } else if (getCoarseY() == 31) {
                setCoarseY(0);
            } else {
                setCoarseY(getCoarseY() + 1);
            }
        }
    }

    public void transferHorizontalBits(LoopyRegister other) {
        var source  = other.register;
        var current = this.register;
        this.register = (current & ~0x041F) | (source & 0x041F);
    }

    public void transferVerticalBits(LoopyRegister from) {
        setFineY(from.getFineY());
        setNameTableY(from.getNameTableY() != 0);
        setCoarseY(from.getCoarseY());
    }
}
