package no.clueless.emulation;

import no.clueless.emulation.cartridge.Cartridge;
import no.clueless.emulation.ppu.PPU;
import no.clueless.emulation.ram.RAM;
import no.clueless.emulation.types.UnsignedWord;
import no.clueless.emulation.types.UnsignedByte;

/**
 * A basic system bus that maps RAM and a Cartridge.
 */
public class BetaBus implements Bus {
    private static final int RAM_START           = 0x0000;
    private static final int RAM_END             = 0x1FFF;
    private static final int RAM_SIZE            = 0x0800;
    private static final int PPU_REGISTERS_START = 0x2000;
    private static final int PPU_REGISTERS_END   = 0x3FFF;
    private static final int APU_IO_START        = 0x4000;
    private static final int APU_IO_END          = 0x4017;
    private static final int APU_TEST_START      = 0x4018;
    private static final int APU_TEST_END        = 0x401F;

    private static final UnsignedWord CARTRIDGE_START = new UnsignedWord(0x4020);
    private static final UnsignedWord CARTRIDGE_END   = new UnsignedWord(0xFFFF);

    private final RAM       ram;
    private final Cartridge cartridge;
    private final PPU       ppu;

    public BetaBus(RAM ram, Cartridge cartridge, PPU ppu) {
        this.ram       = ram;
        this.cartridge = cartridge;
        this.ppu       = ppu;
    }

    @Override
    public Cpu6502 getCpu() {
        return null;
    }

    @Override
    public Ppu2C02 getPpu() {
        return null;
    }

    @Override
    public APU getApu() {
        return null;
    }

    @Override
    public no.clueless.emulation.Cartridge getCartridge() {
        return null;
    }

    @Override
    public void insertCartridge(no.clueless.emulation.Cartridge cartridge) {

    }

    @Override
    public void clock() {

    }

    @Override
    public int read(int address) {
        address &= 0xFFFF;

        if (address >= RAM_START && address <= RAM_END) {
            return ram.read(address % RAM_SIZE);
        } else if (address <= PPU_REGISTERS_END) {
            var register = address & 0x0007;
            return switch (register) {
                case 2 -> ppu.readPpuStatus().intValue();
                case 4 -> ppu.readOamData().intValue();
                case 7 -> ppu.readPpuData().intValue();
                default -> UnsignedByte.ZERO.intValue();
            };
        } else if (address <= APU_IO_END) {
            return 0;
        } else if (address <= APU_TEST_END) {
            return 0;
        } else {
            return cartridge.readCpu(new UnsignedWord(address)).intValue();
        }
    }

    @Override
    public void write(int address, int value) {
        if (address >= RAM_START && address <= RAM_END) {
            ram.write(address % RAM_SIZE, value);
        } else if (address >= PPU_REGISTERS_START && address <= PPU_REGISTERS_END) {
            var register = address & 0x0007;
            switch (register) {
                case 0 -> ppu.writePpuCtrl(new UnsignedByte(value));
                case 1 -> ppu.writePpuMask(new UnsignedByte(value));
                case 3 -> ppu.writeOamAddr(new UnsignedByte(value));
                case 4 -> ppu.writeOamData(new UnsignedByte(value));
                case 5 -> ppu.writePpuScroll(new UnsignedByte(value));
                case 6 -> ppu.writePpuAddr(new UnsignedByte(value));
                case 7 -> ppu.writePpuData(new UnsignedByte(value));
            }
        } else if (address >= APU_IO_START && address <= APU_IO_END) {
            // APU/IO - Not implemented
        } else if (address >= APU_TEST_START && address <= APU_TEST_END) {
            // APU/IO
        } else if (address == 0x4014) {
            ppu.writeOamDma(new UnsignedByte(value));
        } else {
            //cartridge.writeCpu(address, value);
        }
    }

    @Override
    public void reset() {

    }
}
