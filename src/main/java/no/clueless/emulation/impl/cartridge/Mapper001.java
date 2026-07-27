package no.clueless.emulation.impl.cartridge;

import no.clueless.emulation.Mapper;

import java.util.Optional;

public class Mapper001 implements Mapper {
    @Override
    public int getId() {
        return 1;
    }

    @Override
    public Optional<Integer> mapCpuAddress(int address) {
        return Optional.empty();
    }

    @Override
    public Optional<Integer> mapPpuAddress(int address) {
        return Optional.empty();
    }
}
