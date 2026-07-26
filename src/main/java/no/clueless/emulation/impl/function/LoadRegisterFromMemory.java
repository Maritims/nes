package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;

import java.util.function.BiConsumer;

public class LoadRegisterFromMemory implements OpcodeFunction {
    private final BiConsumer<Cpu6502, Integer> setRegisterFunction;

    public LoadRegisterFromMemory(BiConsumer<Cpu6502, Integer> setRegisterFunction) {
        this.setRegisterFunction = setRegisterFunction;
    }

    @Override
    public int execute(Cpu6502 cpu, int address) {
        var memory = cpu.read(address) & 0xFF;
        cpu.setFlag(Cpu6502.Flag.ZERO, memory == 0);
        cpu.setFlag(Cpu6502.Flag.NEGATIVE, (memory & 0x80) != 0);
        setRegisterFunction.accept(cpu, memory);

        return 2;
    }
}
