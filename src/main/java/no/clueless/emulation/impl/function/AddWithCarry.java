package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;

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
        var accumulator = cpu.getAccumulator();
        var rawMemory   = cpu.read(address);
        var carryIn     = cpu.hasFlag(Cpu6502.Flag.CARRY) ? 1 : 0;

        // For SBC, the memory data is inverted.
        var memory = isSubtraction ? (rawMemory ^ 0xFF) : rawMemory;
        var sum    = accumulator + memory + carryIn;
        var result = sum & 0xFF; // AND with 0xFF to truncate the result to 8 bits.

        // Overflow occurs if the accumulator and the memory data had the same sign, but the accumulator and the sum have different signs.
        var hasOverflow = (~(accumulator ^ memory) & (accumulator ^ sum) & 0x80) != 0;
        var hasZero     = result == 0;
        var hasNegative = (result & 0x80) != 0;
        var hasCarry    = sum > 0xFF;

        if (cpu.isDecimalModeEnabled() && cpu.hasFlag(Cpu6502.Flag.DECIMAL_MODE)) {
            if (isSubtraction) {
                // Decimal Subtraction (SBC)
                var lowNibble  = (accumulator & 0x0F) - (rawMemory & 0x0F) - (1 - carryIn);
                var highNibble = (accumulator >> 4) - (rawMemory >> 4);

                if (lowNibble < 0) {
                    lowNibble -= 6;
                    highNibble--;
                }
                if (highNibble < 0) {
                    highNibble -= 6;
                }

                result = ((highNibble << 4) | (lowNibble & 0x0F)) & 0xFF;
            } else {
                // Decimal Addition (ADC)
                var lowNibble  = (accumulator & 0x0F) + (memory & 0x0F) + carryIn;
                var highNibble = (accumulator >> 4) + (rawMemory >> 4);

                if (lowNibble > 0x9) {
                    lowNibble += 0x6;
                    highNibble++;
                }
                if (highNibble > 0x9) {
                    highNibble += 0x6;
                }

                hasCarry = highNibble > 0xF;
                result = ((highNibble << 4) | (lowNibble & 0x0F)) & 0xFF;
            }
        }

        cpu.setAccumulator(result);
        cpu.setFlag(Cpu6502.Flag.CARRY, hasCarry);
        cpu.setFlag(Cpu6502.Flag.OVERFLOW, hasOverflow);
        cpu.setFlag(Cpu6502.Flag.ZERO, hasZero);
        cpu.setFlag(Cpu6502.Flag.NEGATIVE, hasNegative);

        return 2;
    }
}
