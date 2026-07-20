package no.clueless.emulation.cpu;

public enum Flag {
    Negative(0x7),
    Overflow(0x6),
    Five(0x5),
    Break(0x4),
    Decimal(0x3),
    InterruptDisable(0x2),
    Zero(0x1),
    Carry(0x0);

    private final int mask;

    Flag(int mask) {
        if (mask < 0 || mask > 7) {
            throw new IllegalArgumentException("Invalid flag mask");
        }
        this.mask = mask;
    }

    public int getMask() {
        return mask;
    }
}
