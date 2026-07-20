package no.clueless.emulation.cpu;

import no.clueless.emulation.types.UInt16;
import no.clueless.emulation.types.UInt8;

import java.util.function.Function;

/**
 * Represents an opcode.
 *
 * @param code            The 8-byte value of the opcode.
 * @param instruction     The instruction that this opcode represents.
 * @param bytes           The total number of bytes consumed by the instruction and its operands.
 * @param cycles          The total number of CPU cycles required to execute this instruction.
 * @param addressResolver A function that resolves the addressing mode of the opcode.
 */
public record Opcode(
        UInt8 code,
        Instruction instruction,
        int bytes,
        int cycles,
        Function<CPU, UInt16> addressResolver
) {
    public Opcode {
        if (code == null) {
            throw new IllegalArgumentException("code cannot be null");
        }
        if (instruction == null) {
            throw new IllegalArgumentException("instruction cannot be null");
        }
        if (bytes < 1) {
            throw new IllegalArgumentException("bytes must be at least 1");
        }
        if (cycles < 1) {
            throw new IllegalArgumentException("cycles must be at least 1");
        }
        if (addressResolver == null) {
            throw new IllegalArgumentException("addressResolver cannot be null");
        }
    }

    public Opcode(int code, Instruction instruction, int bytes, int cycles, Function<CPU, UInt16> addressResolver) {
        this(new UInt8(code), instruction, bytes, cycles, addressResolver);
    }
}
