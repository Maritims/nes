package no.clueless.emulation.impl.ppu;

public class PPUSTATUS {
    /**
     * VSOx xxxx
     */
    private int register;

    public int read() {
        return register;
    }

    public void write(int register) {
        this.register = register & 0xFF;
    }

    public boolean isVblank() {
        return ((register >> 7) & 0x01) != 0;
    }

    public boolean isSprite0Hit() {
        return ((register >> 6) & 0x01) != 0;
    }

    public boolean isSpriteOverflow() {
        return ((register >> 5) & 0x01) != 0;
    }

    public void setVblank(boolean value) {
        if (value) {
            register |= 0x80;
        } else {
            register &= ~0x80;
        }
    }

    public void setSprite0Hit(boolean value) {
        if (value) {
            register |= 0x40;
        } else {
            register &= ~0x40;
        }
    }

    public void setSpriteOverflow(boolean value) {
        if (value) {
            register |= 0x20;
        } else  {
            register &= ~0x20;
        }
    }
}
