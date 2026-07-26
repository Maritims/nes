package no.clueless.emulation.impl.function;

import no.clueless.emulation.AddressingModeFunction;
import no.clueless.emulation.Bus;
import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.cpu.OperandResult;

public class Implied implements AddressingModeFunction<Cpu6502> {
    @Override
    public OperandResult resolve(Cpu6502 cpu, Bus bus) {
        return new OperandResult(-1, 0, false);
    }
}
