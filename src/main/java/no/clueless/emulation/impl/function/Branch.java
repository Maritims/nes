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
        var cycles = 2;

        if(predicate.test(cpu)) {
            cycles++;

            if (PageBoundaryChecker.hasCrossed(cpu.getProgramCounter(), address)) {
                cycles++;
            }

            cpu.setProgramCounter(address);
        }

        return cycles;
    }
}
