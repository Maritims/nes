package no.clueless.emulation.impl;

public final class CpuMemoryMap {
    public static final int RAM_START          = 0x0000;
    public static final int RAM_END            = 0x1FFF;
    public static final int PPU_REGISTER_START = 0x2000;
    public static final int PPU_REGISTER_END   = 0x3FFF;
    public static final int APU_START          = 0x4000;
    public static final int APU_END            = 0x4015;
    public static final int IO_START           = 0x4016;
    public static final int IO_END             = 0x4017;
    public static final int APU_TEST_START     = 0x4018;
    public static final int APU_TEST_END       = 0x401F;
    public static final int WRAM_START         = 0x6000;
    public static final int WRAM_END           = 0x7FFF;
    public static final int PRG_ROM_START      = 0x8000;
    public static final int PRG_ROM_END        = 0xFFFF;
}
