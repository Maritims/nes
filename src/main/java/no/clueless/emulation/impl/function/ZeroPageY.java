package no.clueless.emulation.impl.function;

import no.clueless.emulation.AddressingModeFunction;
import no.clueless.emulation.Cpu6502;

public class ZeroPageY extends ZeroPageWithRegister implements AddressingModeFunction<Cpu6502> {
    public ZeroPageY() {
        super(Cpu6502::getY);
    }
}
