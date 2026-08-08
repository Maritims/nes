package no.clueless.emulation.util;

public class ByteFlipper {
    public static int flip(int value) {
        return Integer.reverse(value) >>> 24;
    }
}
