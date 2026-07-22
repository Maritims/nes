package no.clueless.emulation.cpu;

import no.clueless.emulation.Bus;

@FunctionalInterface
public interface AddressingModeStrategy {
    OperandResult resolve(CPU cpu, Bus bus);
}
