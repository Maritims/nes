package no.clueless.emulation.impl.function;

/**
 * Represents a utility class for checking page boundaries.
 */
public class PageBoundaryChecker {
    /**
     * Checks whether two 16-bit addresses have different high bytes.
     *
     * @param a A 16-bit address.
     * @param b A 16-bit address.
     * @return True if the addresses have different high bytes, otherwise false.
     */
    public static boolean hasCrossed(int a, int b) {
        return (a & 0xFF00) != (b & 0xFF00);
    }
}
