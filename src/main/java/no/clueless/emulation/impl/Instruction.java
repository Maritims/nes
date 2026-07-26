package no.clueless.emulation.impl;

/**
 * Represents an instruction.
 *
 * @param opcode                             An 8-bit opcode.
 * @param addressingMode                     An addressing mode.
 * @param additionalCyclesFromAddressingMode The number of additional cycles from the addressing mode.
 */
public record Instruction(Opcode opcode, AddressingModes addressingMode, int additionalCyclesFromAddressingMode) {
    public static Instruction i(Opcode opcode, AddressingModes addressingMode, int cycles) {
        return new Instruction(opcode, addressingMode, cycles);
    }
}
