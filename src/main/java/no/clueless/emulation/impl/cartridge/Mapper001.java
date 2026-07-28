package no.clueless.emulation.impl.cartridge;

import no.clueless.emulation.Mapper;

import java.util.function.IntConsumer;

public class Mapper001 implements Mapper {

    @Override
    public boolean mapCpuRead(int address, IntConsumer callback) {
        return false;
    }

    @Override
    public boolean mapCpuWrite(int address, IntConsumer callback) {
        return false;
    }

    @Override
    public boolean mapPpuRead(int address, IntConsumer callback) {
        return false;
    }

    @Override
    public void mapPpuWrite(int address, int value) {

    }
}
