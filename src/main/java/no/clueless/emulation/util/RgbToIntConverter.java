package no.clueless.emulation.util;

public class RgbToIntConverter {
    public static int convertRgbToInt(int red, int green, int blue) {
        return ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | (blue & 0xFF);
    }
}
