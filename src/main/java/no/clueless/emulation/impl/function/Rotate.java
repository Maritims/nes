package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.cpu.OpcodeFunction;
import no.clueless.emulation.util.ResolvedAddress;

public class Rotate implements OpcodeFunction {
    private final boolean isLeft;

    private Rotate(boolean isLeft) {
        this.isLeft = isLeft;
    }

    public static final Rotate ROL = new Rotate(true);
    public static final Rotate ROR = new Rotate(false);

    private int rotate(Cpu6502 cpu, int value) {
        var     oldCarry = cpu.hasFlag(Cpu6502.Flag.CARRY);
        boolean newCarry;
        int     result;

        if (isLeft) {
            // ROL: Bit 7 -> Carry, Carry -> Bit 0
            newCarry = (value & 0x80) != 0;
            result   = ((value << 1) | (oldCarry ? 0x01 : 0)) & 0xFF;
        } else {
            // ROR: Bit 0 -> Carry, Carry -> Bit 7
            newCarry = (value & 0x01) != 0;
            result   = ((value >> 1) | (oldCarry ? 0x80 : 0)) & 0xFF;
        }

        cpu.setFlag(Cpu6502.Flag.CARRY, newCarry);
        cpu.setFlag(Cpu6502.Flag.NEGATIVE, (result & 0x80) != 0);
        cpu.setFlag(Cpu6502.Flag.ZERO, result == 0);

        return result;
    }

    @Override
    public int execute(Cpu6502 cpu, ResolvedAddress address) {
        if (address.address() == -1) {
            var accumulator = cpu.getAccumulator();
            var result      = rotate(cpu, accumulator);
            cpu.setAccumulator(result);
        } else {
            var original = cpu.read(address.address());
            var result   = rotate(cpu, original);
            cpu.write(address.address(), original);
            cpu.write(address.address(), result);
        }

        return 0;
    }
}
