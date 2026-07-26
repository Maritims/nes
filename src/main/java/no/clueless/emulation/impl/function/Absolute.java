package no.clueless.emulation.impl.function;

import no.clueless.emulation.AddressingModeFunction;
import no.clueless.emulation.Bus;
import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.util.ResolvedAddress;

public class Absolute implements AddressingModeFunction<Cpu6502> {
    @Override
    public ResolvedAddress resolve(Cpu6502 cpu, Bus bus) {
        var lowByte  = bus.read(cpu.getAndIncrementProgramCounter());
        var highByte = bus.read(cpu.getAndIncrementProgramCounter());
        var address  = ((highByte << 8) | lowByte) & 0xFFFF;
        return new ResolvedAddress(address, false);
    }
}
