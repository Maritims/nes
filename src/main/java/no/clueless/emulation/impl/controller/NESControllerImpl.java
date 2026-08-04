package no.clueless.emulation.impl.controller;

import no.clueless.emulation.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NESControllerImpl implements Controller {
    private static final Logger log = LoggerFactory.getLogger(NESControllerImpl.class);

    public enum Button {
        A(0x80),
        B(0x40),
        SELECT(0x20),
        START(0x10),
        UP(0x08),
        DOWN(0x04),
        LEFT(0x02),
        RIGHT(0x01);

        private final int bitmask;

        Button(int bitmask) {
            this.bitmask = bitmask;
        }

        public int getBitmask() {
            return bitmask;
        }
    }

    private volatile boolean strobeState;
    private          int     shiftRegister;
    private volatile int     buttonStates;

    @Override
    public void setStrobeState(boolean newStrobeState) {
        if (strobeState && !newStrobeState) {
            shiftRegister = buttonStates;
        }

        strobeState = newStrobeState;

        if (strobeState) {
            shiftRegister = buttonStates;
        }
    }

    @Override
    public int readDataPort() {
        if (strobeState) {
            return buttonStates & 0x01;
        }

        var bit = shiftRegister & 0x01;
        shiftRegister = (shiftRegister >> 1);
        return bit;
    }

    @Override
    public void setButtonState(Enum<?> button, boolean isPressed) {
        if (!(button instanceof Button)) {
            throw new IllegalArgumentException("Invalid button type for NESControllerImpl");
        }

        log.debug("setButtonState: button={}, isPressed={}", button, isPressed);

        var bitmask = ((Button) button).getBitmask();
        if (isPressed) {
            buttonStates |= bitmask;
        } else {
            buttonStates &= ~bitmask;
        }
    }
}
