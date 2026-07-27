package no.clueless.emulation.impl.cpu;

import no.clueless.emulation.impl.AddressingModes;

/**
 * Represents an instruction.
 *
 * @param opcode         An 8-bit opcode.
 * @param addressingMode An addressing mode.
 * @param cycles         The number of cycles the instruction takes to execute.
 */
public record Instruction(Opcode opcode, AddressingModes addressingMode, int cycles) {
    public static Instruction i(Opcode opcode, AddressingModes addressingMode, int cycles) {
        return new Instruction(opcode, addressingMode, cycles);
    }
}
