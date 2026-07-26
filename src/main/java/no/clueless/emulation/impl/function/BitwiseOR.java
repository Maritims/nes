package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.Cpu6502.Flag;
import no.clueless.emulation.impl.OpcodeFunction;

public class BitwiseOR implements OpcodeFunction {
    public static final BitwiseOR ORA = new BitwiseOR();

    private BitwiseOR() {}

    @Override
    public int execute(Cpu6502 cpu, int address) {
        var memory      = cpu.read(address);
        var accumulator = cpu.getAccumulator();
        var result      = accumulator | memory;

        cpu.setAccumulator(result);
        cpu.setFlag(Flag.ZERO, result == 0);
        cpu.setFlag(Flag.NEGATIVE, (result & 0x80) != 0);

        return 2;
    }
}
