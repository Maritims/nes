package no.clueless.emulation.impl.cartridge;

import no.clueless.emulation.Mapper;

import java.util.OptionalInt;

public class Mapper001 implements Mapper {
    @Override
    public int getId() {
        return 1;
    }

    @Override
    public OptionalInt mapCpuAddress(int address) {
        return OptionalInt.empty();
    }

    @Override
    public OptionalInt mapPpuAddress(int address) {
        return OptionalInt.empty();
    }
}
