package no.clueless.emulation.impl.function;

import no.clueless.emulation.AddressingModeFunction;
import no.clueless.emulation.Bus;
import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.util.ResolvedAddress;

public class Indirect implements AddressingModeFunction<Cpu6502> {
    @Override
    public ResolvedAddress resolve(Cpu6502 cpu, Bus bus) {
        var vectorLowByte  = bus.read(cpu.getAndIncrementProgramCounter());
        var vectorHighByte = bus.read(cpu.getAndIncrementProgramCounter());
        var vector         = (vectorHighByte << 8) | vectorLowByte;

        var lowByte = bus.read(vector);

        // Emulate the hardware bug: force the high-byte vector lookup to stay on the same page
        var highByteAddress = (vector & 0xFF00) | ((vector + 1) & 0x00FF);
        var highByte        = bus.read(highByteAddress);

        var targetAddress = (highByte << 8) | lowByte;

        return new ResolvedAddress(targetAddress, PageBoundaryChecker.hasCrossed(vector, targetAddress));
    }
}
