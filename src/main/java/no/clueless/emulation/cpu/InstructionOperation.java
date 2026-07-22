package no.clueless.emulation.cpu;

import no.clueless.emulation.types.UnsignedWord;

@FunctionalInterface
public interface InstructionOperation {
    void execute(CPU cpu, UnsignedWord address);
}
