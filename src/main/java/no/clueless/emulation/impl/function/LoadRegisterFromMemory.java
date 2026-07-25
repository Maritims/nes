package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;

import java.util.function.BiConsumer;

public class LoadRegisterFromMemory implements OpcodeFunction {
    private final BiConsumer<Cpu6502, Integer> register;

    public LoadRegisterFromMemory(BiConsumer<Cpu6502, Integer> register) {
        this.register = register;
    }

    @Override
    public int execute(Cpu6502 cpu, int address) {
        var memoryData = cpu.read(address);
        cpu.setFlag(Cpu6502.Flag.ZERO, memoryData == 0);
        cpu.setFlag(Cpu6502.Flag.NEGATIVE, (memoryData & 0x80) != 0);
        register.accept(cpu, memoryData);
        return memoryData;
    }
}
