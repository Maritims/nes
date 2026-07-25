package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;

import java.util.function.Predicate;

public class Branch implements OpcodeFunction {
    private final Predicate<Cpu6502> predicate;

    public Branch(Predicate<Cpu6502> predicate) {
        this.predicate = predicate;
    }

    @Override
    public int execute(Cpu6502 cpu, int address) {
        if(predicate.test(cpu)) {
            cpu.addCycles(1);

            if (PageBoundaryChecker.hasCrossed(cpu.getProgramCounter(), address)) {
                cpu.addCycles(1);
            }

            cpu.setProgramCounter(address);
        }
        return cpu.getProgramCounter();
    }
}
