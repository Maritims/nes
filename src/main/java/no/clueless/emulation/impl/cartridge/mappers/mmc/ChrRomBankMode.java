package no.clueless.emulation.impl.cartridge.mappers.mmc;

/**
 * Select 4 KB or 8 KB CHR bank at PPU $0000.
 */
public enum ChrRomBankMode {
    MODE_4KB(0x1F),
    /**
     * Bit 0 is ignored in 8 KB mode.
     */
    MODE_8KB(0x1E);

    private final int bitmask;

    ChrRomBankMode(int bitmask) {
        this.bitmask = bitmask;
    }

    public int getBitmask() {
        return bitmask;
    }
}
