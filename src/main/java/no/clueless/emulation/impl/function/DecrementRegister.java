package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.cpu.OpcodeFunction;
import no.clueless.emulation.util.ResolvedAddress;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class DecrementRegister implements OpcodeFunction {
    private final Function<Cpu6502, Integer>   getRegisterFunction;
    private final BiConsumer<Cpu6502, Integer> setRegisterFunction;

    public DecrementRegister(Function<Cpu6502, Integer> getRegisterFunction, BiConsumer<Cpu6502, Integer> setRegisterFunction) {
        this.getRegisterFunction = getRegisterFunction;
        this.setRegisterFunction = setRegisterFunction;
    }

    @Override
    public int execute(Cpu6502 cpu, ResolvedAddress ignored) {
        var registerValue = getRegisterFunction.apply(cpu);
        registerValue = (registerValue - 1) & 0xFF;
        setRegisterFunction.accept(cpu, registerValue);
        cpu.setFlag(Cpu6502.Flag.ZERO, registerValue == 0);
        cpu.setFlag(Cpu6502.Flag.NEGATIVE, (registerValue & 0x80) != 0);
        return 0;
    }
}
