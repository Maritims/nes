package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.cpu.OpcodeFunction;
import no.clueless.emulation.util.ResolvedAddress;

import java.util.function.BiFunction;

public class BitwiseOperation implements OpcodeFunction {
    private final BiFunction<Integer, Integer, Integer> operation;

    private BitwiseOperation(BiFunction<Integer, Integer, Integer> operation) {
        this.operation = operation;
    }

    public static final BitwiseOperation AND = new BitwiseOperation((a, b) -> (a & b));
    public static final BitwiseOperation ORA = new BitwiseOperation((a, b) -> (a | b));
    public static final BitwiseOperation EOR = new BitwiseOperation((a, b) -> (a ^ b));

    @Override
    public int execute(Cpu6502 cpu, ResolvedAddress address) {
        var memory      = cpu.read(address.address());
        var accumulator = cpu.getAccumulator();
        var result      = operation.apply(accumulator, memory);

        cpu.setAccumulator(result);
        cpu.setFlag(Cpu6502.Flag.ZERO, result == 0);
        cpu.setFlag(Cpu6502.Flag.NEGATIVE, (result & 0x80) != 0);

        return address.isPageCrossed() ? 1 : 0;
    }
}
