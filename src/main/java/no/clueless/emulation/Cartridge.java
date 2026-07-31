package no.clueless.emulation;

import java.util.Optional;

public interface Cartridge {
    Mapper getMapper();

    Optional<Integer> readPrg(int address);

    void writePrg(int address, int value);

    Optional<Integer> readChr(int address);

    boolean writeChr(int address, int value);

    void reset();
}
