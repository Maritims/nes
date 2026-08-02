package no.clueless.emulation.impl.apu;

import no.clueless.emulation.Apu2A03;

import static no.clueless.emulation.impl.Masks.BOTTOM_5_BITS;

public class Apu2A03Impl implements Apu2A03 {
    private int     clockCounter        = 0;
    private int     frameClockCounter   = 0;
    private int     pulse1LengthCounter = 0;
    private boolean pulse1Enabled       = false;

    @Override
    public void clock() {
        if (clockCounter % 6 == 0) {
            frameClockCounter++;

            if (frameClockCounter >= 7457) {
                frameClockCounter = 0;
                pulse1LengthCounter--;
            }
        }
        clockCounter++;
    }

    @Override
    public int readRegister(int address) {
        address &= 0xFFFF;
        if (address == 0x4015) {
            var status = 0;
            if (pulse1LengthCounter == 0) {
                status |= 0x01;
            }
            return status;
        }
        return 0;
    }

    @Override
    public void writeRegister(int address, int value) {
        address &= 0xFFFF;
        value &= 0xFF;

        switch (address) {
            case 0x4003, 0x4007, 0x4008, 0x400F:
                var lengthIndex = (value >> 3) & BOTTOM_5_BITS;
                if (address == 0x4003 && pulse1Enabled) {
                    pulse1LengthCounter = 0;
                }
                break;
            case 0x4015:
                pulse1Enabled = (value & 0x01) != 0;
                if (!pulse1Enabled) {
                    pulse1LengthCounter = 0;
                }
                break;
            default:
                // TODO: Implement remaining APU registers.
                break;
        }
    }

    @Override
    public void reset() {
        pulse1Enabled       = false;
        pulse1LengthCounter = 0;
        frameClockCounter   = 0;
    }
}
