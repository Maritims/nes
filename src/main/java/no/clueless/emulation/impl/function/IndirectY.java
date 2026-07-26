package no.clueless.emulation.impl.function;

import no.clueless.emulation.AddressingModeFunction;
import no.clueless.emulation.Bus;
import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.cpu.ResolvedAddress;

public class IndirectY implements AddressingModeFunction<Cpu6502> {
    @Override
    public ResolvedAddress resolve(Cpu6502 cpu, Bus bus) {
        var pointerLowByte  = bus.read(cpu.getAndIncrementProgramCounter());
        var pointerHighByte = (pointerLowByte + 1) & 0xFF;

        var baseLowByte  = bus.read(pointerLowByte);
        var baseHighByte = bus.read(pointerHighByte);
        var base         = (baseHighByte << 8) | baseLowByte;

        var address       = (base + cpu.getY()) & 0xFFFF;
        var isPageCrossed = isPageCrossed(base, address);

        if (isPageCrossed) {
            cpu.addCycles(1);
        }

        return new ResolvedAddress(address, 5, isPageCrossed);
    }
}
