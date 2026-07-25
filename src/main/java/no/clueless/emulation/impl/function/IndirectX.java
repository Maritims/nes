package no.clueless.emulation.impl.function;

import no.clueless.emulation.AddressingModeFunction;
import no.clueless.emulation.Bus;
import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.cpu.OperandResult;

public class IndirectX implements AddressingModeFunction<Cpu6502> {
    @Override
    public OperandResult resolve(Cpu6502 cpu, Bus bus) {
        var base            = bus.read(cpu.getAndIncrementProgramCounter());
        var pointerLowByte  = base + cpu.getX();
        var pointerHighByte = pointerLowByte + 1;
        var addressLowByte  = bus.read(pointerLowByte);
        var addressHighByte = bus.read(pointerHighByte);
        var address         = (addressHighByte << 8) | addressLowByte;
        return new OperandResult(address, 6, false);
    }
}
