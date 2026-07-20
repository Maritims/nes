package no.clueless.emulation.types;

import java.util.function.IntFunction;

public sealed interface UInt<T extends UInt<T>> permits UInt8, UInt16 {
    int value();

    IntFunction<T> factory();

    /**
     * Increment the value. Wraps around to the minimum value if the value is the maximum value.
     *
     * @return The new value.
     */
    T increment();

    /**
     * Decrement the value. Wraps around to the maximum value if the value is zero.
     *
     * @return The new value.
     */
    T decrement();

    boolean isBitSet(int bit);

    default T and(T other) {
        if (other == null) {
            throw new IllegalArgumentException("other cannot be null");
        }
        return factory().apply(value() & other.value());
    }

    default T or(T other) {
        if (other == null) {
            throw new IllegalArgumentException("other cannot be null");
        }
        return factory().apply(value() | other.value());
    }

    default T xor(T other) {
        if (other == null) {
            throw new IllegalArgumentException("other cannot be null");
        }
        return factory().apply(value() ^ other.value());
    }
}
