package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;

public class AddWithCarry implements OpcodeFunction {
    private AddWithCarry() {}

    public static final AddWithCarry INSTANCE = new AddWithCarry();

    @Override
    public int execute(Cpu6502 cpu, int address) {
        return 0;
    }
}
