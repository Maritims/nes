package no.clueless.emulation.impl.ppu;

import no.clueless.emulation.Cartridge;
import no.clueless.emulation.Ppu2C02;

public class Ppu2C02Impl implements Ppu2C02 {
    /**
     * The PPUCTRL register ($2000, VPHB SINN).
     */
    private PPUCTRL ppuctrl;
    /**
     * The PPUMASK register ($2001, BGRs bMmG).
     */
    private int     ppumask;
    /**
     * The PPUSTATUS register ($2002, VSO- ----).
     */
    private int     ppustatus;
    /**
     * The OAM read/write address ($2003).
     */
    private int     oamaddr;
    /**
     * The OAM data ($2004).
     */
    private OAM     oamdata;
    /**
     * The PPUSCROLL register ($2005, XXXX XXXX YYYY YYYY).
     */
    private int     ppuscroll;
    /**
     * The VRAM address ($2006).
     */
    private int     ppuaddr;
    /**
     * The VRAM data ($2007).
     */
    private int     ppudata;
    /**
     * The OAM DMA high address ($4014).
     */
    private int     oamdma;

    /**
     * The current VRAM address. In the hardware this is read from the internal 'v' register outside rendering. In software it is more practical with a dedicated field.
     */
    private int currentVramAddress;
    /**
     * The temporary VRAM address before it is transferred to the 'v' register. In the hardware this is read from the internal 't' register outside rendering. In software it is more practical with a dedicated field.
     */
    private int tempVramAddress;

    /**
     * The fine X scroll position. In the hardware this is read from the internal 'x' register during rendering. In software, it is more practical with a dedicated field.
     */
    private int fineX;
    /**
     * The write-latch indicating whether this is the first or second write. In the hardware, this is read from the 'w' register. In software, it is more practical with a dedicated field.
     */
    private int writeLatch;

    private final PatternTable[] patternTables = new PatternTable[]{new PatternTable(), new PatternTable()};

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

    }

    @Override
    public void reset() {

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
                ppustatus &= (1 << 7);

                // Clear the write-latch.
                writeLatch = 0;

                yield data;
            }
            case 0x2004 -> oamdata.read(oamaddr);
            case 0x2007 -> {
                // Reading from the PPUDATA register is delayed by one cycle.
                // Rather than returning the data in the register, data is returned from an internal data buffer.
                // The buffer is updated on every read from the PPUDATA register, but only after the previous contents have been returned to the CPU.
                var data = dataBuffer;
                //populateDataBuffer();

                // The $3F00-$3FFF range of VRAM contains palette data on later PPUs, specifically the 2C02G, 2C02H and PAL PPUs.
                if (currentVramAddress >= 0x3F00) {
                    data = dataBuffer;
                }

                // The VRAM address is incremented after each read from the PPUDATA register.
                currentVramAddress += ppuctrl.getVramAddressIncrement();

                yield data;
            }
            default -> throw new IllegalStateException("Unexpected value: " + address);
        };
    }

    void populateDataBuffer(int address) {
        address &= 0x3FFF;

        var foo = cartridge.readPrgRom(currentVramAddress).orElse(null);
        if (foo != null) {
            dataBuffer = foo;
            return;
        }

        if (address <= 0x0FFF) {
            dataBuffer = patternTables[0].read(address);
        } else if (address <= 0x1FFF) {
            dataBuffer = patternTables[1].read(address);
        } else if (address <= 0x23BF) {
            // Nametable 0
        } else if (address <= 0x23FF) {
            // Attribute table 0
        } else if (address <= 0x27BF) {
            // Nametable 1
        } else if (address <= 0x27FF) {
            // Attribute table 1
        } else if (address <= 0x2BBF) {
            // Nametable 2
        } else if (address <= 0x2BFF) {
            // Attribute table 2
        } else if (address <= 0x2FBF) {
            // Nametable 3
        } else if (address <= 0x2FFF) {
            // Attribute table 3
        } else if (address >= 0x3F00) {
            // Palette RAM
        }
    }

    @Override
    public void write(int address, int value) {
        address &= 0xFFFF;
        value &= 0xFF;
    }
}
