package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;
import no.clueless.emulation.util.ResolvedAddress;

public class Increment implements OpcodeFunction {
    public static final Increment INC = new Increment();

    private Increment() {
    }

    @Override
    public int execute(Cpu6502 cpu, ResolvedAddress address) {
        var memory = cpu.read(address.address());
        var result = (memory + 1) & 0xFF;

        cpu.setFlag(Cpu6502.Flag.ZERO, result == 0);
        cpu.setFlag(Cpu6502.Flag.NEGATIVE, (result & 0x80) != 0);

        cpu.write(address.address(), memory);
        cpu.write(address.address(), result);

        return 0;
    }
}
