package no.clueless.emulation.impl.function;

import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.impl.OpcodeFunction;

public class Increment implements OpcodeFunction {
    @Override
    public int execute(Cpu6502 cpu, int address) {
        var memoryData = cpu.read(address);
        var result = memoryData + 1;

        cpu.setFlag(Cpu6502.Flag.ZERO, result == 0);
        cpu.setFlag(Cpu6502.Flag.NEGATIVE, (result & 0x80) != 0);

        cpu.write(address, memoryData);
        cpu.write(address, result);

        return 5;
    }
}
