package no.clueless.emulation.impl.function;

import no.clueless.emulation.AddressingModeFunction;
import no.clueless.emulation.Bus;
import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.util.ResolvedAddress;

public class IndirectY implements AddressingModeFunction<Cpu6502> {
    @Override
    public ResolvedAddress resolve(Cpu6502 cpu, Bus bus) {
        var pointerLowByte  = bus.read(cpu.getAndIncrementProgramCounter()) & 0xFF;
        var pointerHighByte = (pointerLowByte + 1) & 0xFF;

        var baseLowByte  = bus.read(pointerLowByte) & 0xFF;
        var baseHighByte = bus.read(pointerHighByte) & 0xFF;
        var base         = (baseHighByte << 8) | baseLowByte;

        var address       = (base + (cpu.getY() & 0xFF)) & 0xFFFF;
        var isPageCrossed = PageBoundaryChecker.hasCrossed(base, address);

        return new ResolvedAddress(address, isPageCrossed);
    }
}
