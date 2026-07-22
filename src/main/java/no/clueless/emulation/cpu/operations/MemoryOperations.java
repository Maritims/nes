package no.clueless.emulation.cpu.operations;

import no.clueless.emulation.cpu.CPU;
import no.clueless.emulation.types.UnsignedByte;
import no.clueless.emulation.types.UnsignedWord;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class MemoryOperations {
    /**
     * Reads an 8-bit value from the bus and updates the status register.
     *
     * @param address  The address to read from.
     * @param register A consumer that updates a register.
     * @throws IllegalArgumentException if address or register is null.
     */
    private static void loadRegisterFromMemory(CPU cpu, UnsignedWord address, Consumer<UnsignedByte> register) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }

        var data = cpu.getBus().read(address);
        cpu.getStatusRegister().updateNegativeAndZero(data);
        register.accept(data);
    }

    /**
     * Loads the accumulator with the value at the specified address.
     */
    public static void lda(CPU cpu, UnsignedWord address) {
        loadRegisterFromMemory(cpu, address, cpu::setAccumulator);
    }

    /**
     * Loads the X register with the value at the specified address.
     */
    public static void ldx(CPU cpu, UnsignedWord address) {
        loadRegisterFromMemory(cpu, address, cpu::setX);
    }

    /**
     * Loads the Y register with the value at the specified address.
     */
    public static void ldy(CPU cpu, UnsignedWord address) {
        loadRegisterFromMemory(cpu, address, cpu::setY);
    }

    /**
     * UNOFFICIAL: Loads the accumulator and X register with the value at the specified address and updates the status register.
     */
    public static void lax(CPU cpu, UnsignedWord address) {
        var memoryData = cpu.getBus().read(address);
        cpu.setAccumulator(memoryData);
        cpu.setX(memoryData);
        cpu.getStatusRegister().updateNegativeAndZero(memoryData);
    }

    /**
     * UNOFFICIAL: ANDs the accumulator and X register and writes the result to the specified address.
     */
    public static void sax(CPU cpu, UnsignedWord address) {
        var accumulator = cpu.getAccumulator();
        var x           = cpu.getX();
        var result      = accumulator.and(x);

        cpu.getBus().write(address, result);
    }

    /**
     * Reads an 8-bit value from the bus and updates the status register.
     *
     * @param address  The address to write to.
     * @param register The register to read from.
     * @throws IllegalArgumentException if address or register is null.
     */
    private static void storeRegisterInMemory(CPU cpu, UnsignedWord address, Supplier<UnsignedByte> register) {
        if (address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        if (register == null) {
            throw new IllegalArgumentException("register cannot be null");
        }
        cpu.getBus().write(address, register.get());
    }

    /**
     * Stores the accumulator value at the specified address.
     */
    public static void sta(CPU cpu, UnsignedWord address) {
        storeRegisterInMemory(cpu, address, cpu::getAccumulator);
    }

    /**
     * Stores the X register value at the specified address.
     */
    public static void stx(CPU cpu, UnsignedWord address) {
        storeRegisterInMemory(cpu, address, cpu::getX);
    }

    /**
     * Stores the Y register value at the specified address.
     */
    public static void sty(CPU cpu, UnsignedWord address) {
        storeRegisterInMemory(cpu, address, cpu::getY);
    }

    /**
     * Transfers the accumulator to the X register.
     */
    public static void tax(CPU cpu, UnsignedWord ignored) {
        cpu.transfer(cpu.getAccumulator(), cpu::setX);
    }

    /**
     * Transfers the accumulator to the Y register.
     */
    public static void tay(CPU cpu, UnsignedWord ignored) {
        cpu.transfer(cpu.getAccumulator(), cpu::setY);
    }

    /**
     * Transfers the X register to the accumulator.
     */
    public static void txa(CPU cpu, UnsignedWord ignored) {
        cpu.transfer(cpu.getX(), cpu::setAccumulator);
    }

    /**
     * Transfers the Y register to the accumulator.
     */
    public static void tya(CPU cpu, UnsignedWord ignored) {
        cpu.transfer(cpu.getY(), cpu::setAccumulator);
    }

    /**
     * Increments the memory data at the specified address and updates the status register.
     */
    public static void inc(CPU cpu, UnsignedWord address) {
        var memoryData = cpu.getBus().read(address);
        var result     = memoryData.increment();

        cpu.getStatusRegister().updateNegativeAndZero(result);
        cpu.getBus().write(address, memoryData);
        cpu.getBus().write(address, result);
    }

    /**
     * Decrements the memory data at the specified address and updates the status register.
     */
    public static void dec(CPU cpu, UnsignedWord address) {
        var memoryData = cpu.getBus().read(address);
        var result     = memoryData.decrement();

        cpu.getStatusRegister().updateNegativeAndZero(result);
        cpu.getBus().write(address, memoryData);
        cpu.getBus().write(address, result);
    }

    /**
     * Increments the specified register and updates the status register.
     */
    private static void incrementRegister(CPU cpu, Supplier<UnsignedByte> readRegister, Consumer<UnsignedByte> updateRegister) {
        var original = readRegister.get();
        var result   = original.increment();

        updateRegister.accept(result);
        cpu.getStatusRegister().updateNegativeAndZero(result);
    }

    /**
     * Decrements the specified register and updates the status register.
     */
    private static void decrementRegister(CPU cpu, Supplier<UnsignedByte> readRegister, Consumer<UnsignedByte> updateRegister) {
        var original = readRegister.get();
        var result   = original.decrement();

        updateRegister.accept(result);
        cpu.getStatusRegister().updateNegativeAndZero(result);
    }

    /**
     * Increments the X register and updates the status register.
     */
    public static void inx(CPU cpu, UnsignedWord ignored) {
        incrementRegister(cpu, cpu::getX, cpu::setX);
    }

    /**
     * Decrements the X register and updates the status register.
     */
    public static void dex(CPU cpu, UnsignedWord ignored) {
        decrementRegister(cpu, cpu::getX, cpu::setX);
    }

    /**
     * Increments the Y register and updates the status register.
     */
    public static void iny(CPU cpu, UnsignedWord ignored) {
        incrementRegister(cpu, cpu::getY, cpu::setY);
    }

    /**
     * Decrements the Y register and updates the status register.
     */
    public static void dey(CPU cpu, UnsignedWord ignored) {
        decrementRegister(cpu, cpu::getY, cpu::setY);
    }

    public static void nop(CPU ignoredCpu, UnsignedWord ignored) {
    }
}
