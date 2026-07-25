package no.clueless.emulation.impl.function;

import no.clueless.emulation.AddressingModeFunction;
import no.clueless.emulation.Cpu6502;

public class AbsoluteX extends AbsoluteWithRegister implements AddressingModeFunction<Cpu6502> {
    protected AbsoluteX() {
        super(Cpu6502::getX);
    }
}
