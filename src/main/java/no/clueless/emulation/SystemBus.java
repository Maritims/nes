package no.clueless.emulation;

import no.clueless.emulation.cartridge.Cartridge;
import no.clueless.emulation.ppu.PPU;
import no.clueless.emulation.ram.RAM;
import no.clueless.emulation.types.UnsignedWord;
import no.clueless.emulation.types.UnsignedByte;

/**
 * A basic system bus that maps RAM and a Cartridge.
 */
public class SystemBus implements Bus {
    private static final UnsignedWord RAM_START = new UnsignedWord(0x0000);
    private static final UnsignedWord RAM_END   = new UnsignedWord(0x1FFF);
    private static final UnsignedWord RAM_SIZE  = new UnsignedWord(0x0800);

    private static final UnsignedWord PPU_REGISTERS_START = new UnsignedWord(0x2000);
    private static final UnsignedWord PPU_REGISTERS_END   = new UnsignedWord(0x3FFF);

    private static final UnsignedWord APU_IO_START = new UnsignedWord(0x4000);
    private static final UnsignedWord APU_IO_END   = new UnsignedWord(0x4017);

    private static final UnsignedWord APU_TEST_START = new UnsignedWord(0x4018);
    private static final UnsignedWord APU_TEST_END   = new UnsignedWord(0x401F);

    private static final UnsignedWord CARTRIDGE_START = new UnsignedWord(0x4020);
    private static final UnsignedWord CARTRIDGE_END   = new UnsignedWord(0xFFFF);

    private final RAM       ram;
    private final Cartridge cartridge;
    private final PPU       ppu;

    public SystemBus(RAM ram, Cartridge cartridge, PPU ppu) {
        this.ram       = ram;
        this.cartridge = cartridge;
        this.ppu       = ppu;
    }

    @Override
    public UnsignedByte read(UnsignedWord address) {
        if (address.between(RAM_START, RAM_END)) {
            return ram.read(address.modulo(RAM_SIZE));
        } else if (address.between(PPU_REGISTERS_START, PPU_REGISTERS_END)) {
            var register = address.intValue() & 0x0007;
            return switch (register) {
                case 2 -> ppu.readPpuStatus();
                case 4 -> ppu.readOamData();
                case 7 -> ppu.readPpuData();
                default -> UnsignedByte.ZERO;
            };
        } else if (address.between(APU_IO_START, APU_IO_END)) {
            return UnsignedByte.ZERO;
        } else if (address.between(APU_TEST_START, APU_TEST_END)) {
            return UnsignedByte.ZERO;
        } else {
            return cartridge.readCpu(address);
        }
    }

    @Override
    public void write(UnsignedWord address, UnsignedByte value) {
        if (address.between(RAM_START, RAM_END)) {
            ram.write(address.modulo(RAM_SIZE), value);
        } else if (address.between(PPU_REGISTERS_START, PPU_REGISTERS_END)) {
            var register = address.intValue() & 0x0007;
            switch (register) {
                case 0 -> ppu.writePpuCtrl(value);
                case 1 -> ppu.writePpuMask(value);
                case 3 -> ppu.writeOamAddr(value);
                case 4 -> ppu.writeOamData(value);
                case 5 -> ppu.writePpuScroll(value);
                case 6 -> ppu.writePpuAddr(value);
                case 7 -> ppu.writePpuData(value);
            }
        } else if (address.between(APU_IO_START, APU_IO_END)) {
            // APU/IO - Not implemented
        } else if (address.between(APU_TEST_START, APU_TEST_END)) {
            // APU/IO
        } else if (address.intValue() == 0x4014) {
            ppu.writeOamDma(value);
        } else {
            //cartridge.writeCpu(address, value);
        }
    }
}
