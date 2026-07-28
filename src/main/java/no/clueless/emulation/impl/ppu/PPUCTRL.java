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

    public int getNameTableX() {
        return register & 0x01;
    }

    public int getNameTableY() {
        return (register >> 1) & 0x01;
    }

    public int getIncrementMode() {
        return (register >> 2) & 0x01;
    }

    public int getSpritePatternTableAddress() {
        return (register >> 3) & 0x01;
    }

    public int getBackgroundPatternTableAddress() {
        return (register >> 4) & 0x01;
    }

    public int getSpriteSize() {
        return (register >> 5) & 0x01;
    }

    public int getPPUMasterSlaveSelect() {
        return (register >> 6) & 0x01;
    }

    public int getNMIEnable() {
        return (register >> 7) & 0x01;
    }

    public void setNameTableX(boolean value) {
        register = (register & ~0x01) | (value ? 0x01 : 0);
    }

    public void setNameTableY(boolean value) {
        register = (register & ~0x02) | (value ? 0x02 : 0);
    }
}
