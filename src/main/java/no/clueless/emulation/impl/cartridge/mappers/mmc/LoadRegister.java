package no.clueless.emulation.impl.cartridge.mappers.mmc;

import static no.clueless.emulation.impl.Masks.MASK_8BIT;

public class LoadRegister {
    private int register;
    private int writeCount;

    public int getRegister() {
        return register;
    }

    public void setRegister(int register) {
        this.register = register & MASK_8BIT;
    }

    public int getWriteCount() {
        return writeCount;
    }

    public void reset() {
        register   = 0;
        writeCount = 0;
    }

    /**
     * Writes data to the register.
     * @param data The data arrives LSB first, so it's placed at bit 5 in the register.
     */
    public void write(int data) {
        if (writeCount >= 5) {
            throw new IllegalStateException("The write count is %d. Unable to write any more data.".formatted(writeCount));
        }

        register >>= 1;
        register |= (data & 0x01) << 4;
        writeCount++;
    }
}
