package no.clueless.emulation.cpu.operations;

import no.clueless.emulation.cpu.CPU;
import no.clueless.emulation.cpu.Flag;
import no.clueless.emulation.types.UnsignedByte;
import no.clueless.emulation.types.UnsignedWord;

import static no.clueless.emulation.cpu.operations.ALU.shiftLeft;
import static no.clueless.emulation.cpu.operations.ALU.shiftRight;

public class ShiftOperations {
    static UnsignedByte rotateLeft(CPU cpu, UnsignedByte value) {
        var carryIn  = cpu.getStatusRegister().hasFlag(Flag.Carry) ? 1 : 0;
        var carryOut = value.testBit(7);
        var result   = new UnsignedByte(((value.intValue() << 1) | carryIn) & 0xFF);

        cpu.getStatusRegister().updateFlag(Flag.Carry, carryOut);
        cpu.getStatusRegister().updateNegativeAndZero(result);

        return result;
    }

    static UnsignedByte rotateRight(CPU cpu, UnsignedByte value) {
        var carryIn  = cpu.getStatusRegister().hasFlag(Flag.Carry) ? 0x80 : 0;
        var carryOut = value.testBit(0);
        var result   = new UnsignedByte(((value.intValue() >> 1) | carryIn) & 0xFF);

        cpu.getStatusRegister().updateFlag(Flag.Carry, carryOut);
        cpu.getStatusRegister().updateNegativeAndZero(result);

        return result;
    }

    public static void asl(CPU cpu, UnsignedWord address) {
        if (address == null) {
            var accumulator = cpu.getAccumulator();
            var result      = shiftLeft(cpu, accumulator);

            cpu.setAccumulator(result);
        } else {
            var original = cpu.getBus().read(address);
            var result   = shiftLeft(cpu, original);

            cpu.getBus().write(address, original);
            cpu.getBus().write(address, result);
        }
    }

    public static void lsr(CPU cpu, UnsignedWord address) {
        if (address == null) {
            var accumulator = cpu.getAccumulator();
            var result      = shiftRight(cpu, accumulator);

            cpu.setAccumulator(result);
        } else {
            var original = cpu.getBus().read(address);
            var shifted  = shiftRight(cpu, original);

            cpu.getBus().write(address, original);
            cpu.getBus().write(address, shifted);
        }
    }

    public static void rol(CPU cpu, UnsignedWord address) {
        if (address == null) {
            var accumulator = cpu.getAccumulator();
            var result      = rotateLeft(cpu, accumulator);

            cpu.setAccumulator(result);
        } else {
            var original = cpu.getBus().read(address);
            var rotated  = rotateLeft(cpu, original);

            cpu.getBus().write(address, original);
            cpu.getBus().write(address, rotated);
        }
    }

    public static void ror(CPU cpu, UnsignedWord address) {
        if (address == null) {
            var accumulator = cpu.getAccumulator();
            var result      = rotateRight(cpu, accumulator);

            cpu.setAccumulator(result);
        } else {
            var original = cpu.getBus().read(address);
            var rotated  = rotateRight(cpu, original);

            cpu.getBus().write(address, original);
            cpu.getBus().write(address, rotated);
        }
    }

    public static void rla(CPU cpu, UnsignedWord address) {
        var accumulator = cpu.getAccumulator();
        var memoryData  = cpu.getBus().read(address);
        var result      = rotateLeft(cpu, memoryData);

        cpu.setAccumulator(accumulator.and(result));
        cpu.getBus().write(address, result);
        cpu.getStatusRegister().updateNegativeAndZero(cpu.getAccumulator());
    }

    public static void rra(CPU cpu, UnsignedWord address) {
        var memoryData = cpu.getBus().read(address);
        var result     = rotateRight(cpu, memoryData);

        cpu.getBus().write(address, result);
        ALU.executeArithmeticCalculation(cpu, address, false);
    }
}
