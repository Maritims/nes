package no.clueless.emulation.impl.ppu;

public class PPUMASK {
    private int register;

    public void write(int register) {
        this.register = register & 0xFF;
    }

    public boolean isEmphasizeBlue() {
        return (register & (1 << 7)) != 0;
    }

    public boolean isEmphasizeGreen() {
        return (register & (1 << 6)) != 0;
    }

    public boolean isEmphasizeRed() {
        return (register & (1 << 5)) != 0;
    }

    public boolean isSpriteRenderingEnabled() {
        return (register & (1 << 4)) != 0;
    }

    public boolean isBackgroundRenderingEnabled() {
        return (register & (1 << 3)) != 0;
    }

    public boolean isRenderSpritesLeft() {
        return (register & (1 << 2)) != 0;
    }

    public boolean isRenderBackgroundLeft() {
        return (register & (1 << 1)) != 0;
    }

    public boolean isGrayscale() {
        return (register & (1 << 0)) != 0;
    }

    public void setSpriteRenderingEnabled(boolean value) {
        if (value) {
            register |= (1 << 4);
        } else {
            register &= ~(1 << 4);
        }
    }

    public void setBackgroundRenderingEnabled(boolean value) {
        if (value) {
            register |= (1 << 3);
        } else {
            register &= ~(1 << 3);
        }
    }
}
