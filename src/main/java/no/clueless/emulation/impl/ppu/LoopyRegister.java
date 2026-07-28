package no.clueless.emulation.impl.ppu;

/**
 * Represents the Loopy Register.
 * <p>Source: <a href="https://www.nesdev.org/wiki/PPU_scrolling#PPU_internal_registers">https://www.nesdev.org/wiki/PPU_scrolling#PPU_internal_registers</a></p>
 */
public class LoopyRegister {
    private int register;

    public void write(int register) {
        this.register = register & 0xFFFF;
    }

    public int read() {
        return register;
    }

    public int getCoarseX() {
        return register & 0x1F;
    }

    public int getCoarseY() {
        return (register >> 5) & 0x1F;
    }

    public boolean isNameTableX() {
        return ((register >> 10) & 0x01) != 0;
    }

    public boolean isNameTableY() {
        return ((register >> 11) & 0x01) != 0;
    }

    public int getFineY() {
        return (register >> 12) & 0x07;
    }

    public void setCoarseX(int value) {
        register = (register & ~0x001F) | (value & 0x001F);
    }

    public void setCoarseY(int value) {
        register = (register & ~0x03E0) | ((value & 0x1F) << 5);
    }

    public void setNameTableX(int value) {
        register = (register & ~0x0400) | (value & 0x0400);
    }

    public void setNameTableY(int value) {
        register = (register & ~0x0800) | (value & 0x0800);
    }

    public void setFineY(int value) {
        register = (register & ~0x7000) | ((value & 0x07) << 12);
    }
}
