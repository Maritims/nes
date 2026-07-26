package no.clueless.emulation.util;

/**
 * Represents the result of an operation.
 * @param address        A 16-bit address
 * @param isPageCrossed  Whether the operation crossed a page boundary
 */
public record ResolvedAddress(int address, boolean isPageCrossed) {
}
