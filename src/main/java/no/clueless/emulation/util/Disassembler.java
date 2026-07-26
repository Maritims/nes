package no.clueless.emulation.util;

import no.clueless.emulation.impl.Instruction;

public class Disassembler {
    public static String disassemble(int programCounter, Instruction instruction, int address) {
        var sb = new StringBuilder();

        sb.append("$%04X ".formatted(programCounter));
        sb.append("%s".formatted(instruction.opcode().name()));

        var addressingModeStr = switch (instruction.addressingMode()) {
            case IMM -> "#$%d".formatted(address);
            case IMP -> "{IMP}";
            case ZP0 -> "$%02X {ZP0}".formatted(address & 0xFF);
            case ZPX -> "$%02X,X {ZPX}".formatted(address & 0xFF);
            default -> "{%s}".formatted(instruction.addressingMode().name());
        };

        sb.append(" %s".formatted(addressingModeStr));

        return sb.toString();
    }
}
