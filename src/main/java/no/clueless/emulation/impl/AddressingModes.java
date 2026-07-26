package no.clueless.emulation.impl;

import no.clueless.emulation.AddressingModeFunction;
import no.clueless.emulation.Bus;
import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.util.ResolvedAddress;
import no.clueless.emulation.impl.function.*;

public enum AddressingModes implements AddressingModeFunction<Cpu6502> {
    IMM(new Immediate()),
    ABS(new Absolute()),
    ABX(new AbsoluteX()),
    ABY(new AbsoluteY()),
    ZP0(new ZeroPage()),
    ZPX(new ZeroPageX()),
    ZPY(new ZeroPageY()),
    IND(new Indirect()),
    IZX(new IndirectX()),
    IZY(new IndirectY()),
    REL(new Relative()),
    IMP(new Implied());

    private final AddressingModeFunction<Cpu6502> function;

    AddressingModes(AddressingModeFunction<Cpu6502> function) {
        this.function = function;
    }

    @Override
    public ResolvedAddress resolve(Cpu6502 cpu, Bus bus) {
        return function.resolve(cpu, bus);
    }
}
