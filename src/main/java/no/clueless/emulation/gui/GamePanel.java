package no.clueless.emulation.gui;

import no.clueless.emulation.util.RgbToIntConverter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class GamePanel extends JPanel implements FrameBuffer {
    private final BufferedImage image  = new BufferedImage(FrameBuffer.WIDTH, FrameBuffer.HEIGHT, BufferedImage.TYPE_INT_RGB);
    private final int[]         pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    private final int           scale  = 3;

    public GamePanel() {
        setPreferredSize(new Dimension(FrameBuffer.WIDTH * scale, FrameBuffer.HEIGHT * scale));
        setFocusable(true);
        requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, FrameBuffer.WIDTH * scale, FrameBuffer.HEIGHT * scale, null);
    }

    @Override
    public void setPixel(int x, int y, int rgb) {
        if (x >= 0 && y >= 0 && x < FrameBuffer.WIDTH && y < FrameBuffer.HEIGHT) {
            pixels[y * FrameBuffer.WIDTH + x] = rgb;
        }
    }

    @Override
    public void render() {
        repaint();
    }

    @Override
    public int convertRgbToInt(int red, int green, int blue) {
        return RgbToIntConverter.convertRgbToInt(red, green, blue);
    }
}
