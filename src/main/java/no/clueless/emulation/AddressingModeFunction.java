package no.clueless.emulation;

import no.clueless.emulation.util.ResolvedAddress;

@FunctionalInterface
public interface AddressingModeFunction<T> {
    ResolvedAddress resolve(T cpu, Bus bus);
}
