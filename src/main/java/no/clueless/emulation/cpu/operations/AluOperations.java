package no.clueless.emulation.cpu.operations;

import no.clueless.emulation.cpu.CPU;
import no.clueless.emulation.cpu.Flag;
import no.clueless.emulation.types.UnsignedByte;
import no.clueless.emulation.types.UnsignedWord;

import static no.clueless.emulation.cpu.operations.ALU.*;

/**
 * Arithmetic logic unit (ALU) operations. Handles all math and bitwise comparisons against the accumulator and memory.
 */
public class AluOperations {
    static void compare(CPU cpu, UnsignedByte registerValue, UnsignedByte memoryValue) {
        if (registerValue == null || memoryValue == null) {
            throw new IllegalArgumentException("values cannot be null");
        }
        var hasCarry = registerValue.compareTo(memoryValue) >= 0;
        var tmp      = registerValue.subtract(memoryValue);

        cpu.getStatusRegister().updateFlag(Flag.Carry, hasCarry);
        cpu.getStatusRegister().updateNegativeAndZero(tmp);
    }

    /**
     * Adds the memory data at the given address to the accumulator and updates the status register.
     */
    public static void adc(CPU cpu, UnsignedWord address) {
        executeArithmeticCalculation(cpu, address, false);
    }

    /**
     * Subtracts the memory data at the given address from the accumulator and updates the status register.
     */
    public static void sbc(CPU cpu, UnsignedWord address) {
        executeArithmeticCalculation(cpu, address, true);
    }

    /**
     * ANDs the accumulator with the memory data at the given address and updates the status register.
     */
    public static void and(CPU cpu, UnsignedWord address) {
        var memoryData  = cpu.getBus().read(address);
        var accumulator = cpu.getAccumulator();
        var result      = accumulator.and(memoryData);

        cpu.setAccumulator(result);
        cpu.getStatusRegister().updateNegativeAndZero(result);
    }

    /**
     * ORs the accumulator with the memory data at the given address and updates the status register.
     */
    public static void ora(CPU cpu, UnsignedWord address) {
        var memoryData  = cpu.getBus().read(address);
        var accumulator = cpu.getAccumulator();
        var result      = accumulator.or(memoryData);

        cpu.setAccumulator(result);
        cpu.getStatusRegister().updateNegativeAndZero(result);
    }

    /**
     * XORs the accumulator with the memory data at the given address and updates the status register.
     */
    public static void eor(CPU cpu, UnsignedWord address) {
        var memoryData  = cpu.getBus().read(address);
        var accumulator = cpu.getAccumulator();
        var result      = accumulator.xor(memoryData);

        cpu.setAccumulator(result);
        cpu.getStatusRegister().updateNegativeAndZero(result);
    }

    /**
     * Sets the flags based on the accumulator and the memory data at the given address.
     */
    public static void bit(CPU cpu, UnsignedWord address) {
        var memoryData  = cpu.getBus().read(address);
        var accumulator = cpu.getAccumulator();
        var result      = accumulator.and(memoryData);
        var isZero      = result.equals(UnsignedByte.ZERO);
        var bit7        = memoryData.testBit(7);
        var bit6        = memoryData.testBit(6);

        cpu.getStatusRegister().updateFlag(Flag.Zero, isZero);
        cpu.getStatusRegister().updateFlag(Flag.Negative, bit7);
        cpu.getStatusRegister().updateFlag(Flag.Overflow, bit6);
    }

    /**
     * Compares memory data with a register value and updates the status register.
     *
     * @param address       16-bit address to read from.
     * @param registerValue 8-bit register value to compare with.
     * @throws IllegalArgumentException if address or registerValue is null.
     */
    private static void compare(CPU cpu, UnsignedWord address, UnsignedByte registerValue) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        var memoryData = cpu.getBus().read(address);
        compare(cpu, registerValue, memoryData);
    }

    /**
     * Compares the accumulator with the memory data at the given address and updates the status register.
     */
    public static void cmp(CPU cpu, UnsignedWord address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }

        var accumulator = cpu.getAccumulator();
        compare(cpu, address, accumulator);
    }

    /**
     * Compares the X register with the memory data at the given address and updates the status register.
     */
    public static void cpx(CPU cpu, UnsignedWord address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }

        var x = cpu.getX();
        compare(cpu, address, x);
    }

    /**
     * Compares the Y register with the memory data at the given address and updates the status register.
     */
    public static void cpy(CPU cpu, UnsignedWord address) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }

        var y = cpu.getY();
        compare(cpu, address, y);
    }

    /**
     * UNOFFICIAL: Decrements the memory data at the given address and compares it with the accumulator.
     */
    public static void dcp(CPU cpu, UnsignedWord address) {
        var memoryData = cpu.getBus().read(address);
        var result     = memoryData.decrement();

        cpu.getBus().write(address, result);
        compare(cpu, cpu.getAccumulator(), result);
    }

    /**
     * UNOFFICIAL: Increments the memory data at the given address and performs an arithmetic calculation.
     */
    public static void isb(CPU cpu, UnsignedWord address) {
        var memoryData = cpu.getBus().read(address);
        var result     = memoryData.increment();

        cpu.getBus().write(address, result);
        executeArithmeticCalculation(cpu, address, true);
    }

    /**
     * UNOFFICIAL: Performs a logical shift left on the memory data at the given address and ORs it with the accumulator.
     */
    public static void slo(CPU cpu, UnsignedWord address) {
        var memoryData = cpu.getBus().read(address);
        var result     = shiftLeft(cpu, memoryData);

        cpu.getBus().write(address, result);
        ora(cpu, address);
    }

    /**
     * UNOFFICIAL: Performs a logical shift right on the memory data at the given address and EORs it with the accumulator.
     */
    public static void sre(CPU cpu, UnsignedWord address) {
        var memoryData = cpu.getBus().read(address);
        var result     = shiftRight(cpu, memoryData);

        cpu.getBus().write(address, result);
        eor(cpu, address);
    }
}
