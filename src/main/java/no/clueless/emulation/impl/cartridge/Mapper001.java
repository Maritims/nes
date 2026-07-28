package no.clueless.emulation.impl.cartridge;

import no.clueless.emulation.Mapper;

import java.util.Optional;
import java.util.function.IntConsumer;

public class Mapper001 implements Mapper {

    @Override
    public boolean mapCpuRead(int address, IntConsumer callback) {
        return false;
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
