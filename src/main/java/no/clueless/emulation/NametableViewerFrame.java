package no.clueless.emulation;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class NametableViewerFrame extends JFrame {

    private static final int NES_WIDTH = 256;
    private static final int NES_HEIGHT = 240;
    private static final int SCALE = 3; // 3x scaling (768x720 window size)

    private final NametablePanel canvasPanel;

    public NametableViewerFrame() {
        setTitle("NES Nametable Viewer ($2000)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        this.canvasPanel = new NametablePanel();
        this.canvasPanel.setPreferredSize(new Dimension(NES_WIDTH * SCALE, NES_HEIGHT * SCALE));

        add(canvasPanel);
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Call this whenever VRAM or CHR-ROM updates to refresh the window!
     */
    public void updateBuffer(BufferedImage nametableImage) {
        canvasPanel.setImage(nametableImage);
    }

    // Custom panel handling integer scaling
    private static class NametablePanel extends JPanel {
        private BufferedImage image;

        public NametablePanel() {
            setBackground(Color.BLACK);
        }

        public void setImage(BufferedImage image) {
            this.image = image;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                Graphics2D g2d = (Graphics2D) g;
                // Nearest-neighbor rendering prevents blurriness on pixel art
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                g2d.drawImage(image, 0, 0, getWidth(), getHeight(), null);
            }
        }
    }
}