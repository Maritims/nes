package no.clueless.emulation.event;

import java.util.EventListener;

@FunctionalInterface
public interface FpsListener extends EventListener {
    void fpsUpdated(FpsEvent event);
}
