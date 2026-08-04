package no.clueless.emulation.util;

import no.clueless.emulation.impl.AddressingModes;
import no.clueless.emulation.impl.cpu.Opcode;

public class Disassembler {
    public static String disassemble(int pc, Opcode opcode, AddressingModes addressingMode, int address) {
        var sb = new StringBuilder();

        sb.append("$%04X ".formatted(pc));
        sb.append("%s".formatted(opcode.name()));

        var addressingModeStr = switch (addressingMode) {
            case IMM -> "#$%02X".formatted(address & 0xFF);
            case IMP -> "{IMP}";
            case ABS -> "$%04X {ABS}".formatted(address & 0xFFFF);
            case ZP0 -> "$%02X {ZP0}".formatted(address & 0xFF);
            case REL -> "$%04X {REL}".formatted(address & 0xFFFF);
            case IZY -> "$%02X,Y {IZY}".formatted(address & 0xFF);
            case IZX -> "$%02X,X {IZX}".formatted(address & 0xFF);
            default -> "{%s}".formatted(addressingMode.name());
        };

        sb.append(" %s".formatted(addressingModeStr));

        return sb.toString();
    }
}
