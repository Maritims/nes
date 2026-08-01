package no.clueless.emulation.impl.ppu;

public class PPUMask {
    private int register;

    public int getRegister() {
        return register & 0xFF;
    }

    public void setRegister(int register) {
        this.register = register & 0xFF;
    }

    public boolean isGrayscale() {
        return (register & 0x1) != 0;
    }

    public void setGrayscale(boolean grayscale) {
        if (grayscale) {
            register |= 0x1;
        } else {
            register &= ~0x1;
        }
    }

    public boolean isRenderBackgroundLeft() {
        return (register & 0x2) != 0;
    }

    public void setRenderBackgroundLeft(boolean renderBackgroundLeft) {
        if (renderBackgroundLeft) {
            register |= 0x2;
        } else {
            register &= ~0x2;
        }
    }

    public boolean isRenderSpritesLeft() {
        return (register & 0x4) != 0;
    }

    public void setRenderSpritesLeft(boolean renderSpritesLeft) {
        if (renderSpritesLeft) {
            register |= 0x4;
        } else {
            register &= ~0x4;
        }
    }

    public boolean isRenderBackground() {
        return (register & 0x8) != 0;
    }

    public void setRenderBackground(boolean renderBackground) {
        if (renderBackground) {
            register |= 0x8;
        } else {
            register &= ~0x8;
        }
    }

    public boolean isRenderSprites() {
        return (register & 0x10) != 0;
    }

    public void setRenderSprites(boolean renderSprites) {
        if (renderSprites) {
            register |= 0x10;
        } else {
            register &= ~0x10;
        }
    }

    public boolean isEmphasizeRed() {
        return (register & 0x20) != 0;
    }

    public void setEmphasizeRed(boolean emphasizeRed) {
        if (emphasizeRed) {
            register |= 0x20;
        } else {
            register &= ~0x20;
        }
    }

    public boolean isEmphasizeGreen() {
        return (register & 0x40) != 0;
    }

    public void setEmphasizeGreen(boolean emphasizeGreen) {
        if (emphasizeGreen) {
            register |= 0x40;
        } else {
            register &= ~0x40;
        }
    }

    public boolean isEmphasizeBlue() {
        return (register & 0x80) != 0;
    }

    public void setEmphasizeBlue(boolean emphasizeBlue) {
        if (emphasizeBlue) {
            register |= 0x80;
        } else {
            register &= ~0x80;
        }
    }
}
