package no.clueless.emulation.ppu;

import no.clueless.emulation.types.UnsignedByte;

public class PPU {
    private final byte[] vram       = new byte[2048];
    private final byte[] paletteRam = new byte[32];
    private final byte[] oam        = new byte[256];

    private UnsignedByte PPUCTRL;
    private UnsignedByte PPUMASK;
    private UnsignedByte PPUSTATUS;
    private UnsignedByte OAMADDR;
    private UnsignedByte OAMDATA;
    private UnsignedByte PPUSCROLL;
    private UnsignedByte PPUADDR;
    private UnsignedByte PPUDATA;
    private UnsignedByte OAMDMA;

    private int     currentVramAddress; // v
    private int     tempVramAddress; // t
    private int     x;
    private boolean addressLatch; // w

    public byte[] getVram() {
        return vram;
    }

    public void writePpuCtrl(UnsignedByte value) {
        PPUCTRL = value;

        // Extract nametable bits.
        var nametableBits = value.intValue() & 0x03;

        // Insert nametable bits into bits 10 and 11 of the temp vram address ('t').
        tempVramAddress = (tempVramAddress & 0xF3FF) | (nametableBits << 10);
    }

    public void writePpuMask(UnsignedByte value) {
        PPUMASK = value;
    }

    public void writeOamAddr(UnsignedByte value) {
        OAMADDR = value;
    }

    public void writeOamData(UnsignedByte value) {
        OAMDATA               = value;
        oam[value.intValue()] = value.byteValue();
        OAMADDR               = OAMADDR.increment();
    }

    public void writePpuScroll(UnsignedByte value) {
        PPUSCROLL = value;

        if (!addressLatch) {
            // First write: fine X (bits 0-2) and coarse X (bits 3-7 into 't' bits 0-4)
            x = value.intValue() & 0x07;
            tempVramAddress = (tempVramAddress & 0xFFE0) | (value.intValue() >> 3);
            addressLatch = true;
        } else {
            // Second write: fine Y (bits 0-2 into 't' bits 12-14) and coarse Y (bits 3-7 into 't' bits 5-9)
            tempVramAddress = (tempVramAddress & 0x8FFF) | ((value.intValue() & 0x07) << 12);
            tempVramAddress = (tempVramAddress & 0xFC1F) | ((value.intValue() >> 3) << 5);
            addressLatch = false;
        }
    }

    public void writePpuAddr(UnsignedByte value) {
        PPUADDR = value;
        var val = value.intValue();

        if (!addressLatch) {
            // First write: High byte of VRAM address (bits 8-13 into 't')
            tempVramAddress = (tempVramAddress & 0x00FF) | ((val & 0x3F) << 8);
            addressLatch = true;
        } else {
            // Second write: Low byte of VRAM address (bits 0-7 into 't')
            tempVramAddress = (tempVramAddress & 0xFF00) | val;
            // Copy temporary address 't' to active VRAM address 'v'
            currentVramAddress = tempVramAddress;
            addressLatch = false;
        }
    }

    public void writePpuData(UnsignedByte value) {
        PPUDATA = value;

        int addr = currentVramAddress & 0x3FFF;

        // 1. Route write to Nametable VRAM ($2000 - $3EFF)
        if (addr >= 0x2000 && addr <= 0x3EFF) {
            int vramIndex = (addr - 0x2000) & 0x07FF; // 2KB mirror mask
            vram[vramIndex] = value.byteValue();
        }
        // 2. Route write to Palette RAM ($3F00 - $3FFF)
        else if (addr >= 0x3F00 && addr <= 0x3FFF) {
            int paletteAddr = addr & 0x001F;
            if ((paletteAddr & 0x13) == 0x10) paletteAddr &= ~0x10; // Hardware mirror
            paletteRam[paletteAddr] = value.byteValue();
        }

        // 3. Auto-increment VRAM address (+1 across or +32 down)
        int step = (PPUCTRL.intValue() & 0x04) != 0 ? 32 : 1;
        currentVramAddress = (currentVramAddress + step) & 0x3FFF;
    }

    public void writeOamDma(UnsignedByte value) {
        OAMDMA = value;
    }
}
