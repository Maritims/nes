package no.clueless.emulation.cpu;

public enum Flag {
    Negative(0x7),
    Overflow(0x6),
    One(0x5),
    Break(0x4),
    Decimal(0x3),
    InterruptDisable(0x2),
    Zero(0x1),
    Carry(0x0);

    private final int value;

    Flag(int value) {
        if (value < 0 || value > 7) {
            throw new IllegalArgumentException("Invalid flag value");
        }
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
