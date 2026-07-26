package no.clueless.emulation.impl.function;

import no.clueless.emulation.AddressingModeFunction;
import no.clueless.emulation.Bus;
import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.util.ResolvedAddress;

public class ZeroPage implements AddressingModeFunction<Cpu6502> {
    @Override
    public ResolvedAddress resolve(Cpu6502 cpu, Bus bus) {
        var lowByte = bus.read(cpu.getAndIncrementProgramCounter()) & 0xFF;
        return new ResolvedAddress(lowByte, false);
    }
}
