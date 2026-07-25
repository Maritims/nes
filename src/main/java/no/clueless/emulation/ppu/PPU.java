package no.clueless.emulation.ppu;

import no.clueless.emulation.types.UnsignedByte;
import no.clueless.emulation.types.UnsignedWord;

public class PPU {
    private final PPUBus ppuBus;
    private final byte[] oam = new byte[256];

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

    private UnsignedByte vramReadBuffer = UnsignedByte.ZERO;

    public PPU(PPUBus ppuBus) {
        this.ppuBus = ppuBus;
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
        OAMDATA                 = value;
        oam[OAMADDR.intValue()] = value.byteValue();
        OAMADDR                 = OAMADDR.increment();
    }

    public void writePpuScroll(UnsignedByte value) {
        PPUSCROLL = value;

        if (!addressLatch) {
            // First write: fine X (bits 0-2) and coarse X (bits 3-7 into 't' bits 0-4)
            x               = value.intValue() & 0x07;
            tempVramAddress = (tempVramAddress & 0xFFE0) | (value.intValue() >> 3);
            addressLatch    = true;
        } else {
            // Second write: fine Y (bits 0-2 into 't' bits 12-14) and coarse Y (bits 3-7 into 't' bits 5-9)
            tempVramAddress = (tempVramAddress & 0x8FFF) | ((value.intValue() & 0x07) << 12);
            tempVramAddress = (tempVramAddress & 0xFC1F) | ((value.intValue() >> 3) << 5);
            addressLatch    = false;
        }
    }

    public void writePpuAddr(UnsignedByte value) {
        PPUADDR = value;
        var val = value.intValue();

        if (!addressLatch) {
            // First write: High byte of VRAM address (bits 8-13 into 't')
            tempVramAddress = (tempVramAddress & 0x00FF) | ((val & 0x3F) << 8);
            addressLatch    = true;
        } else {
            // Second write: Low byte of VRAM address (bits 0-7 into 't')
            tempVramAddress = (tempVramAddress & 0xFF00) | val;
            // Copy temporary address 't' to active VRAM address 'v'
            currentVramAddress = tempVramAddress;
            addressLatch       = false;
        }
    }

    public void writePpuData(UnsignedByte value) {
        PPUDATA = value;

        ppuBus.write(new UnsignedWord(currentVramAddress), value);

        // Auto-increment VRAM address (+1 across or +32 down)
        var step = (PPUCTRL.intValue() & 0x04) != 0 ? 32 : 1;
        currentVramAddress = (currentVramAddress + step) & 0x3FFF;
    }

    public void writeOamDma(UnsignedByte value) {
        OAMDMA = value;
    }

    public UnsignedByte readPpuStatus() {
        var status = PPUSTATUS;
        addressLatch = false;
        PPUSTATUS    = PPUSTATUS.and(new UnsignedByte(0x7F));
        return status;
    }

    public UnsignedByte readOamData() {
        return new UnsignedByte(oam[OAMADDR.intValue()]);
    }

    public UnsignedByte readPpuData() {
        UnsignedWord address = new UnsignedWord(currentVramAddress);
        UnsignedByte data    = ppuBus.readByte(address);

        // If address is in VRAM ($0000-$3EFF), return previous buffered byte and store new data in buffer
        if ((currentVramAddress & 0x3FFF) < 0x3F00) {
            UnsignedByte bufferedResult = vramReadBuffer;
            vramReadBuffer = data;
            data           = bufferedResult;
        } else {
            // Palette RAM ($3F00-$3FFF) reads return immediately, but update buffer with mirrored Nametable byte below
            vramReadBuffer = ppuBus.readByte(new UnsignedWord(currentVramAddress - 0x1000));
        }

        // Auto-increment VRAM address
        int step = (PPUCTRL.intValue() & 0x04) != 0 ? 32 : 1;
        currentVramAddress = (currentVramAddress + step) & 0x7FFF;

        return data;
    }
}
