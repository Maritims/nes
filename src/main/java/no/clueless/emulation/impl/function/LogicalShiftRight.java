package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;

public class LogicalShiftRight implements OpcodeFunction {
    public static final LogicalShiftRight LSR = new LogicalShiftRight();

    private LogicalShiftRight() {}

    protected int shiftRight(Cpu6502 cpu, int value) {
        var carryOut = (value & 1) != 0;
        var result   = (value >> 1) & 0xFF;
        cpu.setFlag(Cpu6502.Flag.CARRY, carryOut);
        cpu.setFlag(Cpu6502.Flag.NEGATIVE, (result & 0x80) != 0);
        cpu.setFlag(Cpu6502.Flag.ZERO, result == 0);
        return result;
    }

    @Override
    public int execute(Cpu6502 cpu, int address) {
        if (address == -1) {
            var accumulator = cpu.getAccumulator();
            var result = shiftRight(cpu, accumulator);
            cpu.setAccumulator(result);
            return 2;
        } else {
            var original = cpu.read(address);
            var result = shiftRight(cpu, original);
            cpu.write(address, original);
            cpu.write(address, result);
            return 5;
        }
    }
}
