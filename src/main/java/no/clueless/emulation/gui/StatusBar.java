package no.clueless.emulation.gui;

import javax.swing.*;

public class StatusBar extends JPanel {
    private final JLabel fpsLabel    = new JLabel();
    private final JLabel cpuMhzLabel = new JLabel();

    public StatusBar() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        add(Box.createHorizontalStrut(10));
        add(new JLabel("FPS:"));
        add(fpsLabel);
        setFps(0.0);

        add(Box.createHorizontalStrut(10));
        add(new  JLabel("CPU:"));
        add(cpuMhzLabel);
        setCpuMhz(0.0);
    }

    public void setFps(double fps) {
        fpsLabel.setText("%.3f".formatted(fps));
    }

    public void setCpuMhz(double cpuMhz) {
        cpuMhzLabel.setText("%.3f".formatted(cpuMhz));
    }
}
