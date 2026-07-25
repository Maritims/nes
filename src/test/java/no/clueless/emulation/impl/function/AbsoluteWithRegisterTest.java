package no.clueless.emulation.impl.function;

import no.clueless.emulation.Bus;
import no.clueless.emulation.Cpu6502;
import no.clueless.emulation.cpu.OperandResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

class AbsoluteWithRegisterTest {

    Cpu6502              cpu;
    Bus                  bus;
    AbsoluteWithRegister absoluteWithRegister;

    @BeforeEach
    void setUp() {
        cpu                  = mock(Cpu6502.class);
        bus                  = mock(Bus.class);
        absoluteWithRegister = new AbsoluteWithRegister(Cpu6502::getX) {
            @Override
            public OperandResult resolve(Cpu6502 cpu, Bus bus) {
                return super.resolve(cpu, bus);
            }
        };
    }

    @Test
    void fetch_effective_address_without_page_crossed() {

    }

    @Test
    void fetch_effective_address_with_page_crossed() {
    }
}