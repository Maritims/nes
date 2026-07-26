package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;

public class RotateLeftAndBitwiseAND implements OpcodeFunction {
    private final Rotate     rol = Rotate.ROL;
    private final BitwiseAND and = BitwiseAND.AND;

    @Override
    public int execute(Cpu6502 cpu, int address) {
        rol.execute(cpu, address);
        return and.execute(cpu, address);
    }
}
