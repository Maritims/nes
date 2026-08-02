package no.clueless.emulation.impl.controller;

import no.clueless.emulation.Controller;

public class NESControllerImpl implements Controller {
    public enum Button {
        A(0x01), B(0x02), SELECT(0x4), START(0x8), UP(0x10), DOWN(0x20), LEFT(0x40), RIGHT(0x80);

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
            shiftRegister = buttonStates;
        }

        var bit = shiftRegister & 0x01;
        shiftRegister = (shiftRegister >> 1) | 0x80;
        return bit;
    }

    @Override
    public void setButtonState(Enum<?> button, boolean isPressed) {
        if (!(button instanceof Button)) {
            throw new IllegalArgumentException("Invalid button type for NESControllerImpl");
        }

        if (isPressed) {
            buttonStates |= ((Button) button).getBitmask();
        } else {
            buttonStates &= ~((Button) button).getBitmask();
        }
    }
}
