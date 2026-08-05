package no.clueless.emulation.impl.ppu;

import no.clueless.emulation.Ppu2C02;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SpriteEvaluatorTest {

    PPUCtrl         control;
    PPUStatus       status;
    OAM             primaryOAM;
    OAM             secondaryOAM;
    Ppu2C02         ppu;
    SpriteEvaluator sut;

    public static Stream<Arguments> initialize_secondaryOAM_from_cycles_1_to_64() {
        return Stream.of(
                Arguments.of(0, 0),
                Arguments.of(1, 1),
                Arguments.of(64, 1),
                Arguments.of(65, 0)
        );
    }

    @BeforeEach
    void setUp() {
        control      = mock(PPUCtrl.class);
        status       = mock(PPUStatus.class);
        primaryOAM   = mock(OAM.class);
        secondaryOAM = mock(OAM.class);
        ppu          = mock(Ppu2C02.class);
        sut          = spy(new SpriteEvaluator(ppu));

        when(ppu.getControl()).thenReturn(control);
        when(ppu.getStatus()).thenReturn(status);
        when(ppu.getPrimaryOAM()).thenReturn(primaryOAM);
        when(ppu.getSecondaryOAM()).thenReturn(secondaryOAM);
    }

    @ParameterizedTest
    @ValueSource(ints = {93, 94, 95, 96, 97, 98, 99, 100})
    void sprite_is_in_range_when_it_intersects_the_scan_line(int spriteY) {
        assertTrue(sut.isSpriteInRange(spriteY, 100, 8));
    }

    @Test
    void sprite_is_not_in_range_when_its_entirely_above_the_scan_line() {
        assertFalse(sut.isSpriteInRange(101, 100, 8));
    }

    @Test
    void sprite_is_not_in_range_when_its_entirely_below_the_scan_line() {
        assertFalse(sut.isSpriteInRange(92, 100, 8));
    }

    /*@Test
    void writeToSecondaryOAM_should_always_write_sprite_Y() {
        // act
        var oamIndex = 0;
        var spriteY  = 123;
        var sprite   = mock(OAM.Entry.class);
        when(sprite.y()).thenReturn(spriteY);
        when(primaryOAM.getSprite(anyInt())).thenReturn(sprite);

        // act
        sut.writeToSecondaryOAM(oamIndex);

        // assert
        verify(secondaryOAM).set(oamIndex, 0, spriteY);
    }*/

    /*@Test
    void writeToSecondaryOAM_should_write_the_entire_sprite_when_the_sprite_is_in_range() {
        // act
        var oamIndex = 0;
        var sprite   = mock(OAM.Entry.class);

        when(sprite.y()).thenReturn(100);
        when(sprite.tileIndex()).thenReturn(1);
        when(sprite.attributes()).thenReturn(2);
        when(sprite.x()).thenReturn(3);
        when(primaryOAM.getSprite(anyInt())).thenReturn(sprite);
        when(ppu.getScanLine()).thenReturn(100);
        when(ppu.getControl().getSpriteHeight()).thenReturn(8);

        // act
        sut.writeToSecondaryOAM(oamIndex);

        // assert
        verify(sut).isSpriteInRange(anyInt(), anyInt(), anyInt());
        verify(secondaryOAM).set(oamIndex, 0, sprite.y());
        verify(secondaryOAM).set(oamIndex, 1, sprite.tileIndex());
        verify(secondaryOAM).set(oamIndex, 2, sprite.attributes());
        verify(secondaryOAM).set(oamIndex, 3, sprite.x());
    }*/

    @ParameterizedTest(name = "Skip sprite evaluation during cycle {0}")
    @ValueSource(ints = {-1, 240})
    void skip_invisible_scan_lines(int scanLine) {
        // arrange
        when(ppu.getScanLine()).thenReturn(scanLine);

        // act
        sut.onPpuCycle();

        // assert
        verify(ppu, never()).getCycle();
    }

    @ParameterizedTest(name = "Invoke secondary OAM initialization {1} times during cycle {0}")
    @MethodSource
    void initialize_secondaryOAM_from_cycles_1_to_64(int cycle, int invocations) {
        // arrange
        when(ppu.getCycle()).thenReturn(cycle);

        // act
        sut.onPpuCycle();

        // assert
        verify(secondaryOAM, times(invocations)).fill(0xFF);
    }

    @Test
    void read_from_primary_OAM_on_odd_cycles() {
        // arrange
        var sprite = mock(OAM.Entry.class);
        when(ppu.getCycle()).thenReturn(65);
        when(primaryOAM.getSprite(anyInt())).thenReturn(sprite);

        // act
        sut.onPpuCycle();

        // assert
        verify(primaryOAM).get(anyInt(), anyInt());
    }

    /*@Test
    void write_to_secondary_OAM_on_even_cycles_unless_secondary_OAM_is_full() {
        // arrange
        var sprite = mock(OAM.Entry.class);
        when(ppu.getCycle()).thenReturn(66);
        when(primaryOAM.getSprite(anyInt())).thenReturn(sprite);

        // act
        sut.onPpuCycle();

        // assert
        verify(sut).writeToSecondaryOAM(anyInt());
    }*/

    /*@Test
    void read_from_secondary_OAM_on_even_cycles_when_secondary_OAM_is_full() {
        // arrange
        var sprite = mock(OAM.Entry.class);
        when(ppu.getCycle()).thenReturn(66);
        when(sut.isSecondaryOAMFull()).thenReturn(true);
        when(secondaryOAM.getSprite(anyInt())).thenReturn(sprite);

        // act
        sut.onPpuCycle();

        // assert
        verify(sut, description("Secondary OAM state was not checked")).isSecondaryOAMFull();
        verify(secondaryOAM, description("Sprite was not read from secondary OAM")).getSprite(anyInt());
    }*/
}