package no.clueless.emulation;

public interface Cartridge {
    int read(int address);

    void reset();
}
