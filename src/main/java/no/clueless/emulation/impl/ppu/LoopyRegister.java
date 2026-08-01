package no.clueless.emulation.impl.ppu;

/**
 * Represents the Loopy Register.
 * <p>Source: <a href="https://www.nesdev.org/wiki/PPU_scrolling#PPU_internal_registers">https://www.nesdev.org/wiki/PPU_scrolling#PPU_internal_registers</a></p>
 */
public class LoopyRegister {
    private int register;

    public void write(int register) {
        //this.register = register & 0x7FFF;
        this.register = register;
    }

    public int read() {
        return register;
    }

    public int getCoarseX() {
        return register & 0x001F;
    }

    public int getCoarseY() {
        return (register >> 5) & 0x001F;
    }

    public int getNameTableIndex() {
        return (register >> 10) & 0x0003;
    }

    public boolean isNameTableX() {
        return ((register >> 10) & 0x01) != 0;
    }

    public boolean isNameTableY() {
        return ((register >> 11) & 0x01) != 0;
    }

    public int getFineY() {
        return (register >> 12) & 0x0007;
    }

    public void setCoarseX(int value) {
        register = (register & ~0x001F) | (value & 0x001F);
    }

    public void setCoarseY(int value) {
        register = (register & ~0x03E0) | ((value & 0x1F) << 5);
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

    public void setFineY(int value) {
        register = (register & ~0x7000) | ((value & 0x07) << 12);
    }

    /**
     * Increments coarse X (bits 0-4). On overflow coarse X is reset to 0 and bit 10 is flipped.
     */
    public void incrementCoarseX() {
        var coarseX = getCoarseX();

        if (coarseX == 31) {
            setCoarseX(0);
            flipNameTableX();
        } else {
            setCoarseX(coarseX + 1);
        }
    }

    /**
     * Increments fine Y (bits 12-14).
     * On overflow fine Y resets to zero and coarse Y (bits 5-9) increments.
     * If coarse Y reaches 29, it resets to 0 and flips bit 11. Reaching 29 means we have reached the bottom of the nametable.
     */
    public void incrementFineY() {
        var fineY = getFineY();

        if (fineY < 7) {
            setFineY(fineY + 1);
        } else {
            setFineY(0);

            var coarseY = getCoarseY();

            if (coarseY == 29) {
                setCoarseY(0);
                flipNameTableY();
            } else if (coarseY == 31) {
                setCoarseY(0);
            } else {
                setCoarseY(coarseY + 1);
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
        setNameTableY(from.isNameTableY());
        setCoarseY(from.getCoarseY());
    }
}
