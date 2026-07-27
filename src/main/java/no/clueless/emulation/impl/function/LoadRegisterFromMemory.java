package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;
import no.clueless.emulation.util.ResolvedAddress;

import java.util.function.BiConsumer;

public class LoadRegisterFromMemory implements OpcodeFunction {
    private final BiConsumer<Cpu6502, Integer> setRegisterFunction;

    public static LoadRegisterFromMemory LDA = new LoadRegisterFromMemory(Cpu6502::setAccumulator);
    public static LoadRegisterFromMemory LDX = new LoadRegisterFromMemory(Cpu6502::setX);
    public static LoadRegisterFromMemory LDY = new LoadRegisterFromMemory(Cpu6502::setY);

    public LoadRegisterFromMemory(BiConsumer<Cpu6502, Integer> setRegisterFunction) {
        this.setRegisterFunction = setRegisterFunction;
    }

    @Override
    public int execute(Cpu6502 cpu, ResolvedAddress address) {
        var memory = cpu.read(address.address()) & 0xFF;
        cpu.setFlag(Cpu6502.Flag.ZERO, memory == 0);
        cpu.setFlag(Cpu6502.Flag.NEGATIVE, (memory & 0x80) != 0);
        setRegisterFunction.accept(cpu, memory);

        return address.isPageCrossed() ? 1 : 0;
    }
}
