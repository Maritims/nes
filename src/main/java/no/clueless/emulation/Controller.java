package no.clueless.emulation;

public interface Controller {
    void setStrobeState(boolean value);

    int readDataPort();

    void setButtonState(Enum<?> button, boolean isPressed);
}
