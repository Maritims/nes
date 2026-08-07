package no.clueless.emulation.impl.ppu;

import no.clueless.emulation.impl.ppu.register.PpuBus;
import no.clueless.emulation.impl.ppu.register.PpuRegisterHandler;
import no.clueless.emulation.impl.ppu.register.PpuRegisters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static no.clueless.emulation.impl.PpuMemoryMap.PALETTE_RAM_START;

public class PixelCompositor {
    private static final Logger log = LoggerFactory.getLogger(PixelCompositor.class);
    private final NESPalette palette;
    private final PpuRegisters       registers;
    private final PpuBus             bus;
    private final PpuRegisterHandler registerHandler;

    public PixelCompositor(NESPalette palette, PpuRegisters registers, PpuBus bus, PpuRegisterHandler registerHandler) {
        this.palette         = palette;
        this.registers       = registers;
        this.bus             = bus;
        this.registerHandler = registerHandler;
    }

    public int compose(
            int cycle,
            int backgroundShifterPatternLow, int backgroundShifterPatternHigh,
            int backgroundShifterAttributeLow, int backgroundShifterAttributeHigh,
            int foregroundPixel, int foregroundPalette, int foregroundPriority,
            Runnable onOpaquePixel
    ) {
        var backgroundPixel   = 0x00;
        var backgroundPalette = 0x00;

        if (registers.mask().isRenderBackground()) {
            if (registers.mask().isRenderBackgroundLeft() || cycle >= 9) {
                var bitMux = 0x8000 >> registerHandler.getFineX();

                var p0Pixel = (backgroundShifterPatternLow & bitMux) > 0 ? 1 : 0;
                var p1Pixel = (backgroundShifterPatternHigh & bitMux) > 0 ? 1 : 0;

                backgroundPixel = (p1Pixel << 1) | p0Pixel;

                var backgroundPalette0 = (backgroundShifterAttributeLow & bitMux) > 0 ? 1 : 0;
                var backgroundPalette1 = (backgroundShifterAttributeHigh & bitMux) > 0 ? 1 : 0;

                backgroundPalette = (backgroundPalette1 << 1) | backgroundPalette0;
            }
        }

        if (backgroundPixel > 0 || foregroundPixel > 0) {
            //log.debug("Cycle: {}, bg pixel: {}, fg pixel: {}", cycle, backgroundPixel, foregroundPixel);
        }

        var pixel   = 0x00;
        var palette = 0x00;

        if (backgroundPixel == 0 && foregroundPixel == 0) {
            // Both pixels are transparent, no one wins.
        } else if (backgroundPixel == 0 && foregroundPixel > 0) {
            // The background pixel is transparent, but the foreground pixel is visible.
            // The foreground pixel wins!
            pixel   = foregroundPixel;
            palette = foregroundPalette;
        } else if (backgroundPixel > 0 && foregroundPixel == 0) {
            // The background pixel is visible, but the foreground pixel is transparent.
            // The background pixel wins!
            pixel   = backgroundPixel;
            palette = backgroundPalette;
        } else if (backgroundPixel > 0 && foregroundPixel > 0) {
            if (foregroundPriority > 0) {
                // The foreground pixel is more important.
                pixel   = foregroundPixel;
                palette = foregroundPalette;
            } else {
                // The background pixel is more important.
                pixel   = backgroundPixel;
                palette = backgroundPalette;
            }

            onOpaquePixel.run();
        }

        //noinspection UnnecessaryLocalVariable
        var rgb = this.palette.get(bus.read(PALETTE_RAM_START + (palette << 2) + pixel) & 0x3F);
        return rgb;
    }
}
