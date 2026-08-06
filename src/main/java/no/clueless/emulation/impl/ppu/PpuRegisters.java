package no.clueless.emulation.impl.ppu;

public record PpuRegisters(
        PpuControl control,
        PpuMask mask,
        PpuStatus status,
        LoopyRegister vramAddress,
        LoopyRegister tempVramAddress,
        OAM primaryOAM,
        OAM secondaryOAM
) {
    public PpuRegisters() {
        this(new PpuControl(), new PpuMask(), new PpuStatus(), new LoopyRegister(), new LoopyRegister(), OAM.PRIMARY, OAM.SECONDARY);
    }

    public void reset() {
        control.setRegister(0);
        mask.setRegister(0);
        status.setRegister(0);
    }
}
