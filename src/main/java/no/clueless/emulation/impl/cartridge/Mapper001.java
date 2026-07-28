package no.clueless.emulation.impl.cartridge;

import no.clueless.emulation.Mapper;

import java.util.Optional;

public class Mapper001 implements Mapper {

    @Override
    public Optional<Integer> mapCpuRead(int address) {
        return Optional.empty();
    }

    @Override
    public void mapCpuWrite(int address, int value) {

    }

    @Override
    public Optional<Integer> mapPpuRead(int address) {
        return Optional.empty();
    }

    @Override
    public void mapPpuWrite(int address, int value) {

    }
}
