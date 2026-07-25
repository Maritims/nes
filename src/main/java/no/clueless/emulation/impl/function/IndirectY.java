package no.clueless.emulation.impl.function;

import no.clueless.emulation.AddressingModeFunction;
import no.clueless.emulation.Bus;
import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.cpu.OperandResult;

public class IndirectY implements AddressingModeFunction<Cpu6502> {
    @Override
    public OperandResult resolve(Cpu6502 cpu, Bus bus) {
        var pointerLowByte = bus.read(cpu.getAndIncrementProgramCounter());
        var pointerHighByte = pointerLowByte + 1;
        var baseLowByte = bus.read(pointerLowByte);
        var baseHighByte = bus.read(pointerHighByte);
        var base = (baseHighByte << 8) | baseLowByte;
        var address = base + cpu.getY();
        var isPageCrossed = isPageCrossed(base, address);

        if (isPageCrossed) {
            cpu.addCycles(1);
        }

        return new OperandResult(address, 5, isPageCrossed);
    }
}
