package no.clueless.emulation.impl.function;

import no.clueless.emulation.AddressingModeFunction;
import no.clueless.emulation.Bus;
import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.cpu.ResolvedAddress;

public class Immediate implements AddressingModeFunction<Cpu6502> {
    public Immediate() {}

    @Override
    public ResolvedAddress resolve(Cpu6502 cpu, Bus bus) {
        var address = cpu.getAndIncrementProgramCounter();
        return new ResolvedAddress(address, 2, false);
    }
}
