package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;

public class RotateRightAndAddWithCarry implements OpcodeFunction {
    private final Rotate       ror = Rotate.ROR;
    private final AddWithCarry adc = AddWithCarry.ADC;

    @Override
    public int execute(Cpu6502 cpu, int address) {
        ror.execute(cpu, address);
        return adc.execute(cpu, address);
    }
}
