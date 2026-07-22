package no.clueless.emulation.cpu;

import no.clueless.emulation.types.UnsignedWord;

public record OperandResult(UnsignedWord address, int cyclesConsumed, boolean isPageCrossed) {
}
