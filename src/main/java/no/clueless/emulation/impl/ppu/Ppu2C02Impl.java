package no.clueless.emulation.impl.ppu;

import no.clueless.emulation.Cartridge;
import no.clueless.emulation.Ppu2C02;
import no.clueless.emulation.impl.ppu.event.PixelListener;
import no.clueless.emulation.impl.ppu.register.PpuBus;
import no.clueless.emulation.impl.ppu.register.PpuRegisterHandler;
import no.clueless.emulation.impl.ppu.register.PpuRegisters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class Ppu2C02Impl implements Ppu2C02 {
    private static final Logger log = LoggerFactory.getLogger(Ppu2C02Impl.class);
    private final PpuRegisters registers;
    private final PpuBus             bus;
    private final PpuRegisterHandler registerHandler;
    private final PixelListener      drawPixelListener;
    private final SpriteEvaluator    spriteEvaluator;
    private final PixelCompositor    pixelCompositor;
    private final BackgroundPipeline backgroundPipeline;

    private boolean isFrameComplete;

    private int     scanLine = 0;
    private int     cycle    = 0;
    private boolean oddFrame = false;
    private boolean nmi;

    public Ppu2C02Impl(PixelListener drawPixelListener, PpuRegisters registers, PpuBus ppuBus) {
        this.registers          = registers;
        this.bus                = ppuBus;
        this.registerHandler    = new PpuRegisterHandler(registers, bus::read, bus::write);
        this.drawPixelListener  = drawPixelListener;
        this.spriteEvaluator    = new SpriteEvaluator(registers, bus);
        this.pixelCompositor    = new PixelCompositor(new NESPalette(), registers, bus, registerHandler);
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
        isFrameComplete = frameComplete;
    }

    @Override
    public boolean isVerticalBlank() {
        return registers.status().isVerticalBlank();
    }

    @Override
    public int getFineX() {
        return registerHandler.getFineX();
    }

    @Override
    public int getFineY() {
        return registers.vramAddress().getFineY();
    }

    @Override
    public int getCoarseX() {
        return registers.vramAddress().getCoarseX();
    }

    @Override
    public int getCoarseY() {
        return registers.vramAddress().getCoarseY();
    }

    @Override
    public int getSpriteCount() {
        return spriteEvaluator.getSpriteCount();
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
            spriteEvaluator.updateShifters(cycle);
            spriteEvaluator.evaluate(cycle, scanLine);
        }

        if (scanLine >= 0 && scanLine < 240 && cycle == 1) {
            spriteEvaluator.latchSpriteZeroHitPossible();
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

        var checkCollision = new AtomicBoolean(false);

        if (scanLine >= 0 && scanLine < 240 && cycle >= 1 && cycle <= 256) {
            var foregroundPixelInformation = spriteEvaluator.getFinalPixelAndPalette(cycle);
            var finalPixelColor = pixelCompositor.compose(
                    cycle,
                    backgroundPipeline.getShifterPatternLow(),
                    backgroundPipeline.getShifterPatternHigh(),
                    backgroundPipeline.getShifterAttributeLow(),
                    backgroundPipeline.getShifterAttributeHigh(),
                    foregroundPixelInformation.pixel(),
                    foregroundPixelInformation.palette(),
                    foregroundPixelInformation.priority(),
                    () -> {
                        log.debug("Pixel {} is opaque", cycle);
                        checkCollision.set(true);
                    }
            );
            drawPixelListener.setPixel(cycle - 1, scanLine, finalPixelColor);
        }

        if (checkCollision.get()) {
            spriteEvaluator.detectSpriteZeroCollision(cycle);
        }

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
