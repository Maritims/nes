package no.clueless.emulation.gui;

public enum MirroringMode {
    VERTICAL,
    HORIZONTAL,
    SINGLE_SCREEN_LOWER,
    SINGLE_SCREEN_UPPER;

    /**
     * Resolves a slot to a physical table index.
     */
    public int resolvePhysicalTableIndex(int slot) {
        return switch (this) {
            case VERTICAL -> (slot == 0 || slot == 2) ? 0 : 1;
            case HORIZONTAL -> (slot == 0 || slot == 1) ? 0 : 1;
            case SINGLE_SCREEN_LOWER -> 0;
            case SINGLE_SCREEN_UPPER -> 1;
        };
    }
}
