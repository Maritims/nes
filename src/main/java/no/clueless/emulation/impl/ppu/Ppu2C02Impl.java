package no.clueless.emulation.impl.ppu;

import no.clueless.emulation.Cartridge;
import no.clueless.emulation.Ppu2C02;

public class Ppu2C02Impl implements Ppu2C02 {
    /**
     * The PPUCTRL register ($2000, VPHB SINN).
     */
    private final PPUCTRL ppuctrl = new PPUCTRL();
    /**
     * The PPUMASK register ($2001, BGRs bMmG).
     */
    private final PPUMASK ppumask = new PPUMASK();
    /**
     * The PPUSTATUS register ($2002, VSO- ----).
     */
    private       int     ppustatus;
    /**
     * The OAM read/write address ($2003).
     */
    private       int     oamaddr;
    /**
     * The OAM data ($2004).
     */
    private       OAM     oamdata;
    /**
     * The PPUSCROLL register ($2005, XXXX XXXX YYYY YYYY).
     */
    private       int     ppuscroll;
    /**
     * The VRAM address ($2006).
     */
    private       int     ppuaddr;
    /**
     * The VRAM data ($2007).
     */
    private       int     ppudata;
    /**
     * The OAM DMA high address ($4014).
     */
    private       int     oamdma;

    /**
     * The current VRAM address. In the hardware this is read from the internal 'v' register outside rendering. In software it is more practical with a dedicated field.
     */
    private final LoopyRegister currentVramAddress = new LoopyRegister();
    /**
     * The temporary VRAM address before it is transferred to the 'v' register. In the hardware this is read from the internal 't' register outside rendering. In software it is more practical with a dedicated field.
     */
    private final LoopyRegister tempVramAddress    = new LoopyRegister();

    /**
     * The fine X scroll position. In the hardware this is read from the internal 'x' register during rendering. In software, it is more practical with a dedicated field.
     */
    private int fineX;
    /**
     * The write-latch indicating whether this is the first or second write. In the hardware, this is read from the 'w' register. In software, it is more practical with a dedicated field.
     */
    private int writeLatch;

    private final PatternTable[]   patternTables    = new PatternTable[]{new PatternTable(), new PatternTable()};
    private final NameTableManager nameTableManager = new NameTableManager();
    private final PaletteRAM       paletteRAM       = new PaletteRAM();

    private int scanLine = 0;
    private int cycle    = 0;

    /**
     * The PPU data buffer.
     */
    private int dataBuffer;

    private Cartridge cartridge;

    @Override
    public void connectToCartridge(Cartridge cartridge) {
        this.cartridge = cartridge;
    }

    @Override
    public void clock() {
        if (scanLine >= -1 && scanLine < 240) {
            if (scanLine == -1 && cycle == 1) {
                // This is the start of a new frame.

                // Clear VBLANK.
                ppustatus &= ~(1 << 7);

                // Clear Sprite 0 Hit.
                ppustatus &= ~(1 << 6);

                // Clear Sprite Overflow.
                ppustatus &= ~(1 << 5);

                // TODO: Clear shifters, whatever those are.
            }

            if((cycle >= 2 && cycle < 258) || (cycle >= 321 && cycle < 338)) {
                // TODO: Update shifters, whatever those are.

                switch ((cycle -1) % 8) {
                    case 0:
                        // TODO: Load background shifters, whatever those are.
                        break;
                    case 2:
                        // TODO: Load background tile attribute somehow.
                        break;
                    case 4:
                        // TODO: Load background tile LSB somehow.
                        break;
                    case 6:
                        // TODO: Load background tile MSB somehow.
                        break;
                    case 7:
                        // TODO: Handle scroll.
                        break;
                }
            }

            if (cycle == 256) {
                // End of the line, go to the next line.
                // TODO: Increment Y.
            }

            if(cycle == 257) {
                // TODO: Reset X.
            }
        }
    }

    @Override
    public void reset() {
        fineX      = 0;
        writeLatch = 0;
        dataBuffer = 0;
        ppustatus  = 0;
        ppuctrl.write(0);
        ppumask.write(0);
        currentVramAddress.write(0);
        tempVramAddress.write(0);
    }

    private void populateDataBuffer(int address) {
        address &= 0x3FFF;

        if (cartridge != null) {
            var cartridgeData = cartridge.ppuRead(address).orElse(null);
            if (cartridgeData != null) {
                dataBuffer = cartridgeData;
                return;
            }
        }

        if (address <= 0x1FFF) {
            var patternTableIndex = (address & 0x1000) >> 12;
            dataBuffer = patternTables[patternTableIndex].read(address & 0x0FFF);
        } else if (address <= 0x3EFF) {
            dataBuffer = nameTableManager.read(address);
        } else {
            dataBuffer = paletteRAM.read(address);
        }
    }

    @Override
    public int read(int address) {
        address &= 0xFFFF;

        return switch (address) {
            case 0x2002 -> {
                // Only the 3 bits furthest to the left in the PPUSTATUS register contain status information.
                // However, when reading the PPUSTATUS register, the bottom 5 bits is expected to contain data from the previous PPU bus operation.
                var data = (ppustatus & 0xE0) | (dataBuffer & 0x1F);

                // Clear the VBLANK flag.
                ppustatus &= ~(1 << 7);

                // Clear the write-latch.
                writeLatch = 0;

                yield data;
            }
            case 0x2004 -> oamdata.read(oamaddr);
            case 0x2007 -> {
                // Reading from the PPUDATA register is delayed by one cycle.
                // Rather than returning the data in the register, data is returned from an internal data buffer.
                // The buffer is updated on every read from the PPUDATA register, but only after the previous contents have been returned to the CPU.
                var vramAddress = currentVramAddress.read() & 0x3FFF;
                var data        = dataBuffer;
                populateDataBuffer(vramAddress);

                // The $3F00-$3FFF range of VRAM contains palette data on later PPUs, specifically the 2C02G, 2C02H and PAL PPUs.
                if (vramAddress >= 0x3F00) {
                    data       = paletteRAM.read(vramAddress);
                    dataBuffer = nameTableManager.read(vramAddress);
                }

                // The VRAM address is incremented after each read from the PPUDATA register.
                var increment = ppuctrl.getIncrementMode() == 0 ? 1 : 32;
                currentVramAddress.write(currentVramAddress.read() + increment);

                yield data;
            }
            default -> throw new IllegalStateException("Unexpected value: " + address);
        };
    }

    @Override
    public void write(int address, int value) {
        address &= 0xFFFF;
        value &= 0xFF;

        switch (address) {
            case 0x2000:
                ppuctrl.write(value);
                tempVramAddress.setNameTableX(ppuctrl.getNameTableX());
                tempVramAddress.setNameTableY(ppuctrl.getNameTableY());
                break;
            case 0x2001:
                ppumask.write(value);
                break;
            case 0x2003:
                oamaddr = value;
                break;
            case 0x2004:
                oamdata.write(oamaddr, value);
                break;
            case 0x2005:
                if (writeLatch == 0) {
                    fineX = value & 0x07;
                    tempVramAddress.setCoarseX(value);
                    writeLatch = 1;
                } else {
                    tempVramAddress.setFineY(value);
                    tempVramAddress.setCoarseY(value);
                    writeLatch = 0;
                }
                break;
            case 0x2006:
                if (writeLatch == 0) {
                    var highByte = (value & 0x3F) << 8;
                    var lowByte  = tempVramAddress.read() & 0x00FF;
                    tempVramAddress.write(highByte | lowByte);
                    writeLatch = 1;
                } else {
                    var highByte = tempVramAddress.read() & 0xFF00;
                    tempVramAddress.write(highByte | value);
                    currentVramAddress.write(tempVramAddress.read());
                    writeLatch = 0;
                }
                break;
            case 0x2007:
                ppuWrite(currentVramAddress.read(), value);

                var increment = ppuctrl.getIncrementMode() == 0 ? 1 : 32;
                currentVramAddress.write(currentVramAddress.read() + increment);
                break;
        }
    }

    private void ppuWrite(int address, int value) {
        value &= 0xFF;

        if (cartridge.ppuWrite(address, value)) {
            return;
        }

        if (address <= 0x1FFF) {
            var patternTableIndex = (address & 0x1000) >> 12;
            patternTables[patternTableIndex].write(address & 0x0FFF, value);
            return;
        }

        if (address <= 0x3EFF) {
            address &= 0x0FFF;
            nameTableManager.write(address, value);
            return;
        }

        paletteRAM.write(address, value);
    }
}
