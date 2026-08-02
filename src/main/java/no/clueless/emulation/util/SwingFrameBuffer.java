package no.clueless.emulation.util;

import no.clueless.emulation.FrameBuffer;
import no.clueless.emulation.impl.controller.NESControllerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class SwingFrameBuffer extends JFrame implements FrameBuffer {
    private static final Logger        log = LoggerFactory.getLogger(SwingFrameBuffer.class);
    private final        BufferedImage image;
    private final        int[]         pixels;
    private final        JLabel        statusLabel;

    public SwingFrameBuffer(String title, int scale, NESControllerImpl controller) {
        image  = new BufferedImage(FrameBuffer.WIDTH, FrameBuffer.HEIGHT, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        setLayout(new BorderLayout());

        var panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, FrameBuffer.WIDTH * scale, FrameBuffer.HEIGHT * scale, null);
            }
        };

        panel.setPreferredSize(new Dimension(FrameBuffer.WIDTH * scale, FrameBuffer.HEIGHT * scale));
        add(panel, BorderLayout.CENTER);

        statusLabel = new JLabel("FPS: 0.0", SwingConstants.LEFT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        statusLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        add(statusLabel, BorderLayout.SOUTH);

        setTitle(title);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                log.info("Pressed {}", e);

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SPACE -> controller.setButtonState(NESControllerImpl.Button.SELECT, true);
                    case KeyEvent.VK_ENTER -> controller.setButtonState(NESControllerImpl.Button.START, true);
                    case KeyEvent.VK_UP -> controller.setButtonState(NESControllerImpl.Button.UP, true);
                    case KeyEvent.VK_DOWN -> controller.setButtonState(NESControllerImpl.Button.DOWN, true);
                    case KeyEvent.VK_LEFT -> controller.setButtonState(NESControllerImpl.Button.LEFT, true);
                    case KeyEvent.VK_RIGHT -> controller.setButtonState(NESControllerImpl.Button.RIGHT, true);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SPACE -> controller.setButtonState(NESControllerImpl.Button.SELECT, false);
                    case KeyEvent.VK_ENTER -> controller.setButtonState(NESControllerImpl.Button.START, false);
                    case KeyEvent.VK_UP -> controller.setButtonState(NESControllerImpl.Button.UP, false);
                    case KeyEvent.VK_DOWN -> controller.setButtonState(NESControllerImpl.Button.DOWN, false);
                    case KeyEvent.VK_LEFT -> controller.setButtonState(NESControllerImpl.Button.LEFT, false);
                    case KeyEvent.VK_RIGHT -> controller.setButtonState(NESControllerImpl.Button.RIGHT, false);
                }
            }
        });
    }

    public void setStatus(double fps, double cpuMhz) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(String.format("FPS: %.2f | CPU %.3f MHz", fps, cpuMhz)));
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
