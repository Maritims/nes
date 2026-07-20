package no.clueless.emulation.cpu;

import no.clueless.emulation.types.UInt8;

import java.util.*;

/**
 * Represents the status register in the CPU.
 */
public class StatusRegister {
    private final EnumSet<Flag> flags;

    /**
     * Constructor.
     *
     * @param flags The initial flags.
     * @throws IllegalArgumentException if flags is null.
     */
    public StatusRegister(Set<Flag> flags) {
        if (flags == null) {
            throw new IllegalArgumentException("Flags cannot be null");
        }
        this.flags = flags.isEmpty() ? EnumSet.noneOf(Flag.class) : EnumSet.copyOf(flags);
    }

    public StatusRegister() {
        this(EnumSet.noneOf(Flag.class));
    }

    public static StatusRegister fromByte(UInt8 byteValue) {
        if (byteValue == null) {
            throw new IllegalArgumentException("Byte value cannot be null");
        }

        var flags = new HashSet<Flag>();
        var value = byteValue.value();

        for(var flag : Flag.values()) {
            if ((value & flag.getMask()) != 0) {
                flags.add(flag);
            }
        }

        return new StatusRegister(flags);
    }

    /**
     * Returns a copy of the flags.
     *
     * @return An unmodifiable copy of the current flags.
     */
    public EnumSet<Flag> getFlags() {
        return EnumSet.copyOf(flags);
    }

    /**
     * Sets flags.
     *
     * @param flags 1 or more flags to set.
     * @return This instance for fluent chaining.
     * @throws IllegalArgumentException if flags is null or empty.
     */
    public StatusRegister setFlag(Flag... flags) {
        if (flags == null || flags.length == 0) {
            throw new IllegalArgumentException("flags cannot be null or empty");
        }
        this.flags.addAll(Arrays.asList(flags));
        return this;
    }

    /**
     * Sets flags.
     *
     * @param flags 1 or more flags to set.
     * @return This instance for fluent chaining.
     * @throws IllegalArgumentException if flags is null or empty.
     */
    public StatusRegister setFlag(Collection<Flag> flags) {
        if (flags == null || flags.isEmpty()) {
            throw new IllegalArgumentException("flags cannot be null or empty");
        }
        this.flags.addAll(flags);
        return this;
    }

    /**
     * Clears flags.
     *
     * @param flags The flags to clear.
     * @throws IllegalArgumentException if flags is null or empty.
     */
    public void clearFlag(Flag... flags) {
        clearFlag(Arrays.asList(flags));
    }

    /**
     * Clears flags.
     *
     * @param flags The flags to clear.
     * @throws IllegalArgumentException if flags is null or empty.
     */
    public void clearFlag(Collection<Flag> flags) {
        if (flags == null || flags.isEmpty()) {
            throw new IllegalArgumentException("flags cannot be null or empty");
        }
        flags.forEach(this.flags::remove);
    }

    /**
     * Updates a flag based on a condition.
     *
     * @param flag      The flag to update.
     * @param condition The condition to check.
     */
    public void updateFlag(Flag flag, boolean condition) {
        if (condition) {
            setFlag(flag);
        } else {
            clearFlag(flag);
        }
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
     * @throws IllegalArgumentException if value is null.
     */
    public void updateNegativeAndZero(UInt8 value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }

        var raw        = value.value();
        var isZero     = raw == 0;
        var isNegative = (raw & 0x80) != 0;

        updateFlag(Flag.Zero, isZero);
        updateFlag(Flag.Negative, isNegative);
    }

    public void update(StatusRegister other) {
        if (other == null) {
            throw new IllegalArgumentException("other cannot be null");
        }

        for (var flag : Flag.values()) {
            if (flag == Flag.Break || flag == Flag.Five) {
                continue;
            }
            updateFlag(flag, other.hasFlag(flag));
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        StatusRegister that = (StatusRegister) object;
        return Objects.equals(flags, that.flags);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(flags);
    }

    @Override
    public String toString() {
        return "StatusRegister{" +
                "flags=" + flags +
                '}';
    }

    /**
     * Converts the status register to a byte.
     * <p>On the physical 6502, bit 5 is always 1 when pushed to the stack (PHP/BRK).</p>
     *
     * @return The byte representation of the status register.
     */
    public UInt8 toByte() {
        return new UInt8(flags.stream().mapToInt(Flag::getMask).sum() | 0x20);
    }
}
