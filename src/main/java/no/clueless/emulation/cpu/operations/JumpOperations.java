package no.clueless.emulation.cpu.operations;

import no.clueless.emulation.cpu.CPU;
import no.clueless.emulation.cpu.Flag;
import no.clueless.emulation.cpu.StatusRegister;
import no.clueless.emulation.types.UnsignedByte;
import no.clueless.emulation.types.UnsignedWord;

public class JumpOperations {
    public static void brk(CPU cpu, UnsignedWord ignored) {
        var programCounter = cpu.getProgramCounter();
        var returnAddress = programCounter.add16(new UnsignedWord(1));
        cpu.push16(returnAddress);

        var statusRegisterAsByte = cpu.getStatusRegister()
                .unsignedByteValue()
                .or(new UnsignedByte(Flag.Break.getMask()))
                .or(new UnsignedByte(Flag.Five.getMask()));

        cpu.push8(statusRegisterAsByte);

        cpu.getStatusRegister().updateFlag(Flag.InterruptDisable, true);

        var lowByte  = cpu.getBus().read(0xFFFE);
        var highByte = cpu.getBus().read(0xFFFF);

        cpu.setProgramCounter(UnsignedWord.fromInts(lowByte, highByte));
    }

    /**
     * Jumps to the target address by setting the program counter to the target address.
     */
    public static void jmp(CPU cpu, UnsignedWord targetAddress) {
        cpu.setProgramCounter(targetAddress);
    }

    /**
     * Pushes the current program counter onto the stack and jumps to the target address by setting the program counter to the target address.
     */
    public static void jsr(CPU cpu, UnsignedWord targetAddress) {
        if (targetAddress == null) {
            throw new IllegalArgumentException("targetAddress cannot be null");
        }

        var addressToPush = cpu.getProgramCounter().subtractWord(UnsignedWord.ONE);
        cpu.push16(addressToPush);
        cpu.setProgramCounter(targetAddress);
    }

    /**
     * Pulls the status register from the stack and updates the status register, and then pulls the program counter from the stack and sets the program counter to that value.
     */
    public static void rti(CPU cpu, UnsignedWord ignored) {
        var status = cpu.pull8();
        cpu.getStatusRegister().update(StatusRegister.fromByte(status));
        cpu.setProgramCounter(cpu.pull16());
    }

    /**
     * Pulls an address from the stack and sets the program counter to the address + 1.
     */
    public static void rts(CPU cpu, UnsignedWord ignored) {
        var poppedAddress = cpu.pull16();
        cpu.setProgramCounter(poppedAddress.increment());
    }
}
