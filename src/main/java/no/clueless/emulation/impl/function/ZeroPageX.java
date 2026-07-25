package no.clueless.emulation.impl.function;

import no.clueless.emulation.AddressingModeFunction;
import no.clueless.emulation.Cpu6502;

public class ZeroPageX extends ZeroPageWithRegister implements AddressingModeFunction<Cpu6502> {
    public ZeroPageX() {
        super(Cpu6502::getX);
    }
}
