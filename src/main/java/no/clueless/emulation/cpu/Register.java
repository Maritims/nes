package no.clueless.emulation.cpu;

import no.clueless.emulation.types.UInt;

public abstract class Register<T extends UInt<T>> {
    private T value;

    protected Register() {}

    protected Register(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    /**
     * Increment the value by one.
     */
    public void increment() {
        this.value = value.increment();
    }

    /**
     * Decrement the value by one.
     */
    public void decrement() {
        this.value = value.decrement();
    }

    public void updateValue(T newValue) {
        if (newValue == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        this.value = newValue;
    }
}
