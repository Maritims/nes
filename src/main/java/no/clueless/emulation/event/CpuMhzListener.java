package no.clueless.emulation.event;

import java.util.EventListener;

@FunctionalInterface
public interface CpuMhzListener extends EventListener {
    void cpuMhzUpdated(CpuMhzEvent event);
}
