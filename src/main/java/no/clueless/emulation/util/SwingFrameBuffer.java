package no.clueless.emulation.util;

import no.clueless.emulation.FrameBuffer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class SwingFrameBuffer implements FrameBuffer {
    private final BufferedImage image;
    private final int[]         pixels;
    private final JFrame        frame;

    public SwingFrameBuffer(String title, int scale) {
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        var panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, WIDTH * scale, HEIGHT * scale, null);
            }
        };

        panel.setPreferredSize(new Dimension(WIDTH * scale, HEIGHT * scale));

        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    public void setPixel(int x, int y, int rgb) {
        if (x >= 0 &&  y >= 0 && x < WIDTH && y < HEIGHT) {
            pixels[y * WIDTH + x] = rgb;
        }
    }

    @Override
    public void render() {
        frame.repaint();
    }
}
