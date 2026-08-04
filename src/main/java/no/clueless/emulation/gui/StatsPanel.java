package no.clueless.emulation.gui;

import javax.swing.*;
import java.awt.*;

public abstract class StatsPanel extends JPanel {

    protected StatsPanel(String title) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(8, 8, 8, 8),
                BorderFactory.createTitledBorder(title)
        ));
    }

    protected Font getStatsFont() {
        return new Font(Font.MONOSPACED, Font.PLAIN, 12);
    }

    protected final JPanel createRow(String title, JLabel valueLabel, Font font) {
        var row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));

        var titleLabel = new JLabel(title);
        titleLabel.setFont(font);

        valueLabel.setFont(font);
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        // Explicitly set layout regions so they don't overlap
        row.add(titleLabel, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);

        return row;
    }

    protected final void addRow(JPanel row) {
        add(row);
        add(Box.createVerticalStrut(4));
    }
}
