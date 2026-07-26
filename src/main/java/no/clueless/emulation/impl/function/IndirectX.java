package no.clueless.emulation.impl.function;

import no.clueless.emulation.AddressingModeFunction;
import no.clueless.emulation.Bus;
import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.cpu.ResolvedAddress;

public class IndirectX implements AddressingModeFunction<Cpu6502> {
    @Override
    public ResolvedAddress resolve(Cpu6502 cpu, Bus bus) {
        var base            = bus.read(cpu.getAndIncrementProgramCounter());
        var pointerLowByte  = (base + cpu.getX()) & 0xFF;
        var pointerHighByte = (pointerLowByte + 1) & 0xFF;
        var addressLowByte  = bus.read(pointerLowByte);
        var addressHighByte = bus.read(pointerHighByte);
        var address         = (addressHighByte << 8) | addressLowByte;
        return new ResolvedAddress(address, 6, false);
    }
}
