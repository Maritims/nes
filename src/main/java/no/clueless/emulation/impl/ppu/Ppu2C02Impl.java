package no.clueless.emulation.impl.ppu;

import no.clueless.emulation.Cartridge;
import no.clueless.emulation.Ppu2C02;
import no.clueless.emulation.impl.ppu.event.PixelListener;
import no.clueless.emulation.impl.ppu.register.PpuBus;
import no.clueless.emulation.impl.ppu.register.PpuRegisterHandler;
import no.clueless.emulation.impl.ppu.register.PpuRegisters;

public class Ppu2C02Impl implements Ppu2C02 {
    private final PpuRegisters       registers;
    private final PpuBus             bus;
    private final PpuRegisterHandler registerHandler;
    private final PixelListener      pixelListener;
    private final SpriteEvaluator    spriteEvaluator;
    private final PixelCompositor    pixelCompositor;
    private final BackgroundPipeline backgroundPipeline;

    private boolean isFrameComplete;

    private int     scanLine = 0;
    private int     cycle    = 0;
    private boolean oddFrame = false;
    private boolean nmi;

    public Ppu2C02Impl(PixelListener pixelListener, PpuRegisters registers, PpuBus ppuBus) {
        this.registers          = registers;
        this.bus                = ppuBus;
        this.registerHandler    = new PpuRegisterHandler(registers, bus::read, bus::write);
        this.pixelListener      = pixelListener;
        this.spriteEvaluator    = new SpriteEvaluator(this, registers, bus, this::getCycle, this::getScanLine);
        this.pixelCompositor    = new PixelCompositor(new NESPalette(), registers, bus, registerHandler, spriteEvaluator);
        this.backgroundPipeline = new BackgroundPipeline(registers, bus::read);
    }

    @Override
    public int getScanLine() {
        return scanLine;
    }

    @Override
    public int getCycle() {
        return cycle;
    }

    @Override
    public boolean isNmi() {
        return nmi;
    }

    @Override
    public void clearNmi() {
        nmi = false;
    }

    @Override
    public boolean isFrameComplete() {
        return isFrameComplete;
    }

    @Override
    public void setFrameComplete(boolean frameComplete) {
        isFrameComplete = false;
    }

    @Override
    public boolean isVerticalBlank() {
        return registers.status().isVerticalBlank();
    }

    public void setScanLine(int scanLine) {
        this.scanLine = scanLine;
    }

    public void setCycle(int cycle) {
        this.cycle = cycle;
    }

    @Override
    public void connectToCartridge(Cartridge cartridge) {
        bus.connectToCartridge(cartridge);
    }

    @Override
    public void clock() {
        spriteEvaluator.onPpuCycle();

        if (scanLine >= -1 && scanLine < 240) {
            if (scanLine == 0 && cycle == 0 && oddFrame && (registers.mask().isRenderBackground() || registers.mask().isRenderSprites())) {
                cycle = 1;
            }

            if (scanLine == -1 && cycle == 1) {
                registers.status().setVerticalBlank(false);
                registers.status().setSpriteZeroHit(false);
                registers.status().setSpriteOverflow(false);
                isFrameComplete = false;

                spriteEvaluator.resetShifters();
            }

            backgroundPipeline.onTick(cycle, scanLine);
        }

        // Vertical blanking lines.
        if (scanLine >= 241 && scanLine <= 260) {
            if (scanLine == 241 && cycle == 1) {
                registers.status().setVerticalBlank(true);
                if (registers.control().getEnableNmi()) {
                    nmi = true;
                }

                isFrameComplete = true;
            }
        }

        var finalPixelColor = pixelCompositor.compose(
                cycle,
                backgroundPipeline.getShifterPatternLow(),
                backgroundPipeline.getShifterPatternHigh(),
                backgroundPipeline.getShifterAttributeLow(),
                backgroundPipeline.getShifterAttributeHigh()
        );
        pixelListener.setPixel(cycle - 1, scanLine, finalPixelColor);

        cycle++;

        if (cycle >= 341) {
            cycle = 0;
            scanLine++;
            if (scanLine >= 261) {
                scanLine = -1;
                oddFrame = !oddFrame;
            }
        }
    }

    @Override
    public void reset() {
        scanLine        = 0;
        cycle           = 0;
        isFrameComplete = false;
        oddFrame        = false;

        backgroundPipeline.resetShifters();
        registerHandler.reset();
    }

    @Override
    public void writePrimaryOAM(int address, int value) {
        registerHandler.writePrimaryOAM(address, value);
    }

    @Override
    public int readRegister(int address) {
        return registerHandler.read(address);
    }

    @Override
    public void writeRegister(int address, int data) {
        registerHandler.write(address, data);
    }

    @Override
    public int readBus(int address) {
        return bus.read(address);
    }
}
