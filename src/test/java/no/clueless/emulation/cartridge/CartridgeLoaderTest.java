package no.clueless.emulation.cartridge;

import no.clueless.emulation.impl.cartridge.CartridgeLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CartridgeLoaderTest {

    public static Stream<Arguments> isValidHeaderSignature_should_return_false_for_invalid_header_signature() {
        return Stream.of(
                Arguments.of((Object) new byte[] { 'N', 'E', 'S', 0x1B, 0x1A }),
                Arguments.of((Object) new byte[] { 'N', 'E', 'S', 0x1B })
        );
    }

    @Test
    void isValidHeaderSignature_should_return_true_for_valid_header_signature() {
        var bytes = new byte[] { 'N', 'E', 'S', 0x1A };
        assertTrue(CartridgeLoader.isValidHeaderSignature(bytes));
    }

    @ParameterizedTest
    @MethodSource
    void isValidHeaderSignature_should_return_false_for_invalid_header_signature(byte[] bytes) {
        assertFalse(CartridgeLoader.isValidHeaderSignature(bytes));
    }
}