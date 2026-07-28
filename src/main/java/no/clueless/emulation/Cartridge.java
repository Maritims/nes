package no.clueless.emulation;

import java.util.Optional;

public interface Cartridge {
    Optional<Integer> cpuRead(int address);

    void cpuWrite(int address, int value);

    Optional<Integer> ppuRead(int address);

    void ppuWrite(int address, int value);

    void reset();
}
