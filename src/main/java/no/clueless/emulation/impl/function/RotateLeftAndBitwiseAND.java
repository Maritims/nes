package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;
import no.clueless.emulation.util.ResolvedAddress;

public class RotateLeftAndBitwiseAND implements OpcodeFunction {
    @Override
    public int execute(Cpu6502 cpu, ResolvedAddress address) {
        Rotate.ROL.execute(cpu, address);
        BitwiseOperation.AND.execute(cpu, address);
        return 0;
    }
}
