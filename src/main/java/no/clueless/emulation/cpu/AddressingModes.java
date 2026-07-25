package no.clueless.emulation.cpu;

import no.clueless.emulation.AddressingModeFunction;
import no.clueless.emulation.Bus;
import no.clueless.emulation.types.UnsignedByte;
import no.clueless.emulation.types.UnsignedWord;

public enum AddressingModes implements AddressingModeFunction<CPU> {
    IMMEDIATE((cpu, bus) -> {
        var address = cpu.getAndIncrementProgramCounter();
        return new OperandResult(address.intValue(), 2, false);
    }),
    ABSOLUTE((cpu, bus) -> {
        var low     = bus.read(cpu.getAndIncrementProgramCounter().intValue());
        var high    = bus.read(cpu.getAndIncrementProgramCounter().intValue());
        var address = UnsignedWord.fromInts(low, high);
        return new OperandResult(address.intValue(), 4, false);
    }),
    ABSOLUTE_X((cpu, bus) -> executeAbsoluteWithRegister(cpu, bus, cpu.getX())),
    ABSOLUTE_Y((cpu, bus) -> executeAbsoluteWithRegister(cpu, bus, cpu.getY())),
    ZERO_PAGE((cpu, bus) -> {
        var lowByte = new UnsignedByte(bus.read(cpu.getAndIncrementProgramCounter().intValue()));
        var address = lowByte.unsignedWordValue();

        return new OperandResult(address.intValue(), 3, false);
    }),
    ZERO_PAGE_X((cpu, bus) -> executeZeroPageWithRegister(cpu, bus, cpu.getX())),
    ZERO_PAGE_Y((cpu, bus) -> executeZeroPageWithRegister(cpu, bus, cpu.getY())),
    INDIRECT((cpu, bus) -> {
        var vectorLowByte  = bus.read(cpu.getAndIncrementProgramCounter().intValue());
        var vectorHighByte = bus.read(cpu.getAndIncrementProgramCounter().intValue());
        var vector         = UnsignedWord.fromInts(vectorLowByte, vectorHighByte);

        var low = bus.read(vector.intValue());
        // Emulate the hardware bug: force the high-byte vector lookup to stay on the same page
        var highAddress = (vector.intValue() & 0xFF00) | ((vector.intValue() + 1) & 0x00FF);
        var high        = bus.read(highAddress);

        var address = UnsignedWord.fromInts(low, high);
        return new OperandResult(address.intValue(), 6, isPageCrossed(vector, address));
    }),
    INDIRECT_X((cpu, bus) -> {
        var base            = bus.read(cpu.getAndIncrementProgramCounter().intValue());
        var pointerLowByte  = base + cpu.getX().intValue();
        var pointerHighByte = pointerLowByte + 1;
        var addressLowByte  = bus.read(pointerLowByte);
        var addressHighByte = bus.read(pointerHighByte);
        var address         = UnsignedWord.fromInts(addressLowByte, addressHighByte);

        return new OperandResult(address.intValue(), 6, false);
    }),
    INDIRECT_Y((cpu, bus) -> {
        var pointerLowByte  = bus.read(cpu.getAndIncrementProgramCounter().intValue());
        var pointerHighByte = pointerLowByte + 1;

        var baseLowByte     = bus.read(pointerLowByte);
        var baseHighByte    = bus.read(pointerHighByte);
        var base            = UnsignedWord.fromInts(baseLowByte, baseHighByte);
        var address         = base.addByte(cpu.getY());
        var isPageCrossed   = !base.testHighByte(address);

        if (isPageCrossed) {
            cpu.consumeCycles(1);
        }

        return new OperandResult(address.intValue(), 5, isPageCrossed);
    }),
    RELATIVE((cpu, bus) -> {
        var offset  = bus.read(cpu.getAndIncrementProgramCounter().intValue());
        var address = cpu.getProgramCounter().addSignedOffset(new UnsignedByte(offset));
        return new OperandResult(address.intValue(), 2, false);
    });

    private final AddressingModeFunction<CPU> resolver;

    AddressingModes(AddressingModeFunction<CPU> resolver) {
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
        var lowByte       = bus.read(cpu.getAndIncrementProgramCounter().intValue());
        var highByte      = bus.read(cpu.getAndIncrementProgramCounter().intValue());
        var base          = UnsignedWord.fromInts(lowByte, highByte);
        var address       = base.addByte(register);
        var isPageCrossed = !base.testHighByte(address);
        var cycles        = 4 + (isPageCrossed ? 1 : 0);

        return new OperandResult(address.intValue(), cycles, isPageCrossed);
    }

    static OperandResult executeZeroPageWithRegister(CPU cpu, Bus bus, UnsignedByte register) {
        var base    = bus.read(cpu.getAndIncrementProgramCounter().intValue());
        var lowByte = base + register.intValue();
        var address = new UnsignedWord(lowByte);

        return new OperandResult(address.intValue(), 4, false);
    }
}
