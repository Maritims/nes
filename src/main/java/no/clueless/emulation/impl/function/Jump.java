package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;
import no.clueless.emulation.util.ResolvedAddress;

public class Jump implements OpcodeFunction {
    @Override
    public int execute(Cpu6502 cpu, ResolvedAddress address) {
        cpu.setProgramCounter(address.address());
        return 0;
    }
}
