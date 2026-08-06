package no.clueless.emulation.impl.ppu;

import no.clueless.emulation.impl.PpuMemoryMap;
import org.slf4j.Logger;

import java.util.function.BiConsumer;
import java.util.function.Function;

import static no.clueless.emulation.impl.Masks.*;
import static no.clueless.emulation.impl.Masks.MASK_8BIT;
import static no.clueless.emulation.impl.PpuMemoryMap.*;
import static no.clueless.emulation.impl.PpuMemoryMap.PPUDATA;

public class PpuRegisterHandler {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(PpuRegisterHandler.class);

    private static void logUnknownRegister(int address) {
        log.warn("Unknown register: {}", "%04X".formatted(address));
    }

    private final PpuRegisters registers;

    private final Function<Integer, Integer>   readBus;
    private final BiConsumer<Integer, Integer> writeBus;

    private int dataBuffer   = 0;
    private int addressLatch = 0;
    private int oamaddr      = 0;
    private int fineX        = 0;

    public PpuRegisterHandler(PpuRegisters registers, Function<Integer, Integer> readBus, BiConsumer<Integer, Integer> writeBus) {
        this.registers = registers;
        this.readBus   = readBus;
        this.writeBus  = writeBus;
    }

    public void reset() {
        dataBuffer   = 0;
        addressLatch = 0;
        oamaddr      = 0;
        fineX        = 0;

        registers.reset();
    }

    public int getFineX() {
        return fineX;
    }

    private int readStatus() {
        var data = (registers.status().getRegister() & BYTE_TOP_3_BITS) | (dataBuffer & BOTTOM_5_BITS);


        // Clear the VBLANK flag.
        registers.status().setVerticalBlank(false);

        // TEMPORARY: We have ot mock this to avoid ending up in an infinite loop on the Super Mario Bros. start screen.
        registers.status().setSpriteZeroHit(true);

        // Clear the write-latch.
        addressLatch = 0;

        return data;
    }

    private int readData() {
        var data = dataBuffer;
        dataBuffer = readBus.apply(registers.vramAddress().getRegister());

        if (registers.vramAddress().getRegister() >= PALETTE_RAM_START) {
            data = dataBuffer;
        }

        // The VRAM address is incremented after each read from the PPUDATA register.
        var increment = registers.control().getIncrementMode() == 0 ? 1 : 32;
        registers.vramAddress().setRegister(registers.vramAddress().getRegister() + increment);

        return data;
    }

    /**
     * Only the registers {@link PpuMemoryMap#PPUSTATUS}, {@link PpuMemoryMap#OAMDATA} and {@link PpuMemoryMap#PPUDATA} are readable.
     */
    public int read(int address) {
        return switch (address) {
            case PPUSTATUS -> readStatus();
            case OAMDATA -> registers.primaryOAM().read(oamaddr);
            case PPUDATA -> readData();
            default -> {
                logUnknownRegister(address);
                yield 0x00;
            }
        };
    }

    /**
     * TODO: Add documentation.
     */
    private void writeControl(int data) {
        registers.control().setRegister(data);
        registers.tempVramAddress().setNameTableX((registers.control().getNameTableX() & 0x400) != 0);
        registers.tempVramAddress().setNameTableY((registers.control().getNameTableY() & 0x800) != 0);
    }

    /**
     * TODO: Add documentation.
     */
    private void writeScroll(int data) {
        if (addressLatch == 0) {
            fineX = data & 0x07;
            registers.tempVramAddress().setCoarseX(data >> 3);
            addressLatch = 1;
        } else {
            registers.tempVramAddress().setFineY(data & 0x07);
            registers.tempVramAddress().setCoarseY(data >> 3);
            addressLatch = 0;
        }
    }

    /**
     * TODO: Add documentation.
     */
    private void writeAddr(int data) {
        if (addressLatch == 0) {
            registers.tempVramAddress().setRegister(((data & 0x3F) << 8) | (registers.tempVramAddress().getRegister() & 0x00FF));
            addressLatch = 1;
        } else {
            registers.tempVramAddress().setRegister((registers.tempVramAddress().getRegister() & 0xFF00) | data);
            registers.vramAddress().setRegister(registers.tempVramAddress().getRegister());
            addressLatch = 0;
        }
    }

    /**
     * Writes to the PPUDATA register will add either 1 (horizontal progression) or 32 (vertical progression) to {@link PpuRegisters#vramAddress()} depending on the return value of the {@link PpuControl#getIncrementMode()}.
     */
    private void writeData(int data) {
        writeBus.accept(registers.vramAddress().getRegister(), data);
        var increment = registers.control().getIncrementMode() == 0 ? 1 : 32;
        registers.vramAddress().setRegister(registers.vramAddress().getRegister() + increment);
    }

    public void write(int address, int data) {
        address &= MASK_16BIT;
        data &= MASK_8BIT;

        switch (address) {
            case PPUCTRL:
                writeControl(data);
                break;
            case PPUMASK:
                registers.mask().setRegister(data);
                break;
            case OAMADDR:
                oamaddr = data;
                break;
            case OAMDATA:
                registers.primaryOAM().write(oamaddr, data);
                break;
            case PPUSCROLL:
                writeScroll(data);
                break;
            case PPUADDR:
                writeAddr(data);
                break;
            case PPUDATA:
                writeData(data);
                break;
            default:
                logUnknownRegister(address);
                break;
        }
    }

    public void writePrimaryOAM(int address, int value) {
        registers.primaryOAM().write(address, value);
    }
}
