package no.clueless.emulation.impl.function;

import no.clueless.emulation.AddressingModeFunction;
import no.clueless.emulation.Bus;
import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.util.ResolvedAddress;

import java.util.function.Function;

public class ZeroPageWithRegister implements AddressingModeFunction<Cpu6502> {
    private final Function<Cpu6502, Integer> registerFunction;

    public ZeroPageWithRegister(Function<Cpu6502, Integer> registerFunction) {
        this.registerFunction = registerFunction;
    }

    @Override
    public ResolvedAddress resolve(Cpu6502 cpu, Bus bus) {
        var lowByte = bus.read(cpu.getAndIncrementProgramCounter());

        var register = registerFunction.apply(cpu);
        var address  = (lowByte + register) & 0xFF;

        return new ResolvedAddress(address, false);
    }
}
