package no.clueless.emulation.ppu;

import no.clueless.emulation.types.UnsignedByte;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the PPUCTRL register.
 */
public class Controller {
    private UnsignedByte value;

    private boolean    nmiEnabled;
    private boolean    outputColorOnEXT;
    private boolean spriteSize;

    public boolean isNmiEnabled() {
        return value.testBit(7);
    }
}
