package no.clueless.emulation.cpu;

import no.clueless.emulation.types.UInt8;

import java.util.EnumSet;
import java.util.Set;

/**
 * Represents the status register in the CPU.
 *
 * @param flags A set of flags.
 */
public record StatusRegister(Set<Flag> flags) {
    /**
     * Constructor.
     *
     * @param flags The flags to set.
     * @throws IllegalArgumentException if flags is null.
     */
    public StatusRegister {
        if (flags == null) {
            throw new IllegalArgumentException("Flags cannot be null");
        }
        flags = EnumSet.copyOf(flags);
    }

    /**
     * Sets a flag.
     *
     * @param flag The flag to set.
     * @return A new StatusRegister instance with the flag set, or the same instance if the flag was already set.
     * @throws IllegalArgumentException if the flag is null.
     */
    public StatusRegister setFlag(Flag flag) {
        if (flag == null) {
            throw new IllegalArgumentException("Flag cannot be null");
        }
        if (flags.contains(flag)) {
            return this;
        }

        var newFlags = EnumSet.copyOf(flags);
        newFlags.add(flag);
        return new StatusRegister(newFlags);
    }

    /**
     * Clears a flag.
     *
     * @param flag The flag to clear.
     * @return A new StatusRegister instance with the flag cleared, or the same instance if the flag was not set.
     * @throws IllegalArgumentException if flag is null.
     */
    public StatusRegister clearFlag(Flag flag) {
        if (flag == null) {
            throw new IllegalArgumentException("Flag cannot be null");
        }
        if (!flags.contains(flag)) {
            return this;
        }

        var set = EnumSet.copyOf(flags);
        set.remove(flag);
        return new StatusRegister(set);
    }

    /**
     * Updates a flag based on a condition.
     *
     * @param flag      The flag to update.
     * @param condition The condition to check.
     * @return A new StatusRegister instance with the flag updated.
     */
    public StatusRegister updateFlag(Flag flag, boolean condition) {
        return condition ? setFlag(flag) : clearFlag(flag);
    }

    /**
     * Checks if a flag is set.
     *
     * @param flag The flag to check.
     * @return True if the flag is set, false otherwise.
     * @throws IllegalArgumentException if flag is null.
     */
    public boolean hasFlag(Flag flag) {
        if (flag == null) {
            throw new IllegalArgumentException("Flag cannot be null");
        }
        return flags.contains(flag);
    }

    /**
     * Updates the negative and zero flags based on the given value.
     *
     * @param value The value to check.
     * @return A new StatusRegister instance with the updated flags.
     * @throws IllegalArgumentException if value is null.
     */
    public StatusRegister updateNegativeAndZero(UInt8 value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }

        var raw        = value.value();
        var isZero     = raw == 0;
        var isNegative = (raw & 0x80) != 0;

        return updateFlag(Flag.Zero, isZero)
                .updateFlag(Flag.Negative, isNegative);
    }
}
