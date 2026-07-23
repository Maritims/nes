package no.clueless.emulation;

import javax.swing.*;
import java.awt.*;

public class PatternTableFrame extends JFrame {
    public PatternTableFrame(byte[] chrRom) {
        setTitle("NES Pattern Table Viewer (CHR-ROM)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        var table0 = PatternTableViewer.renderPatternTable(chrRom, 0x0000);
        var table1 = PatternTableViewer.renderPatternTable(chrRom, 0x1000);

        var panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                var g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

                var scale = 3;
                g2.drawImage(table0, 10, 10, 128 * scale, 128 * scale, null);
                g2.drawImage(table1, 20 + (128 * scale), 10, 128 * scale, 128 * scale, null);
            }
        };

        panel.setPreferredSize(new Dimension(30 + (256 * 3), 20 + (128 * 3)));
        add(panel);
        pack();
        setLocationRelativeTo(null);
    }
}
