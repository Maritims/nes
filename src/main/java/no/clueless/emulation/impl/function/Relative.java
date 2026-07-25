package no.clueless.emulation.impl.function;

import no.clueless.emulation.AddressingModeFunction;
import no.clueless.emulation.Bus;
import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.cpu.OperandResult;

public class Relative implements AddressingModeFunction<Cpu6502> {
    @Override
    public OperandResult resolve(Cpu6502 cpu, Bus bus) {
        var offset  = bus.read(cpu.getAndIncrementProgramCounter());
        var address = cpu.getProgramCounter() + offset;
        return new OperandResult(address, 2, false);
    }
}
