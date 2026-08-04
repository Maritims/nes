package no.clueless.emulation.event;

import java.util.EventObject;

public class FpsEvent extends EventObject {
    private final double fps;

    public FpsEvent(Object source, double fps) {
        super(source);
        this.fps = fps;
    }

    public double getFps() {
        return fps;
    }
}
