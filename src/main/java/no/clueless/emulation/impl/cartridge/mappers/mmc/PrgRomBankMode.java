package no.clueless.emulation.impl.cartridge.mappers.mmc;

public enum PrgRomBankMode {
    /**
     * Switches 32 KB of PRG-ROM at CPU $8000–$FFFF, ignoring the low bit of the bank number (Mode 0).
     * This means a 32 KB bank is mapped across the entire $8000–$FFFF window, and any attempt
     * to select an odd-numbered bank will effectively target the preceding even-numbered bank.
     */
    SWITCH_32K_LOWER_BIT_IGNORED_0,

    /**
     * Switches 32 KB of PRG-ROM at CPU $8000–$FFFF, ignoring the low bit of the bank number (Mode 1).
     * This means a 32 KB bank is mapped across the entire $8000–$FFFF window, and any attempt
     * to select an odd-numbered bank will effectively target the preceding even-numbered bank.
     */
    SWITCH_32K_LOWER_BIT_IGNORED_1,

    /**
     * Fixes the first 16 KB bank at $8000 and switches a 16 KB bank at $C000.
     * This means CPU addresses $8000–$BFFF always point to the first bank,
     * while $C000–$FFFF can be dynamically swapped.
     */
    FIX_FIRST_BANK_SWITCH_16K_AT_C000,

    /**
     * Fixes the last 16 KB bank at $C000 and switches a 16 KB bank at $8000.
     * This means CPU addresses $C000–$FFFF always point to the last bank,
     * while $8000–$BFFF can be dynamically swapped.
     */
    FIX_LAST_BANK_SWITCH_16K_AT_8000
}