package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;

import java.util.function.Function;

public class StoreRegisterInMemory implements OpcodeFunction {
    private final Function<Cpu6502, Integer> register;

    public StoreRegisterInMemory(Function<Cpu6502, Integer> register) {
        this.register = register;
    }

    @Override
    public int execute(Cpu6502 cpu, int address) {
        var registerValue = register.apply(cpu);
        cpu.write(address, registerValue);
        return registerValue;
    }
}
