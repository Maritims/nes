package no.clueless.emulation.event;

import java.util.EventObject;

public class CpuMhzEvent extends EventObject {
    private final double cpuMhz;

    public CpuMhzEvent(Object source,  double cpuMhz) {
        super(source);
        this.cpuMhz = cpuMhz;
    }

    public double getCpuMhz() {
        return cpuMhz;
    }
}
