package no.clueless.emulation.gui;

import no.clueless.emulation.impl.ppu.NESPalette;

import javax.swing.*;
import java.awt.*;

public class PaletteViewPanel extends JPanel {
    private final NESPalette palette;
    private final int[]      paletteRam;

    private static final int COLOR_BLOCK_SIZE_PX = 24;

    public PaletteViewPanel(NESPalette palette, int[] paletteRam) {
        this.palette    = palette;
        this.paletteRam = paletteRam;

        setPreferredSize(new Dimension(COLOR_BLOCK_SIZE_PX * 16 + 40, COLOR_BLOCK_SIZE_PX * 10 + 60));
        setBackground(Color.DARK_GRAY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        var g2d = (Graphics2D) g;

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Monospaced", Font.BOLD, 12));

        // 1. Draw Master 64-Color Palette (16 columns x 4 rows)
        g2d.drawString("Master NES Palette (0x00 - 0x3F)", 15, 20);
        drawMasterPalette(g2d, 15, 30);

        // 2. Draw Active PPU Palettes (4 Background, 4 Sprite)
        g2d.drawString("Active PPU Palettes ($3F00 - $3F1F)", 15, 175);
        drawActivePpuPalettes(g2d, 15, 195);
    }

    private void drawMasterPalette(Graphics2D g2d, int startX, int startY) {
        for (int i = 0; i < 64; i++) {
            int row = i / 16;
            int col = i % 16;

            int x = startX + (col * COLOR_BLOCK_SIZE_PX);
            int y = startY + (row * COLOR_BLOCK_SIZE_PX);

            // Fill color block
            g2d.setColor(new Color(palette.get(i)));
            g2d.fillRect(x, y, COLOR_BLOCK_SIZE_PX - 2, COLOR_BLOCK_SIZE_PX - 2);

            // Optional: Draw hex index text on top if cell is large enough
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawRect(x, y, COLOR_BLOCK_SIZE_PX - 2, COLOR_BLOCK_SIZE_PX - 2);
        }
    }

    private void drawActivePpuPalettes(Graphics2D g2d, int startX, int startY) {
        // There are 8 palettes total: 0-3 for Background, 4-7 for Sprites. Each has 4 colors.
        for (int palRow = 0; palRow < 8; palRow++) {
            int y = startY + (palRow * (COLOR_BLOCK_SIZE_PX + 4));

            // Label (BG0-BG3, SP0-SP3)
            String label = (palRow < 4) ? "BG " + palRow : "SP " + (palRow - 4);
            g2d.setColor(Color.WHITE);
            g2d.drawString(label, startX, y + 16);

            for (int col = 0; col < 4; col++) {
                int x = startX + 40 + (col * COLOR_BLOCK_SIZE_PX);

                // Fetch the palette index from PPU RAM (offset by palRow * 4)
                int paletteIndexRam = paletteRam[(palRow * 4) + col] & 0x3F;
                int rgbColor        = palette.get(paletteIndexRam);

                g2d.setColor(new Color(rgbColor));
                g2d.fillRect(x, y, COLOR_BLOCK_SIZE_PX - 2, COLOR_BLOCK_SIZE_PX - 2);

                g2d.setColor(Color.GRAY);
                g2d.drawRect(x, y, COLOR_BLOCK_SIZE_PX - 2, COLOR_BLOCK_SIZE_PX - 2);
            }
        }
    }
}
