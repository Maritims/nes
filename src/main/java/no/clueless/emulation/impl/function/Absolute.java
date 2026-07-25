package no.clueless.emulation.impl.function;

import no.clueless.emulation.AddressingModeFunction;
import no.clueless.emulation.Bus;
import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.cpu.OperandResult;

public class Absolute implements AddressingModeFunction<Cpu6502> {
    @Override
    public OperandResult resolve(Cpu6502 cpu, Bus bus) {
        var lowByte  = bus.read(cpu.getAndIncrementProgramCounter());
        var highByte = bus.read(cpu.getAndIncrementProgramCounter());
        var address  = (highByte << 8) | lowByte;
        return new OperandResult(address, 4, false);
    }
}
