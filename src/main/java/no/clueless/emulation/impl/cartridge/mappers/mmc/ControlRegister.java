package no.clueless.emulation.impl.cartridge.mappers.mmc;

import static no.clueless.emulation.impl.Masks.BOTTOM_5_BITS;

public class ControlRegister {
    private int register;

    public int getRegister() {
        return register;
    }

    public void setRegister(int register) {
        this.register = register & BOTTOM_5_BITS;
    }

    public int getNameTableArrangement() {
        return register & 0x3;
    }

    public void setNameTableArrangement(int value) {
        this.register = (this.register & ~0x3) | (value & 0x3);
    }

    /**
     * Gets the PRG-ROM bank mode as an 8-bit value.
     * @return <p>0, 1: switch 32 KB at $8000, ignoring low bit of bank number.</p>
     * <p>2: fix first bank at $8000 and switch 16 KB bank at $C000.</p>
     * <p>3: fix last bank at $C000 and switch 16 KB bank at $8000.</p>
     */
    public PrgRomBankMode getPrgRomBankMode() {
        var value = (register & 0x0C) >> 2;
        return switch (value) {
            case 0 -> PrgRomBankMode.SWITCH_32K_LOWER_BIT_IGNORED_0;
            case 1 -> PrgRomBankMode.SWITCH_32K_LOWER_BIT_IGNORED_1;
            case 2 -> PrgRomBankMode.FIX_FIRST_BANK_SWITCH_16K_AT_C000;
            case 3 -> PrgRomBankMode.FIX_LAST_BANK_SWITCH_16K_AT_8000;
            default -> throw new IllegalStateException("Unexpected value: " + value);
        };
    }

    public void setPrgRomBankMode(int value) {
        register = (register & ~0x0C) | ((value & 0x3) << 2);
    }

    /**
     * @return 0 (switch 8KB at a time) or 1 (switch two separate 4 KB banks).
     */
    public ChrRomBankMode getChrRomBankMode() {
        var value = (register & 0x10) >> 4;
        return switch (value) {
            case 0 -> ChrRomBankMode.MODE_4KB;
            case 1 -> ChrRomBankMode.MODE_8KB;
            default -> throw new IllegalStateException("Unexpected value: " + value);
        };
    }

    public void setChrRomBankMode(boolean value) {
        if (value) {
            register |= 0x10;
        } else {
            register &= ~0x10;
        }
    }
}
