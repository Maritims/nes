package no.clueless.emulation.cpu.operations;

import no.clueless.emulation.cpu.CPU;
import no.clueless.emulation.types.UnsignedWord;

public class ShiftOperations {
    public static void asl(CPU cpu, UnsignedWord address) {
        if (address == null) {
            var accumulator = cpu.getAccumulator();
            var result      = cpu.shiftLeft(accumulator);

            cpu.setAccumulator(result);
        } else {
            var original = cpu.getBus().read(address);
            var result   = cpu.shiftLeft(original);

            cpu.getBus().write(address, original);
            cpu.getBus().write(address, result);
        }
    }

    public static void lsr(CPU cpu, UnsignedWord address) {
        if (address == null) {
            var accumulator = cpu.getAccumulator();
            var result      = cpu.shiftRight(accumulator);

            cpu.setAccumulator(result);
        } else {
            var original = cpu.getBus().read(address);
            var shifted  = cpu.shiftRight(original);

            cpu.getBus().write(address, original);
            cpu.getBus().write(address, shifted);
        }
    }

    public static void rol(CPU cpu, UnsignedWord address) {
        if (address == null) {
            var accumulator = cpu.getAccumulator();
            var result      = cpu.rotateLeft(accumulator);

            cpu.setAccumulator(result);
        } else {
            var original = cpu.getBus().read(address);
            var rotated  = cpu.rotateLeft(original);

            cpu.getBus().write(address, original);
            cpu.getBus().write(address, rotated);
        }
    }

    public static void ror(CPU cpu, UnsignedWord address) {
        if (address == null) {
            var accumulator = cpu.getAccumulator();
            var result      = cpu.rotateRight(accumulator);

            cpu.setAccumulator(result);
        } else {
            var original = cpu.getBus().read(address);
            var rotated  = cpu.rotateRight(original);

            cpu.getBus().write(address, original);
            cpu.getBus().write(address, rotated);
        }
    }
}
