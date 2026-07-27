package no.clueless.emulation.impl;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.util.ResolvedAddress;

@FunctionalInterface
public interface OpcodeFunction {
    /**
     * Executes the opcodeFunction.
     *
     * @param cpu     6502 CPU.
     * @param address Address to execute opcodeFunction at. A 16-bit address. AND with 0xFFFF to mask.
     * @return The number of cycles the opcodeFunction took to execute.
     */
    int execute(Cpu6502 cpu, ResolvedAddress address);

    default OpcodeFunction andThen(OpcodeFunction after, boolean returnAfter) {
        return (cpu, address) -> {
            execute(cpu, address);
            var afterResult = after.execute(cpu, address);
            return returnAfter ? afterResult : 0;
        };
    }

    default OpcodeFunction andThen(OpcodeFunction after) {
        return andThen(after, false);
    }
}
