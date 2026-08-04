package no.clueless.emulation.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameWindow extends JFrame implements KeyListener {
    private final StatusBar   statusBar;
    private final KeyListener keyListener;

    public GameWindow(GamePanel gamePanel, CpuPanel cpuPanel, PpuPanel ppuPanel, KeyListener keyListener) {
        this.statusBar       = new StatusBar();
        this.keyListener     = keyListener;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("NES");
        setLayout(new BorderLayout());

        var eastWrapper = new JPanel(new BorderLayout());
        eastWrapper.setOpaque(false);
        eastWrapper.add(cpuPanel, BorderLayout.NORTH);
        eastWrapper.add(ppuPanel, BorderLayout.CENTER);

        add(gamePanel, BorderLayout.CENTER);
        add(eastWrapper, BorderLayout.EAST);
        add(statusBar, BorderLayout.SOUTH);

        setVisible(true);
        pack();
        setLocationRelativeTo(null);
        addKeyListener(this);
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

    public void setFps(double fps) {
        statusBar.setFps(fps);
    }

    public void setCpuMhz(double cpuMhz) {
        statusBar.setCpuMhz(cpuMhz);
    }
}
