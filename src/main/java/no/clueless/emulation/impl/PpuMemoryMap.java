package no.clueless.emulation.impl;

public class PpuMemoryMap {
    public static final int PATTERN_TABLES_START      = 0x0000;
    public static final int PATTERN_TABLES_END        = 0x1FFF;
    public static final int PATTERN_TABLE_SIZE        = 0x1000;
    public static final int NAME_TABLE_START          = 0x2000;
    public static final int PPUSTATUS                 = 0x2002;
    public static final int OAMDATA                   = 0x2004;
    public static final int PPUDATA                   = 0x2007;
    public static final int PPUCTRL                   = 0x2000;
    public static final int PPUMASK                   = 0x2001;
    public static final int OAMADDR                   = 0x2003;
    public static final int PPUSCROLL                 = 0x2005;
    public static final int PPUADDR                   = 0x2006;
    public static final int ATTRIBUTE_TABLE_0_START   = 0x23C0;
    /**
     * The size of a name table is 1024 bytes (0x0400).
     * <p>
     * Since the name table arrays are 0-indexed, we need a bitmask for 1023 which we can use to ensure addresses won't make us go out of bounds when accessing name tables.
     */
    public static final int NAME_TABLE_SIZE_MINUS_ONE = 0x03FF;
    public static final int UNUSED_END                = 0x3EFF;
    public static final int PALETTE_RAM_START         = 0x3F00;
    public static final int PALETTE_RAM_END           = 0x3FFF;
}
