package no.clueless.emulation.impl;

import no.clueless.emulation.AddressingModeFunction;
import no.clueless.emulation.Cpu6502;

public record Instruction(
        String name,
        Class<? extends OpcodeFunction> opcodeFunction,
        Class<? extends AddressingModeFunction<Cpu6502>> addressingModeFunction,
        int cycles
) {
    public static Instruction[] arrayOf(
            String name1, Class<? extends OpcodeFunction> fn1, Class<? extends AddressingModeFunction<Cpu6502>> addressingMode1, int cycles1,
            String name2, Class<? extends OpcodeFunction> fn2, Class<? extends AddressingModeFunction<Cpu6502>> addressingMode2, int cycles2,
            String name3, Class<? extends OpcodeFunction> fn3, Class<? extends AddressingModeFunction<Cpu6502>> addressingMode3, int cycles3,
            String name4, Class<? extends OpcodeFunction> fn4, Class<? extends AddressingModeFunction<Cpu6502>> addressingMode4, int cycles4,
            String name5, Class<? extends OpcodeFunction> fn5, Class<? extends AddressingModeFunction<Cpu6502>> addressingMode5, int cycles5,
            String name6, Class<? extends OpcodeFunction> fn6, Class<? extends AddressingModeFunction<Cpu6502>> addressingMode6, int cycles6,
            String name7, Class<? extends OpcodeFunction> fn7, Class<? extends AddressingModeFunction<Cpu6502>> addressingMode7, int cycles7,
            String name8, Class<? extends OpcodeFunction> fn8, Class<? extends AddressingModeFunction<Cpu6502>> addressingMode8, int cycles8,
            String name9, Class<? extends OpcodeFunction> fn9, Class<? extends AddressingModeFunction<Cpu6502>> addressingMode9, int cycles9,
            String name10, Class<? extends OpcodeFunction> fn10, Class<? extends AddressingModeFunction<Cpu6502>> addressingMode10, int cycles10,
            String name11, Class<? extends OpcodeFunction> fn11, Class<? extends AddressingModeFunction<Cpu6502>> addressingMode11, int cycles11,
            String name12, Class<? extends OpcodeFunction> fn12, Class<? extends AddressingModeFunction<Cpu6502>> addressingMode12, int cycles12,
            String name13, Class<? extends OpcodeFunction> fn13, Class<? extends AddressingModeFunction<Cpu6502>> addressingMode13, int cycles13,
            String name14, Class<? extends OpcodeFunction> fn14, Class<? extends AddressingModeFunction<Cpu6502>> addressingMode14, int cycles14,
            String name15, Class<? extends OpcodeFunction> fn15, Class<? extends AddressingModeFunction<Cpu6502>> addressingMode15, int cycles15,
            String name16, Class<? extends OpcodeFunction> fn16, Class<? extends AddressingModeFunction<Cpu6502>> addressingMode16, int cycles16
    ) {
        return new Instruction[]{
                new Instruction(name1, fn1, addressingMode1, cycles1),
                new Instruction(name2, fn2, addressingMode2, cycles2),
                new Instruction(name3, fn3, addressingMode3, cycles3),
                new Instruction(name4, fn4, addressingMode4, cycles4),
                new Instruction(name5, fn5, addressingMode5, cycles5),
                new Instruction(name6, fn6, addressingMode6, cycles6),
                new Instruction(name7, fn7, addressingMode7, cycles7),
                new Instruction(name8, fn8, addressingMode8, cycles8),
                new Instruction(name9, fn9, addressingMode9, cycles9),
                new Instruction(name10, fn10, addressingMode10, cycles10),
                new Instruction(name11, fn11, addressingMode11, cycles11),
                new Instruction(name12, fn12, addressingMode12, cycles12),
                new Instruction(name13, fn13, addressingMode13, cycles13),
                new Instruction(name14, fn14, addressingMode14, cycles14),
                new Instruction(name15, fn15, addressingMode15, cycles15),
                new Instruction(name16, fn16, addressingMode16, cycles16),
        };
    }
}
