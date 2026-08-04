package no.clueless.emulation.gui;

import no.clueless.emulation.Ppu2C02;

import javax.swing.*;
import java.awt.*;

public class PpuPanel extends StatsPanel {
    private final JLabel scanLineLabel = new JLabel("-1");
    private final JLabel cycleLabel    = new JLabel("0");
    private final JLabel nmiLabel      = new JLabel("false");
    private final JLabel verticalBlank = new JLabel("false");

    private final Ppu2C02 ppu;

    public PpuPanel(Ppu2C02 ppu) {
        super("PPU Status");

        this.ppu = ppu;

        addRow(createRow("Scan line", scanLineLabel, getStatsFont()));
        addRow(createRow("Cycle", cycleLabel, getStatsFont()));
        addRow(createRow("NMI", nmiLabel, getStatsFont()));
        addRow(createRow("Vertical", verticalBlank, getStatsFont()));

        setPreferredSize(new Dimension(360, getPreferredSize().height));
    }

    public void updateStatus() {
        scanLineLabel.setText("%d".formatted(ppu.getScanLine()));
        cycleLabel.setText("%d".formatted(ppu.getCycle()));
        nmiLabel.setText("%s".formatted(ppu.isNmi()));
        verticalBlank.setText("%s".formatted(ppu.isVerticalBlank()));
    }
}
