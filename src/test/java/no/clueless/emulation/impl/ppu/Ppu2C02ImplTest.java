package no.clueless.emulation.impl.ppu;

import no.clueless.emulation.gui.FrameBuffer;
import no.clueless.emulation.impl.ppu.register.LoopyRegister;
import no.clueless.emulation.impl.ppu.register.PpuControl;
import no.clueless.emulation.impl.ppu.register.PpuMask;
import no.clueless.emulation.impl.ppu.register.PpuStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class Ppu2C02ImplTest {
    private PpuControl    control;
    private PpuMask       mask;
    private PpuStatus     status;
    private LoopyRegister v;
    private FrameBuffer   frameBuffer;
    private Ppu2C02Impl   sut;

    public static Stream<Arguments> preRenderScanLineTransfersVerticalBitsFromCycle280ToCycle304WhenRenderingIsEnabled() {
        return Stream.of(
                Arguments.of(280, 1),
                Arguments.of(281, 1),
                Arguments.of(282, 1),
                Arguments.of(283, 1),
                Arguments.of(284, 1),
                Arguments.of(285, 1),
                Arguments.of(286, 1),
                Arguments.of(287, 1),
                Arguments.of(288, 1),
                Arguments.of(289, 1),
                Arguments.of(290, 1),
                Arguments.of(291, 1),
                Arguments.of(292, 1),
                Arguments.of(293, 1),
                Arguments.of(294, 1),
                Arguments.of(295, 1),
                Arguments.of(296, 1),
                Arguments.of(297, 1),
                Arguments.of(298, 1),
                Arguments.of(299, 1),
                Arguments.of(300, 1),
                Arguments.of(301, 1),
                Arguments.of(302, 1),
                Arguments.of(303, 1),
                Arguments.of(304, 1),
                Arguments.of(305, 0) // Verify that we've added an upper boundary.
        );
    }

    public static Stream<Arguments> verticalBlankingLinesSetsVblankAndSetsNmiIfEnabled() {
        return Stream.of(
                Arguments.of(241, true, true),
                Arguments.of(242, true, true),
                Arguments.of(243, true, true),
                Arguments.of(244, true, true),
                Arguments.of(245, true, true),
                Arguments.of(246, false, false),
                Arguments.of(247, false, false),
                Arguments.of(248, false, false),
                Arguments.of(249, false, false),
                Arguments.of(250, false, false),
                Arguments.of(251, false, false),
                Arguments.of(252, false, false),
                Arguments.of(253, false, false),
                Arguments.of(254, false, false),
                Arguments.of(255, false, false),
                Arguments.of(256, false, false),
                Arguments.of(257, false, false),
                Arguments.of(258, false, false),
                Arguments.of(259, false, false),
                Arguments.of(260, false, false)
        );
    }

    @BeforeEach
    void setUp() {
        control     = spy(new PpuControl());
        mask        = spy(new PpuMask());
        status      = spy(new PpuStatus());
        v           = spy(new LoopyRegister());
        frameBuffer = mock(FrameBuffer.class);
        sut         = new Ppu2C02Impl(frameBuffer);
    }

    @Test
    void preRenderScanLineClearFlagsOnCycleOne() {
        // arrange
        sut.setScanLine(-1);
        sut.setCycle(1);

        // act
        sut.clock();

        // assert
        verify(status).setVerticalBlank(false);
        verify(status).setSpriteZeroHit(false);
        verify(status).setSpriteOverflow(false);
    }

    @ParameterizedTest
    @MethodSource
    void preRenderScanLineTransfersVerticalBitsFromCycle280ToCycle304WhenRenderingIsEnabled(int cycle, int transferVerticalBitsInvocations) {
        // arrange
        mask.setRenderSprites(true);
        sut.setScanLine(-1);
        sut.setCycle(cycle);

        // act
        sut.clock();

        // assert
        verify(v, times(transferVerticalBitsInvocations)).transferVerticalBits(any(LoopyRegister.class));
    }

    @ParameterizedTest
    @MethodSource
    void verticalBlankingLinesSetsVblankAndSetsNmiIfEnabled(int scanLine, boolean isNmiEnabled, boolean expectedNmi) {
        // arrange
        sut.setScanLine(scanLine);
        sut.setCycle(1);
        control.setEnableNmi(isNmiEnabled);

        // act
        sut.clock();

        // assert
        assertEquals(expectedNmi, sut.isNmi());
    }
}