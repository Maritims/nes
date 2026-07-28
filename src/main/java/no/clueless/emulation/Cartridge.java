package no.clueless.emulation;

import java.util.Optional;

public interface Cartridge {
    Optional<Integer> cpuRead(int address);

    Optional<Integer> ppuRead(int address);

    void reset();
}
