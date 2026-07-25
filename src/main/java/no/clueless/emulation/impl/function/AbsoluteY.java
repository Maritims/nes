package no.clueless.emulation.impl.function;

import no.clueless.emulation.AddressingModeFunction;
import no.clueless.emulation.Cpu6502;

public class AbsoluteY extends AbsoluteWithRegister implements AddressingModeFunction<Cpu6502> {
    public AbsoluteY() {
        super(Cpu6502::getY);
    }
}
