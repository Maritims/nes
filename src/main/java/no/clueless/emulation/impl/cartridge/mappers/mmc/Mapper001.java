package no.clueless.emulation.impl.cartridge.mappers.mmc;

import no.clueless.emulation.Mapper;
import no.clueless.emulation.impl.cartridge.Mirroring;

import java.util.function.IntConsumer;

import static no.clueless.emulation.impl.Masks.BOTTOM_5_BITS;

public class Mapper001 implements Mapper {

    private final ControlRegister controlRegister        = new ControlRegister();
    private final LoadRegister    loadRegister           = new LoadRegister();
    private       int             prgBank;
    private       int             chrBank0;
    private       int             chrBank1;
    private       Mirroring       mirroring;

    private final int[] RAM = new int[32 * 1024];

    // TODO: Support returning data from RAM.
    @Override
    public boolean mapPrgRead(int address, IntConsumer callback) {
        if (address >= 0x6000 && address <= 0x7FFF) {
            // TODO: Read from cartridge PRG-RAM
            return true;
        }

        if (address >= 0x8000) {
            int mappedAddress;
            var prgRomBankMode = controlRegister.getPrgRomBankMode();

            switch (prgRomBankMode) {
                case PrgRomBankMode.SWITCH_32K_LOWER_BIT_IGNORED_0, PrgRomBankMode.SWITCH_32K_LOWER_BIT_IGNORED_1 -> {
                    var bank32k = prgBank & ~1;
                    mappedAddress = (bank32k * 0x4000) + (address & 0x7FFF);
                    callback.accept(mappedAddress);
                }
                case PrgRomBankMode.FIX_FIRST_BANK_SWITCH_16K_AT_C000 -> {
                    mappedAddress = address < 0xC000 ? address & 0x3FFF : (prgBank * 0x4000) + (address & 0x3FFF);
                    callback.accept(mappedAddress);
                }
                case PrgRomBankMode.FIX_LAST_BANK_SWITCH_16K_AT_8000 -> {
                    mappedAddress = address < 0xC000 ? (prgBank * 0x4000) + (address & 0x3FFF) : address & 0x3FFF;
                    callback.accept(mappedAddress);
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean mapPrgWrite(int address, int data, IntConsumer callback) {
        if (address >= 0x6000 && address <= 0x7FFF) {
            // TODO: Write to cartridge PRG-RAM.
            return true;
        }

        if (address >= 0x8000) {
            if ((data & 0x80) != 0) {
                // Reset
                loadRegister.reset();
                controlRegister.setPrgRomBankMode(3);
            } else {
                loadRegister.write(data);

                if (loadRegister.getWriteCount() == 5) {
                    var targetRegister = (address >> 13) & 0x3;

                    switch (targetRegister) {
                        case 0 -> {
                            controlRegister.setRegister(loadRegister.getRegister() & BOTTOM_5_BITS);
                            mirroring = switch (controlRegister.getNameTableArrangement()) {
                                case 0 -> Mirroring.ONE_SCREEN_LOWER_BANK;
                                case 1 -> Mirroring.ONE_SCREEN_UPPER_BANK;
                                case 2 -> Mirroring.VERTICAL;
                                case 3 -> Mirroring.HORIZONTAL;
                                default ->
                                        throw new IllegalStateException("Unexpected value: " + controlRegister.getNameTableArrangement());
                            };
                        }
                        case 1 -> chrBank0 = loadRegister.getRegister();
                        case 2 -> chrBank1 = loadRegister.getRegister();
                        case 3 -> prgBank = loadRegister.getRegister() & 0x0F;
                    }

                    loadRegister.reset();
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean mapChrRead(int address, IntConsumer callback) {
        if (address >= 0x0000 && address <= 0x1FFF) {
            int mappedAddress;
            var chrRomBankMode = controlRegister.getChrRomBankMode();

            switch (chrRomBankMode) {
                case ChrRomBankMode.MODE_4KB -> {
                    var bank8k = chrBank0 & ~1;
                    mappedAddress = (bank8k * 0x2000) + (address & 0x1FFF);
                    callback.accept(mappedAddress);
                }
                case ChrRomBankMode.MODE_8KB -> {
                    mappedAddress = address < 0x1000 ? (chrBank0 * 0x1000) + (address & 0x0FFF) : (chrBank1 * 0x1000) + (address & 0x0FFF);
                    callback.accept(mappedAddress);
                }
            }

            return true;
        }
        return false;
    }

    @Override
    public boolean mapChrWrite(int address, IntConsumer callback) {
        return false;
    }
}
