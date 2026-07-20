package no.clueless.emulation.cpu;

import no.clueless.emulation.types.UInt;

public abstract class Register<T extends UInt<T>, R extends Register<T, R>> {
    private final T value;

    protected Register(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        this.value = value;
    }

    /**
     * Creates a new instance of the same type with the given value.
     *
     * @param newValue The new value.
     * @return A new instance of the same type with the given value.
     */
    protected abstract R create(T newValue);

    public T getValue() {
        return value;
    }

    /**
     * Increment the value by one.
     *
     * @return A new instance of the same type with the incremented value.
     */
    public R increment() {
        return create(value.increment());
    }

    /**
     * Decrement the value by one.
     *
     * @return A new instance of the same type with the decremented value.
     */
    public R decrement() {
        return create(value.decrement());
    }
}
