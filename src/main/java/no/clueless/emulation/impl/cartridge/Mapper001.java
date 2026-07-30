package no.clueless.emulation.impl.cartridge;

import no.clueless.emulation.Mapper;

import java.util.function.IntConsumer;

public class Mapper001 implements Mapper {

    @Override
    public boolean mapPrgRead(int address, IntConsumer callback) {
        return false;
    }

    @Override
    public boolean mapPrgWrite(int address, IntConsumer callback) {
        return false;
    }

    @Override
    public boolean mapChrRead(int address, IntConsumer callback) {
        return false;
    }

    @Override
    public boolean mapChrWrite(int address, IntConsumer callback) {
        return false;
    }
}
