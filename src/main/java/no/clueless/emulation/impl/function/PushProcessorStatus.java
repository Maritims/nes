package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.cpu.OpcodeFunction;
import no.clueless.emulation.util.ResolvedAddress;

public class PushProcessorStatus implements OpcodeFunction {
    @Override
    public int execute(Cpu6502 cpu, ResolvedAddress ignored) {
        // The Break flag exists only in the flags pushed to the stack, not as a real state in the CPU.
        // Source: https://www.nesdev.org/wiki/Instruction_reference#PHP
        var result = cpu.getStatusRegister() | Cpu6502.Flag.BREAK.getValue() | Cpu6502.Flag.UNUSED.getValue();
        cpu.pushToStack(result);

        return 0;
    }
}
