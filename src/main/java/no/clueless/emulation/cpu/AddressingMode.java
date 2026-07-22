package no.clueless.emulation.cpu;

import no.clueless.emulation.Bus;
import no.clueless.emulation.types.UnsignedByte;
import no.clueless.emulation.types.UnsignedWord;

public enum AddressingMode implements AddressingModeStrategy {
    IMMEDIATE((cpu, bus) -> {
        var address = cpu.getAndIncrementProgramCounter();
        return new OperandResult(address, 2, false);
    }),
    ABSOLUTE((cpu, bus) -> {
        var low     = bus.read(cpu.getAndIncrementProgramCounter());
        var high    = bus.read(cpu.getAndIncrementProgramCounter());
        var address = UnsignedWord.fromBytes(low, high);
        return new OperandResult(address, 4, false);
    }),
    ABSOLUTE_X((cpu, bus) -> executeAbsoluteWithRegister(cpu, bus, cpu.getX())),
    ABSOLUTE_Y((cpu, bus) -> executeAbsoluteWithRegister(cpu, bus, cpu.getY())),
    ZERO_PAGE((cpu, bus) -> {
        var lowByte = bus.read(cpu.getAndIncrementProgramCounter());
        var address = lowByte.unsignedWordValue();

        return new OperandResult(address, 3, false);
    }),
    ZERO_PAGE_X((cpu, bus) -> executeZeroPageWithRegister(cpu, bus, cpu.getX())),
    ZERO_PAGE_Y((cpu, bus) -> executeZeroPageWithRegister(cpu, bus, cpu.getY())),
    INDIRECT((cpu, bus) -> {
        var vectorLowByte  = bus.read(cpu.getAndIncrementProgramCounter());
        var vectorHighByte = bus.read(cpu.getAndIncrementProgramCounter());
        var vector         = UnsignedWord.fromBytes(vectorLowByte, vectorHighByte);

        var low = bus.read(vector);
        // Emulate the hardware bug: force the high-byte vector lookup to stay on the same page
        var highAddress = (vector.intValue() & 0xFF00) | ((vector.intValue() + 1) & 0x00FF);
        var high        = bus.read(new UnsignedWord(highAddress));

        var address = UnsignedWord.fromBytes(low, high);
        return new OperandResult(address, 6, isPageCrossed(vector, address));
    }),
    INDIRECT_X((cpu, bus) -> {
        var base            = bus.read(cpu.getAndIncrementProgramCounter());
        var pointerLowByte  = base.addByte(cpu.getX());
        var pointerHighByte = pointerLowByte.increment();
        var addressLowByte  = bus.read(pointerLowByte.unsignedWordValue());
        var addressHighByte = bus.read(pointerHighByte.unsignedWordValue());
        var address         = UnsignedWord.fromBytes(addressLowByte, addressHighByte);

        return new OperandResult(address, 6, false);
    }),
    INDIRECT_Y((cpu, bus) -> {
        var pointerLowByte  = bus.read(cpu.getAndIncrementProgramCounter());
        var pointerHighByte = pointerLowByte.increment();

        var baseLowByte     = bus.read(pointerLowByte.unsignedWordValue());
        var baseHighByte    = bus.read(pointerHighByte.unsignedWordValue());
        var base            = UnsignedWord.fromBytes(baseLowByte, baseHighByte);
        var address         = base.addByte(cpu.getY());
        var isPageCrossed   = !base.testHighByte(address);

        if (isPageCrossed) {
            cpu.consumeCycles(1);
        }

        return new OperandResult(address, 5, isPageCrossed);
    }),
    RELATIVE((cpu, bus) -> {
        var offset  = bus.read(cpu.getAndIncrementProgramCounter());
        var address = cpu.getProgramCounter().addSignedOffset(offset);
        return new OperandResult(address, 2, false);
    });

    private final AddressingModeStrategy resolver;

    AddressingMode(AddressingModeStrategy resolver) {
        this.resolver = resolver;
    }

    @Override
    public OperandResult resolve(CPU cpu, Bus bus) {
        return resolver.resolve(cpu, bus);
    }

    private static boolean isPageCrossed(UnsignedWord a, UnsignedWord b) {
        return a.testHighByte(b);
    }

    static OperandResult executeAbsoluteWithRegister(CPU cpu, Bus bus, UnsignedByte register) {
        var lowByte       = bus.read(cpu.getAndIncrementProgramCounter());
        var highByte      = bus.read(cpu.getAndIncrementProgramCounter());
        var base          = UnsignedWord.fromBytes(lowByte, highByte);
        var address       = base.addByte(register);
        var isPageCrossed = !base.testHighByte(address);
        var cycles        = 4 + (isPageCrossed ? 1 : 0);

        return new OperandResult(address, cycles, isPageCrossed);
    }

    static OperandResult executeZeroPageWithRegister(CPU cpu, Bus bus, UnsignedByte register) {
        var base    = bus.read(cpu.getAndIncrementProgramCounter());
        var lowByte = base.addByte(register);
        var address = lowByte.unsignedWordValue();

        return new OperandResult(address, 4, false);
    }
}
