package no.clueless.emulation.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameWindow extends JFrame {
    private final StatusBar   statusBar;

    public GameWindow(GamePanel gamePanel, CpuPanel cpuPanel, PpuPanel ppuPanel) {
        this.statusBar       = new StatusBar();

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
    }

    public void setFps(double fps) {
        statusBar.setFps(fps);
    }

    public void setCpuMhz(double cpuMhz) {
        statusBar.setCpuMhz(cpuMhz);
    }
}
