package no.clueless.emulation.util;

import no.clueless.emulation.FrameBuffer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class SwingFrameBuffer extends JFrame implements FrameBuffer {
    private final BufferedImage image;
    private final int[]         pixels;

    public SwingFrameBuffer(String title, int scale) {
        image  = new BufferedImage(FrameBuffer.WIDTH, FrameBuffer.HEIGHT, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        var panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, FrameBuffer.WIDTH * scale, FrameBuffer.HEIGHT * scale, null);
            }
        };

        panel.setPreferredSize(new Dimension(FrameBuffer.WIDTH * scale, FrameBuffer.HEIGHT * scale));

        setTitle(title);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        add(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
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

    public int convertRgbToInt(int red, int green, int blue) {
        return ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | (blue & 0xFF);
    }
}
