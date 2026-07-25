package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;

import java.util.function.BiConsumer;

public class DecrementRegister implements OpcodeFunction {
    private final BiConsumer<Cpu6502, Integer> setRegisterFunction;

    public DecrementRegister(BiConsumer<Cpu6502, Integer> setRegisterFunction) {
        this.setRegisterFunction = setRegisterFunction;
    }

    @Override
    public int execute(Cpu6502 cpu, int address) {
        return 0;
    }
}
