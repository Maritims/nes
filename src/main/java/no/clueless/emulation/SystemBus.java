package no.clueless.emulation;

import no.clueless.emulation.ram.RAM;
import no.clueless.emulation.types.UInt16;
import no.clueless.emulation.types.UInt8;

/**
 * A basic system bus that maps RAM and a Cartridge.
 */
public class SystemBus implements Bus {
    private final RAM       ram;
    private final Cartridge cartridge;

    public SystemBus(RAM ram, Cartridge cartridge) {
        this.ram       = ram;
        this.cartridge = cartridge;
    }

    @Override
    public UInt8 read(UInt16 address) {
        int addr = address.value();
        // RAM: 0x0000 - 0x1FFF (mirrored every 0x0800)
        if (addr >= 0x0000 && addr <= 0x1FFF) {
            return ram.read(new UInt16(addr % 0x0800));
        }
        // PPU Registers: 0x2000 - 0x3FFF (mirrored every 8 bytes)
        else if (addr >= 0x2000 && addr <= 0x3FFF) {
            // Not implemented yet
            return UInt8.ZERO;
        }
        // APU and I/O Registers: 0x4000 - 0x4017
        else if (addr >= 0x4000 && addr <= 0x4017) {
            // Not implemented yet
            return UInt8.ZERO;
        }
        // APU and I/O functionality (normally disabled): 0x4018 - 0x401F
        else if (addr >= 0x4018 && addr <= 0x401F) {
            return UInt8.ZERO;
        }
        // Cartridge Space: 0x4020 - 0xFFFF
        else {
            return cartridge.readCpu(address);
        }
    }

    @Override
    public void write(UInt16 address, UInt8 value) {
        int addr = address.value();
        if (addr >= 0x0000 && addr <= 0x1FFF) {
            ram.write(new UInt16(addr % 0x0800), value);
        }
        else if (addr >= 0x2000 && addr <= 0x3FFF) {
            // PPU - Not implemented
        }
        else if (addr >= 0x4000 && addr <= 0x4017) {
            // APU/IO - Not implemented
        }
        else if (addr >= 0x4018 && addr <= 0x401F) {
            // APU/IO
        }
        else {
            cartridge.writeCpu(address, value);
        }
    }
}
