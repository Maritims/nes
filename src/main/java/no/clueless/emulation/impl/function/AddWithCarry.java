package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.cpu.Flag;
import no.clueless.emulation.impl.OpcodeFunction;
import no.clueless.emulation.types.UnsignedByte;

public class AddWithCarry implements OpcodeFunction {
    @Override
    public int execute(Cpu6502 cpu, int address) {
        var accumulator     = cpu.getAccumulator();
        var memory      = cpu.read(address);
        var hasCarryAlready = cpu.hasFlag(Cpu6502.Flag.CARRY) ? 1 : 0;

        /*if (isSubtraction) {
            memory = memory ^ UnsignedByte.MAX_VALUE.intValue();
        }*/

        var sum              = accumulator + memory + hasCarryAlready;
        var hasCarryAfterSum = sum > UnsignedByte.MAX_VALUE.intValue();

        // Calculate unmasked bitwise relationships.
        //var carryInToBit7  = accumulator ^ memoryData ^ sum;
        //var signDifference = memoryData ^ sum;

        // Overflow occurs if the accumulator and the memory data had the same sign, but the accumulator and the sum have different signs.
        //var hasOverflowAfterSum = ((carryInToBit7 & signDifference) & 0x80) != 0;
        var hasOverflowAfterSum = (~(accumulator ^ memory) & (accumulator ^ sum) & 0x80) != 0;
        var result              = sum & 0xFF; // AND with 0xFF to truncate the result to 8 bits.

        cpu.setAccumulator(result);
        cpu.setFlag(Cpu6502.Flag.CARRY, hasCarryAfterSum);
        cpu.setFlag(Cpu6502.Flag.OVERFLOW, hasOverflowAfterSum);
        cpu.setFlag(Cpu6502.Flag.ZERO, result == 0);
        cpu.setFlag(Cpu6502.Flag.NEGATIVE, (result & 0x80) != 0);

        return 2;
    }
}
