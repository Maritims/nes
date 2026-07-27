package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.cpu.OpcodeFunction;
import no.clueless.emulation.util.ResolvedAddress;

public class JumpToSubroutine implements OpcodeFunction {
    @Override
    public int execute(Cpu6502 cpu, ResolvedAddress address) {
        // PC should point to the last byte of the JSR instruction.
        var pc = cpu.getProgramCounter() - 1;

        // Push PC + 2 high byte to stack.
        cpu.pushToStack((pc & 0xFF00) >> 8);

        // Push PC + 2 low byte to stack.
        cpu.pushToStack(pc & 0xFF);

        // PC 0 memory.
        cpu.setProgramCounter(address.address());

        return 0;
    }
}
