package no.clueless.emulation.cpu;

import no.clueless.emulation.types.UnsignedByte;

public record Opcode(
        UnsignedByte code,
        no.clueless.emulation.impl.Opcode mnemonic,
        int cycles,
        AddressingModes addressingMode) {
    public Opcode {
        if (code == null) {
            throw new IllegalArgumentException("code cannot be null");
        }
        if (mnemonic == null) {
            throw new IllegalArgumentException("mnemonic cannot be null");
        }
        if (cycles < 1) {
            throw new IllegalArgumentException("additionalCyclesFromAddressingMode must be at least 1");
        }
    }

    public Opcode(int code, no.clueless.emulation.impl.Opcode mnemonic, int cycles, AddressingModes addressingMode) {
        this(new UnsignedByte(code), mnemonic, cycles, addressingMode);
    }
}
