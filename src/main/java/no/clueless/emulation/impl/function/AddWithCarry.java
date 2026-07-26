package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;
import no.clueless.emulation.types.UnsignedByte;

public class AddWithCarry implements OpcodeFunction {
    private final boolean isSubtraction;

    public static final AddWithCarry ADC = new AddWithCarry();

    protected AddWithCarry(boolean isSubtraction) {
        this.isSubtraction = isSubtraction;
    }

    private AddWithCarry() {
        this(false);
    }

    @Override
    public int execute(Cpu6502 cpu, int address) {
        var accumulator     = cpu.getAccumulator();
        var memory          = cpu.read(address);
        var hasCarryAlready = cpu.hasFlag(Cpu6502.Flag.CARRY) ? 1 : 0;

        if (isSubtraction) {
            memory = memory ^ UnsignedByte.MAX_VALUE.intValue();
        }

        var sum              = accumulator + memory + hasCarryAlready;
        var hasCarryAfterSum = sum > UnsignedByte.MAX_VALUE.intValue();

        // Overflow occurs if the accumulator and the memory data had the same sign, but the accumulator and the sum have different signs.
        var hasOverflowAfterSum = (~(accumulator ^ memory) & (accumulator ^ sum) & 0x80) != 0;
        var result = sum & 0xFF; // AND with 0xFF to truncate the result to 8 bits.

        cpu.setAccumulator(result);
        cpu.setFlag(Cpu6502.Flag.CARRY, hasCarryAfterSum);
        cpu.setFlag(Cpu6502.Flag.OVERFLOW, hasOverflowAfterSum);
        cpu.setFlag(Cpu6502.Flag.ZERO, result == 0);
        cpu.setFlag(Cpu6502.Flag.NEGATIVE, (result & 0x80) != 0);

        return 2;
    }
}
