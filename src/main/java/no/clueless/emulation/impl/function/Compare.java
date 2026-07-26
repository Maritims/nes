package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;

import java.util.function.Function;

public class Compare implements OpcodeFunction {
    private final Function<Cpu6502, Integer> sourceRegisterFunction;

    public Compare(Function<Cpu6502, Integer> sourceRegisterFunction) {
        this.sourceRegisterFunction = sourceRegisterFunction;
    }

    public static Compare CMP = new Compare(Cpu6502::getAccumulator);

    @Override
    public int execute(Cpu6502 cpu, int address) {
        var registerValue = sourceRegisterFunction.apply(cpu);
        var memory        = cpu.read(address);
        var hasCarry      = registerValue >= memory;
        var tmp           = registerValue - memory;

        cpu.setFlag(Cpu6502.Flag.CARRY, hasCarry);
        cpu.setFlag(Cpu6502.Flag.ZERO, tmp == 0);
        cpu.setFlag(Cpu6502.Flag.NEGATIVE, (tmp & 0x80) != 0);

        return 2;
    }
}
