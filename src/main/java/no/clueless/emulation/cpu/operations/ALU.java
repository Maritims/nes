package no.clueless.emulation.cpu.operations;

import no.clueless.emulation.cpu.CPU;
import no.clueless.emulation.cpu.Flag;
import no.clueless.emulation.types.UnsignedByte;
import no.clueless.emulation.types.UnsignedWord;

public class ALU {
    public static void executeArithmeticCalculation(CPU cpu, UnsignedWord address, boolean isSubtraction) {
        var accumulator     = cpu.getAccumulator().intValue();
        var memoryData      = cpu.getBus().read(address).intValue();
        var hasCarryAlready = cpu.getStatusRegister().hasFlag(Flag.Carry) ? 1 : 0;

        if (isSubtraction) {
            memoryData = memoryData ^ UnsignedByte.MAX_VALUE.intValue();
        }

        var sum              = accumulator + memoryData + hasCarryAlready;
        var hasCarryAfterSum = sum > UnsignedByte.MAX_VALUE.intValue();

        // Calculate unmasked bitwise relationships.
        //var carryInToBit7  = accumulator ^ memoryData ^ sum;
        //var signDifference = memoryData ^ sum;

        // Overflow occurs if the accumulator and the memory data had the same sign, but the accumulator and the sum have different signs.
        //var hasOverflowAfterSum = ((carryInToBit7 & signDifference) & 0x80) != 0;
        var hasOverflowAfterSum = (~(accumulator ^ memoryData) & (accumulator ^ sum) & 0x80) != 0;
        var result              = new UnsignedByte(sum & 0xFF); // AND with 0xFF to truncate the result to 8 bits.

        cpu.setAccumulator(result);
        cpu.getStatusRegister().updateFlag(Flag.Carry, hasCarryAfterSum);
        cpu.getStatusRegister().updateFlag(Flag.Overflow, hasOverflowAfterSum);
        cpu.getStatusRegister().updateNegativeAndZero(result);
    }

    public static UnsignedByte shiftLeft(CPU cpu, UnsignedByte value) {
        var carryOut = value.testBit(7);
        var result   = new UnsignedByte((value.intValue() << 1) & 0xFF);

        cpu.getStatusRegister().updateFlag(Flag.Carry, carryOut);
        cpu.getStatusRegister().updateNegativeAndZero(result);

        return result;
    }

    public static UnsignedByte shiftRight(CPU cpu, UnsignedByte value) {
        var carryOut = value.testBit(0);
        var result   = new UnsignedByte((value.intValue() >> 1) & 0xFF);

        cpu.getStatusRegister().updateFlag(Flag.Carry, carryOut);
        cpu.getStatusRegister().updateNegativeAndZero(result);

        return result;
    }
}
