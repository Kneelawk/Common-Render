package com.kneelawk.krender.engine.api.util;

import java.nio.ByteOrder;

/**
 * KRender color utilities.
 */
public final class ColorUtils {
    private ColorUtils() {}

    private static final boolean BIG_ENDIAN = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;

    /**
     * Scales one 8-bit number by another.
     * <p>
     * This behaves as though each number represented a value between 0.0 and 1.0, where 0x00 corresponds to 0.0 and
     * 0xFF corresponds to 1.0.
     *
     * @param a the first number to scale by the other.
     * @param b the second number to scale by the other.
     * @return the scaled result of the two.
     */
    public static int scale(int a, int b) {
        // we divide by 0xFF instead of just shifting to avoid the little bit of loss that comes from the fact that
        // 1.0 corresponds to 0xFF instead of 0x100.
        return ((a & 0xFF) * (b & 0xFF)) / 0xFF;
    }

    /**
     * Converts a float between 0.0 and 1.0 to an 8-bit color value between 0x00 and 0xFF.
     *
     * @param f the float value to convert.
     * @return the equivalent color value.
     */
    public static int toFixed(float f) {
        return (int) (f * 0xFF);
    }

    /**
     * Converts an 8-bit color value between 0x00 and 0xFF into a float between 0.0 and 1.0.
     *
     * @param i the color value to convert.
     * @return the equivalent float value.
     */
    public static float fromFixed(int i) {
        return (float) i / 255f;
    }

    /**
     * Combines four integer values each between 0x00 and 0xFF into an integer color.
     *
     * @param r the red value.
     * @param g the green value.
     * @param b the blue value.
     * @param a the alpha value.
     * @return the complete color.
     */
    public static int toArgb(int r, int g, int b, int a) {
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    /**
     * Converts a set of four floats representing color values between 0.0 and 1.0 into an integer color.
     *
     * @param r the red value.
     * @param g the green value.
     * @param b the blue value.
     * @param a the alpha value.
     * @return the complete color.
     */
    public static int toArgb(float r, float g, float b, float a) {
        return toArgb(toFixed(r), toFixed(g), toFixed(b), toFixed(a));
    }

    /**
     * Gets the red component of an ARGB color integer.
     *
     * @param argb the full color.
     * @return the red component.
     */
    public static int red(int argb) {
        return (argb >>> 16) & 0xFF;
    }

    /**
     * Gets the green component of an ARGB color integer.
     *
     * @param argb the full color.
     * @return the green component.
     */
    public static int green(int argb) {
        return (argb >>> 8) & 0xFF;
    }

    /**
     * Gets the blue component of an ARGB color integer.
     *
     * @param argb the full color.
     * @return the blue component.
     */
    public static int blue(int argb) {
        return argb & 0xFF;
    }

    /**
     * Gets the alpha component of an ARGB color integer.
     *
     * @param argb the full color.
     * @return the alpha component.
     */
    public static int alpha(int argb) {
        return (argb >>> 24) & 0xFF;
    }

    /**
     * Gets the red component of an ARGB color integer as a float between 0.0 and 1.0.
     *
     * @param argb the full color.
     * @return the red float component.
     */
    public static float redFloat(int argb) {
        return fromFixed(red(argb));
    }

    /**
     * Gets the green component of an ARGB color integer as a float between 0.0 and 1.0.
     *
     * @param argb the full color.
     * @return the green float component.
     */
    public static float greenFloat(int argb) {
        return fromFixed(green(argb));
    }

    /**
     * Gets the blue component of an ARGB color integer as a float between 0.0 and 1.0.
     *
     * @param argb the full color.
     * @return the blue float component.
     */
    public static float blueFloat(int argb) {
        return fromFixed(blue(argb));
    }

    /**
     * Gets the alpha component of an ARGB color integer as a float between 0.0 and 1.0.
     *
     * @param argb the full color.
     * @return the alpha float component.
     */
    public static float alphaFloat(int argb) {
        return fromFixed(alpha(argb));
    }

    /**
     * Converts KRender ARGB color into native OpenGL color.
     *
     * @param argb the KRender ARGB color to convert.
     * @return the equivalent vanilla color.
     */
    public static int toNative(int argb) {
        // fullbright optimization
        if (argb == -1) return -1;

        if (BIG_ENDIAN) {
            // convert to RGBA
            return ((argb & 0x00FFFFFF) << 8) | ((argb & 0xFF000000) >>> 24);
        } else {
            // JNI attempts to keep integers having the same value which messes up the byte order, giving us ABGR
            return (argb & 0xFF00FF00) | ((argb & 0x00FF0000) >>> 16) | ((argb & 0x000000FF) << 16);
        }
    }

    /**
     * Converts native OpenGL color into KRender ARGB color.
     *
     * @param color the vanilla color to convert.
     * @return the equivalent KRender ARGB color.
     */
    public static int fromNative(int color) {
        // fullbright optimization
        if (color == -1) return -1;

        if (BIG_ENDIAN) {
            // convert from RGBA
            return ((color & 0xFFFFFF00) >>> 8) | ((color & 0x000000FF) << 24);
        } else {
            // convert from ABGR
            return (color & 0xFF00FF00) | ((color & 0x00FF0000) >>> 16) | ((color & 0x000000FF) << 16);
        }
    }
}
