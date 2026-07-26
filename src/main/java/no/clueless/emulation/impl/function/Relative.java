package no.clueless.emulation.impl.function;

import no.clueless.emulation.AddressingModeFunction;
import no.clueless.emulation.Bus;
import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.cpu.ResolvedAddress;

public class Relative implements AddressingModeFunction<Cpu6502> {
    @Override
    public ResolvedAddress resolve(Cpu6502 cpu, Bus bus) {
        // Get the offset byte.
        var offsetAddress = cpu.getAndIncrementProgramCounter();

        // Cast to byte since the offset is signed.
        var offset = (byte) bus.read(offsetAddress);

        // Get the next instruction byte which is the address to apply the offset to.
        var basePc        = cpu.getProgramCounter();
        var targetAddress = basePc + offset;

        return new ResolvedAddress(targetAddress, 2, PageBoundaryChecker.hasCrossed(basePc, targetAddress));
    }
}
