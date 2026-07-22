package no.clueless.emulation.cpu.operations;

import no.clueless.emulation.cpu.CPU;
import no.clueless.emulation.cpu.Flag;
import no.clueless.emulation.types.UnsignedWord;

public class BranchOperations {
    private static boolean isPageCrossed(UnsignedWord a, UnsignedWord b) {
        return !a.testHighByte(b);
    }

    /**
     * Performs a branch if the condition is true.
     *
     * @param address   16-bit address to branch to.
     * @param condition true if the branch should be performed, false otherwise.
     * @throws IllegalArgumentException if address is null.
     */
    private static void branch(CPU cpu, UnsignedWord address, boolean condition) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        if (!condition) {
            return;
        }

        cpu.consumeCycles(1);

        if (isPageCrossed(cpu.getProgramCounter(), address)) {
            cpu.consumeCycles(1);
        }

        cpu.setProgramCounter(address);
    }

    public static void bcc(CPU cpu, UnsignedWord address) {
        var hasCarry = cpu.getStatusRegister().hasFlag(Flag.Carry);
        branch(cpu, address, !hasCarry);
    }

    public static void bcs(CPU cpu, UnsignedWord address) {
        var hasCarry = cpu.getStatusRegister().hasFlag(Flag.Carry);
        branch(cpu, address, hasCarry);
    }

    public static void beq(CPU cpu, UnsignedWord address) {
        var hasZero = cpu.getStatusRegister().hasFlag(Flag.Zero);
        branch(cpu, address, hasZero);
    }

    public static void bne(CPU cpu, UnsignedWord address) {
        var hasZero = cpu.getStatusRegister().hasFlag(Flag.Zero);
        branch(cpu, address, !hasZero);
    }

    public static void bmi(CPU cpu, UnsignedWord address) {
        var hasNegative = cpu.getStatusRegister().hasFlag(Flag.Negative);
        branch(cpu, address, hasNegative);
    }

    public static void bpl(CPU cpu, UnsignedWord address) {
        var hasNegative = cpu.getStatusRegister().hasFlag(Flag.Negative);
        branch(cpu, address, !hasNegative);
    }

    public static void bvc(CPU cpu, UnsignedWord address) {
        var hasOverflow = cpu.getStatusRegister().hasFlag(Flag.Overflow);
        branch(cpu, address, !hasOverflow);
    }

    public static void bvs(CPU cpu, UnsignedWord address) {
        var hasOverflow = cpu.getStatusRegister().hasFlag(Flag.Overflow);
        branch(cpu, address, hasOverflow);
    }
}
