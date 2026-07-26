package no.clueless.emulation;

/**
 * An implementation of the 6502 CPU.
 */
public interface Cpu6502 {
    /**
     * The number of additionalCyclesFromAddressingMode the CPU has been clocked.
     */
    int getClockCount();

    /**
     * The Accumulator register.
     *
     * @return An 8-bit value.
     */
    int getAccumulator();

    /**
     * Sets the Accumulator register.
     *
     * @param value An 8-bit value.
     */
    void setAccumulator(int value);

    /**
     * The X register.
     *
     * @return An 8-bit value.
     */
    int getX();

    /**
     * Sets the X register.
     *
     * @param value An 8-bit value.
     */
    void setX(int value);

    /**
     * The Y register.
     *
     * @return An 8-bit value.
     */
    int getY();

    /**
     * Sets the Y register.
     *
     * @param value An 8-bit value.
     */
    void setY(int value);

    /**
     * The Stack Pointer which points to a location on the bus.
     *
     * @return An 8-bit value.
     */
    int getStackPointer();

    /**
     * Reads the Stack Pointer and increments it.
     *
     * @return An 8-bit value.
     */
    int getAndIncrementStackPointer();

    /**
     * Pops a value from the stack by incrementing the stack pointer and reading the value from the bus at address 0x0100 + stack pointer.
     *
     * @return An 8-bit value.
     */
    int pullFromStack();

    /**
     * Sets the Stack Pointer.
     *
     * @param value An 8-bit value.
     */
    void setStackPointer(int value);

    /**
     * Pushes values onto the stack.
     *
     * @param values 8-bit values to push onto the stack.
     */
    void pushToStack(int... values);

    /**
     * Reads the Program Counter.
     *
     * @return A 16-bit value.
     */
    int getProgramCounter();

    /**
     * Reads the Program Counter and increments it.
     *
     * @return A 16-bit value.
     */
    int getAndIncrementProgramCounter();

    /**
     * Sets the Program Counter.
     *
     * @param value A 16-bit value.
     */
    void setProgramCounter(int value);

    /**
     * The Status register.
     *
     * @return An 8-bit value.
     */
    int getStatusRegister();

    /**
     * Connects the CPU to the bus.
     */
    void connectToBus(Bus bus);

    void addCycles(int cycles);

    /**
     * Clocks the CPU.
     */
    void clock();

    /**
     * Resets the CPU.
     */
    void reset();

    /**
     * Reads an 8-bit value from a 16-bit address.
     *
     * @param address A 16-bit address. AND with 0xFFFF to mask.
     * @return An 8-bit value.
     */
    int read(int address);

    /**
     * Writes an 8-bit value to a 16-bit address.
     *
     * @param address A 16-bit address. AND with 0xFFFF to mask.
     * @param data    An 8-bit value.
     */
    void write(int address, int data);

    /**
     * Flags that can be set in the status register. By left-shifting the value of the enum by n, we can get or set the flag in the bit at position n in the status register.
     */
    enum Flag {
        CARRY(1 << 0),
        ZERO(1 << 1),
        INTERRUPT_DISABLE(1 << 2),
        DECIMAL_MODE(1 << 3),
        BREAK(1 << 4),
        UNUSED(1 << 5),
        OVERFLOW(1 << 6),
        NEGATIVE(1 << 7);

        private final int value;

        Flag(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    void setFlag(Flag flag, boolean value);

    boolean hasFlag(Flag flag);
}
