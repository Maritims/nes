package no.clueless.emulation.cpu;

/**
 * Represents a flag in the status register of the MOS 6502 CPU.
 */
public enum Flag {
    Negative(0b1000_0000),
    Overflow(0b0100_0000),
    Five(0b0010_0000),
    Break(0b0001_0000),
    Decimal(0b0000_1000),
    InterruptDisable(0b0000_0100),
    Zero(0b0000_0010),
    Carry(0x0000_0001);

    private final int mask;

    Flag(int mask) {
        this.mask = mask;
    }

    public int getMask() {
        return mask;
    }
}
