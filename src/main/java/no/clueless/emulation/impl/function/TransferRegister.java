package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class TransferRegister implements OpcodeFunction {
    private final Function<Cpu6502, Integer>   sourceRegisterFunction;
    private final BiConsumer<Cpu6502, Integer> destinationRegisterFunction;

    public TransferRegister(Function<Cpu6502, Integer> sourceRegisterFunction, BiConsumer<Cpu6502, Integer> destinationRegisterFunction) {
        this.sourceRegisterFunction      = sourceRegisterFunction;
        this.destinationRegisterFunction = destinationRegisterFunction;
    }

    @Override
    public int execute(Cpu6502 cpu, int address) {
        var sourceValue = sourceRegisterFunction.apply(cpu) & 0xFF;
        destinationRegisterFunction.accept(cpu, sourceValue);

        cpu.setFlag(Cpu6502.Flag.ZERO, sourceValue == 0);
        cpu.setFlag(Cpu6502.Flag.NEGATIVE, (sourceValue & 0x80) != 0);

        return 2;
    }
}
