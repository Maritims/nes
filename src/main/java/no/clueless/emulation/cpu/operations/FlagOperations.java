package no.clueless.emulation.cpu.operations;

import no.clueless.emulation.cpu.CPU;
import no.clueless.emulation.cpu.Flag;
import no.clueless.emulation.types.UnsignedWord;

public class FlagOperations {
    /**
     * Clears the carry flag.
     */
    public static void clc(CPU cpu, UnsignedWord ignored) {
        cpu.getStatusRegister().clearFlag(Flag.Carry);
    }

    /**
     * Clears the decimal flag.
     */
    public static void cld(CPU cpu, UnsignedWord ignored) {
        cpu.getStatusRegister().clearFlag(Flag.Decimal);
    }

    /**
     * Clears the interrupt disable flag.
     */
    public static void cli(CPU cpu, UnsignedWord ignored) {
        cpu.getStatusRegister().clearFlag(Flag.InterruptDisable);
    }

    /**
     * Clears the overflow flag.
     */
    public static void clv(CPU cpu, UnsignedWord ignored) {
        cpu.getStatusRegister().clearFlag(Flag.Overflow);
    }

    /**
     * Sets the carry flag.
     */
    public static void sec(CPU cpu, UnsignedWord ignored) {
        cpu.getStatusRegister().setFlag(Flag.Carry);
    }

    /**
     * Sets the decimal flag.
     */
    public static void sed(CPU cpu, UnsignedWord ignored) {
        cpu.getStatusRegister().setFlag(Flag.Decimal);
    }

    /**
     * Sets the interrupt disable flag.
     */
    public static void sei(CPU cpu, UnsignedWord ignored) {
        cpu.getStatusRegister().setFlag(Flag.InterruptDisable);
    }
}
