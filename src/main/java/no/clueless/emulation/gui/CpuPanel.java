package no.clueless.emulation.gui;

import no.clueless.emulation.Cpu6502;

import javax.swing.*;
import java.awt.*;

public class CpuPanel extends StatsPanel {
    private final JLabel pcLabel          = new JLabel("0x0000");
    private final JLabel accumulatorLabel = new JLabel("0x00");
    private final JLabel xLabel           = new JLabel("0x00");
    private final JLabel yLabel           = new JLabel("0x00");
    private final JLabel spLabel          = new JLabel("0x00");
    private final JLabel pLabel           = new JLabel("[--]");

    private final Cpu6502 cpu;

    public CpuPanel(Cpu6502 cpu) {
        super("CPU status");

        this.cpu = cpu;

        addRow(createRow("PC:", pcLabel, getStatsFont()));
        addRow(createRow("A:", accumulatorLabel, getStatsFont()));
        addRow(createRow("X:", xLabel, getStatsFont()));
        addRow(createRow("Y:", yLabel, getStatsFont()));
        addRow(createRow("SP:", spLabel, getStatsFont()));
        addRow(createRow("P:", pLabel, getStatsFont()));

        setPreferredSize(new Dimension(360, getPreferredSize().height));
    }

    public void updateStatus() {
        pcLabel.setText("0x%04X".formatted(cpu.getProgramCounter()));
        accumulatorLabel.setText("0x%02X".formatted(cpu.getAccumulator()));
        xLabel.setText("0x%02X".formatted(cpu.getX()));
        yLabel.setText("0x%02X".formatted(cpu.getY()));
        spLabel.setText("0x%02X".formatted(cpu.getStackPointer()));

        var p = cpu.getStatusRegister() & 0xFF;
        var flags = String.format("%s%s%s%s%s%s%s%s",
                ((p & 0x80) != 0 ? "N" : "-"),
                ((p & 0x40) != 0 ? "V" : "-"),
                "-",
                ((p & 0x10) != 0 ? "B" : "-"),
                ((p & 0x08) != 0 ? "D" : "-"),
                ((p & 0x04) != 0 ? "I" : "-"),
                ((p & 0x02) != 0 ? "Z" : "-"),
                ((p & 0x01) != 0 ? "C" : "-")
        );
        pLabel.setText("[%s]".formatted(flags));
    }
}