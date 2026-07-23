package no.clueless.emulation.ppu;

public enum PpuCtrlFlag {
    BASE_NAMETABLE_ADDRESS_LOW(0x1),
    BASE_NAMETABLE_ADDRESS_HIGH(0x2),
    VRAM_ADDRESS_INCREMENT(0x4),
    SPRITE_PATTERN_TABLE_ADDRESS(0x8),
    BACKGROUND_PATTERN_TABLE_ADDRESS(0x10),
    SPRITE_SIZE(0x20),
    MASTER_SLAVE(0x40),
    VBLANK_NMI(0x80);

    private final int mask;

    PpuCtrlFlag(int mask) {
        this.mask = mask;
    }

    public int getMask() {
        return mask;
    }
}
