package no.clueless.emulation.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class GamePanel extends JPanel implements FrameBuffer, KeyListener {
    private final BufferedImage image  = new BufferedImage(FrameBuffer.WIDTH, FrameBuffer.HEIGHT, BufferedImage.TYPE_INT_RGB);
    private final int[]         pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    private final int           scale  = 3;
    private       KeyListener   keyListener;

    public GamePanel() {
        setPreferredSize(new Dimension(FrameBuffer.WIDTH * scale, FrameBuffer.HEIGHT * scale));
        setFocusable(true);
        requestFocusInWindow();
    }

    public void setKeyListener(KeyListener keyListener) {
        this.keyListener = keyListener;
        addKeyListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, FrameBuffer.WIDTH * scale, FrameBuffer.HEIGHT * scale, null);
    }

    @Override
    public void renderUpdated() {
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        keyListener.keyTyped(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keyListener.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keyListener.keyReleased(e);
    }

    @Override
    public void pixelUpdated(int x, int y, int rgb) {
        if (x >= 0 && y >= 0 && x < FrameBuffer.WIDTH && y < FrameBuffer.HEIGHT) {
            pixels[y * FrameBuffer.WIDTH + x] = rgb;
        }
    }
}
