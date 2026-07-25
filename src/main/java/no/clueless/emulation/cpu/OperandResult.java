package no.clueless.emulation.cpu;

/**
 * Represents the result of an operation.
 * @param address        A 16-bit address
 * @param cyclesConsumed The number of cycles consumed by the operation
 * @param isPageCrossed  Whether the operation crossed a page boundary
 */
public record OperandResult(int address, int cyclesConsumed, boolean isPageCrossed) {
}
