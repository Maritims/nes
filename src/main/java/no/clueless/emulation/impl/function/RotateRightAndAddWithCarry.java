package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;
import no.clueless.emulation.util.ResolvedAddress;

public class RotateRightAndAddWithCarry implements OpcodeFunction {
    @Override
    public int execute(Cpu6502 cpu, ResolvedAddress address) {
        Rotate.ROR.execute(cpu, address);
        AddWithCarry.ADC.execute(cpu, address);
        return 0;
    }
}
