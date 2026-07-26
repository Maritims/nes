package no.clueless.emulation.impl;

import no.clueless.emulation.Cpu6502;

@FunctionalInterface
public interface OpcodeFunction {
    /**
     * Executes the opcodeFunction.
     *
     * @param cpu     6502 CPU.
     * @param address Address to execute opcodeFunction at. A 16-bit address. AND with 0xFFFF to mask.
     * @return The number of additionalCyclesFromAddressingMode the opcodeFunction took to execute.
     */
    int execute(Cpu6502 cpu, int address);
}
