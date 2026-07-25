package no.clueless.emulation;

import no.clueless.emulation.cpu.OperandResult;

@FunctionalInterface
public interface AddressingModeFunction<T> {
    OperandResult resolve(T cpu, Bus bus);

    /**
     * Checks if the page crossed between the two addresses.
     *
     * @param a 16-bit address
     * @param b 16-bit address
     * @return true if the arguments do not share the same high byte, otherwise false.
     */
    default boolean isPageCrossed(int a, int b) {
        return (a & 0xFF00) != (b & 0xFF00);
    }
}
