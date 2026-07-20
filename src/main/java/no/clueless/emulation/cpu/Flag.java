package no.clueless.emulation.cpu;

public enum Flag {
    Negative(0x80),
    Overflow(0x40),
    Five(0x20),
    Break(0x10),
    Decimal(0x08),
    InterruptDisable(0x04),
    Zero(0x02),
    Carry(0x01);

    private final int mask;

    Flag(int mask) {
        this.mask = mask;
    }

    public int getMask() {
        return mask;
    }
}
