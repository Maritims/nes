package no.clueless.emulation.gui;

import no.clueless.emulation.util.RgbToIntConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class GamePanel extends JPanel implements FrameBuffer, KeyListener {
    private static final Logger        log    = LoggerFactory.getLogger(GamePanel.class);
    private final        BufferedImage image  = new BufferedImage(FrameBuffer.WIDTH, FrameBuffer.HEIGHT, BufferedImage.TYPE_INT_RGB);
    private final        int[]         pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    private final        int           scale  = 3;
    private        KeyListener   keyListener;

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
}
