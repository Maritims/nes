package no.clueless.emulation.impl.function;

import no.clueless.emulation.AddressingModeFunction;
import no.clueless.emulation.Bus;
import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.util.ResolvedAddress;

import java.util.function.Function;

public class AbsoluteWithRegister implements AddressingModeFunction<Cpu6502> {
    private final Function<Cpu6502, Integer> registerFunction;

    public AbsoluteWithRegister(Function<Cpu6502, Integer> registerFunction) {
        this.registerFunction = registerFunction;
    }

    @Override
    public ResolvedAddress resolve(Cpu6502 cpu, Bus bus) {
        var lowByte  = bus.read(cpu.getAndIncrementProgramCounter());
        var highByte = bus.read(cpu.getAndIncrementProgramCounter());

        var base          = (highByte << 8) | lowByte;
        var register      = registerFunction.apply(cpu);
        var address       = base + register;
        var isPageCrossed = PageBoundaryChecker.hasCrossed(base, address);

        return new ResolvedAddress(address, isPageCrossed);
    }
}
