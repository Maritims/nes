package no.clueless.emulation.gui;

import no.clueless.emulation.impl.ppu.NESPalette;
import no.clueless.emulation.impl.ppu.PaletteRAM;
import no.clueless.emulation.impl.ppu.event.RenderListener;

import javax.swing.*;
import java.awt.*;

public class PaletteViewPanel extends JPanel implements RenderListener {
    private final NESPalette palette;
    private final PaletteRAM paletteRAM;

    private static final int COLOR_BLOCK_SIZE = 24;
    private static final int PADDING          = 15;
    private static final int SECTION_GAP      = 25;

    public PaletteViewPanel(NESPalette palette, PaletteRAM paletteRAM) {
        if (palette == null) {
            throw new IllegalArgumentException("palette cannot be null");
        }
        if (paletteRAM == null) {
            throw new IllegalArgumentException("paletteRAM cannot be null");
        }

        this.palette    = palette;
        this.paletteRAM = paletteRAM;

        setPreferredSize(new Dimension(COLOR_BLOCK_SIZE * 16 + 40, COLOR_BLOCK_SIZE * 10 + 60));
    }

    private void updatePreferredSize() {
        // Dynamically compute size based on grid layout metrics instead of hardcoding
        var masterGridWidth    = 16 * COLOR_BLOCK_SIZE;
        var activePaletteWidth = 4 * COLOR_BLOCK_SIZE;
        var labelWidth         = 40; // Space reserved for "BG 0" / "SP 0" labels

        var totalWidth = Math.max(masterGridWidth, labelWidth + activePaletteWidth) + (PADDING * 2);

        // Approximate height calculation:
        // Title 1 + Master Grid (4 rows) + Gap + Title 2 + Active Palettes (8 rows)
        var fontHeight           = 16;
        var masterGridHeight     = 4 * COLOR_BLOCK_SIZE;
        var activePalettesHeight = 8 * (COLOR_BLOCK_SIZE + 4);

        var totalHeight = PADDING + fontHeight + 5 + masterGridHeight
                + SECTION_GAP
                + fontHeight + 5 + activePalettesHeight
                + PADDING;

        setPreferredSize(new Dimension(totalWidth, totalHeight));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        var g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2d.setFont(new Font("Monospaced", Font.BOLD, 12));
        var fontMetrics = g2d.getFontMetrics();
        int fontHeight = fontMetrics.getHeight();

        int currentY = PADDING + fontHeight;

        // 1. Draw Master 64-Color Palette (16 columns x 4 rows)
        g2d.drawString("Master NES Palette (0x00 - 0x3F)", PADDING, currentY);
        currentY += 8; // Small spacing after title

        drawMasterPalette(g2d, PADDING, currentY);
        currentY += (4 * COLOR_BLOCK_SIZE) + SECTION_GAP;

        // 2. Draw Active PPU Palettes (4 Background, 4 Sprite)
        g2d.drawString("Active PPU Palettes ($3F00 - $3F1F)", PADDING, currentY);
        currentY += 8;

        drawActivePpuPalettes(g2d, PADDING, currentY);
    }

    private void drawMasterPalette(Graphics2D g2d, int startX, int startY) {
        for (var i = 0; i < 64; i++) {
            var row = i / 16;
            var col = i % 16;

            var x = startX + (col * COLOR_BLOCK_SIZE);
            var y = startY + (row * COLOR_BLOCK_SIZE);

            drawColorBlock(g2d, x, y, palette.get(i));
        }
    }

    private void drawActivePpuPalettes(Graphics2D g2d, int startX, int startY) {
        var fontMetrics = g2d.getFontMetrics();

        // Find maximum label width dynamically so color blocks line up cleanly
        int maxLabelWidth = fontMetrics.stringWidth("BG 0") + 10;

        for (var row = 0; row < 8; row++) {
            var rowY = startY + (row * (COLOR_BLOCK_SIZE + 4));

            // Label (BG0-BG3, SP0-SP3) centered vertically relative to the block row
            var label = (row < 4) ? "BG " + row : "SP " + (row - 4);
            int textY = rowY + (COLOR_BLOCK_SIZE / 2) + (fontMetrics.getAscent() / 2) - 2;
            g2d.drawString(label, startX, textY);

            for (var col = 0; col < 4; col++) {
                var x = startX + maxLabelWidth + (col * COLOR_BLOCK_SIZE);

                var paletteIndexRam = paletteRAM.read((row * 4) + col) & 0x3F;
                var rgbColor = palette.get(paletteIndexRam);

                drawColorBlock(g2d, x, rowY, rgbColor);
            }
        }
    }

    private void drawColorBlock(Graphics2D g2d, int x, int y, int rgbColor) {
        int size = COLOR_BLOCK_SIZE - 2; // Leave a 2px gap for borders

        g2d.setColor(new Color(rgbColor));
        g2d.fillRect(x, y, size, size);

        g2d.setColor(Color.GRAY);
        g2d.drawRect(x, y, size, size);
    }

    @Override
    public void render() {
        repaint();
    }
}
