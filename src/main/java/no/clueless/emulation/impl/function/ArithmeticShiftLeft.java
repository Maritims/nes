package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;
import no.clueless.emulation.util.ResolvedAddress;

public class ArithmeticShiftLeft implements OpcodeFunction {
    public static final ArithmeticShiftLeft ASL = new ArithmeticShiftLeft();

    private ArithmeticShiftLeft() {
    }

    protected int shiftLeft(Cpu6502 cpu, int value) {
        // Bit 7 is shifted into the carry flag.
        cpu.setFlag(Cpu6502.Flag.CARRY, (value & (1 << 7)) != 0);

        var result = (value << 1) & 0xFF;
        cpu.setFlag(Cpu6502.Flag.NEGATIVE, (result & 0x80) != 0);

        // Value 0 is shifted into the zero flag.
        cpu.setFlag(Cpu6502.Flag.ZERO, result == 0);

        return result;
    }

    @Override
    public int execute(Cpu6502 cpu, ResolvedAddress address) {
        if (address.address() == -1) {
            var result = shiftLeft(cpu, cpu.getAccumulator());
            cpu.setAccumulator(result);
        } else {
            var original = cpu.read(address.address());
            var result   = shiftLeft(cpu, original);
            cpu.write(address.address(), original);
            cpu.write(address.address(), result);
        }

        return 0;
    }
}
