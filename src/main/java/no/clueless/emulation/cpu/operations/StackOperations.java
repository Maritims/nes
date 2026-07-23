package no.clueless.emulation.cpu.operations;

import no.clueless.emulation.cpu.CPU;
import no.clueless.emulation.cpu.Flag;
import no.clueless.emulation.cpu.StatusRegister;
import no.clueless.emulation.types.UnsignedByte;
import no.clueless.emulation.types.UnsignedWord;

public class StackOperations {
    /**
     * Pushes the accumulator onto the stack.
     */
    public static void pha(CPU cpu, UnsignedWord ignored) {
        cpu.push8(cpu.getAccumulator());
    }

    /**
     * Pushes the status register onto the stack with bit 4 (Break) set to 1.
     */
    public static void php(CPU cpu, UnsignedWord ignored) {
        var status = cpu.getStatusRegister()
                .unsignedByteValue()
                .or(new UnsignedByte(Flag.Break.getMask()));

        cpu.push8(status);
    }

    /**
     * Pulls the accumulator from the stack and updates the status register.
     */
    public static void pla(CPU cpu, UnsignedWord ignored) {
        var accumulator = cpu.pull8();
        cpu.setAccumulator(accumulator);
        cpu.getStatusRegister().updateNegativeAndZero(accumulator);
    }

    /**
     * Pulls the status register from the stack and updates the status register.
     */
    public static void plp(CPU cpu, UnsignedWord ignored) {
        var statusRegister = StatusRegister.fromByte(cpu.pull8());
        cpu.getStatusRegister().update(statusRegister);
    }

    /**
     * Transfers the stack pointer to the X register.
     */
    public static void tsx(CPU cpu, UnsignedWord ignored) {
        cpu.transfer(cpu.getStackPointer().getValue(), cpu::setX);
    }

    /**
     * Transfers the X register to the stack pointer.
     */
    public static void txs(CPU cpu, UnsignedWord ignored) {
        cpu.getStackPointer().setValue(cpu.getX());
    }
}
