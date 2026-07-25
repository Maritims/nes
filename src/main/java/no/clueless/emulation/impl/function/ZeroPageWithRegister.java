package no.clueless.emulation.impl.function;

import no.clueless.emulation.AddressingModeFunction;
import no.clueless.emulation.Bus;
import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.cpu.OperandResult;

import java.util.function.Function;

public class ZeroPageWithRegister implements AddressingModeFunction<Cpu6502> {
    private final Function<Cpu6502, Integer> registerFunction;

    public ZeroPageWithRegister(Function<Cpu6502, Integer> registerFunction) {
        this.registerFunction = registerFunction;
    }

    @Override
    public OperandResult resolve(Cpu6502 cpu, Bus bus) {
        var base     = bus.read(cpu.getAndIncrementProgramCounter());
        var register = registerFunction.apply(cpu);
        var address  = base + register;
        return new OperandResult(address, 4, false);
    }
}
