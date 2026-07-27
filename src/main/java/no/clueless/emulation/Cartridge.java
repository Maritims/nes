package no.clueless.emulation;

import java.util.Optional;

public interface Cartridge {
    Optional<Integer> readPrgRom(int address);

    Optional<Integer> readChrRom(int address);

    void reset();
}
