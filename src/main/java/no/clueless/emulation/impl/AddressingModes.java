package no.clueless.emulation.impl;

import no.clueless.emulation.AddressingModeFunction;
import no.clueless.emulation.Bus;
import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.cpu.OperandResult;
import no.clueless.emulation.impl.function.*;

import java.util.HashMap;
import java.util.Map;

public enum AddressingModes implements AddressingModeFunction<Cpu6502> {
    IMMEDIATE(Immediate.class),
    ABSOLUTE(Absolute.class),
    ABSOLUTE_X(AbsoluteX.class),
    ABSOLUTE_Y(AbsoluteY.class),
    ZERO_PAGE(ZeroPage.class),
    ZERO_PAGE_X(ZeroPageX.class),
    ZERO_PAGE_Y(ZeroPageY.class),
    INDIRECT(Indirect.class),
    INDIRECT_X(IndirectX.class),
    INDIRECT_Y(IndirectY.class),
    RELATIVE(Relative.class);

    private final Class<? extends AddressingModeFunction<Cpu6502>> mode;

    AddressingModes(Class<? extends AddressingModeFunction<Cpu6502>> mode) {
        this.mode = mode;
    }

    private static final Map<Class<? extends AddressingModeFunction<Cpu6502>>, AddressingModeFunction<Cpu6502>> modes = new HashMap<>();

    @Override
    public OperandResult resolve(Cpu6502 cpu, Bus bus) {
        return null;
    }
}
