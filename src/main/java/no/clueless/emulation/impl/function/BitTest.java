package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.cpu.OpcodeFunction;
import no.clueless.emulation.util.ResolvedAddress;

public class BitTest implements OpcodeFunction {
    @Override
    public int execute(Cpu6502 cpu, ResolvedAddress address) {
        var memoryData  = cpu.read(address.address());
        var accumulator = cpu.getAccumulator();
        var result      = accumulator & memoryData;
        var isZero      = result == 0;
        var bit7        = (memoryData & (1 << 7)) != 0;
        var bit6        = (memoryData & (1 << 6)) != 0;

        cpu.setFlag(Cpu6502.Flag.ZERO, isZero);
        cpu.setFlag(Cpu6502.Flag.NEGATIVE, bit7);
        cpu.setFlag(Cpu6502.Flag.OVERFLOW, bit6);

        return 0;
    }
}
