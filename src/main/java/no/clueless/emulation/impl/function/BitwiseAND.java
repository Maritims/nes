package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;

public class BitwiseAND implements OpcodeFunction {
    public static final BitwiseAND AND = new BitwiseAND();

    private BitwiseAND() {}

    @Override
    public int execute(Cpu6502 cpu, int address) {
        var memory = cpu.read(address);
        var accumulator = cpu.getAccumulator();
        var result = (accumulator & memory) & 0xFF;

        cpu.setAccumulator(result);
        cpu.setFlag(Cpu6502.Flag.ZERO, result == 0);
        cpu.setFlag(Cpu6502.Flag.NEGATIVE, (result & 0x80) != 0);

        return 2;
    }
}
