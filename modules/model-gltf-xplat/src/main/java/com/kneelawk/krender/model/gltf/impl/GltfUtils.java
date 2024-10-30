package com.kneelawk.krender.model.gltf.impl;

public class GltfUtils {
    public static short swapShort(short in) {
        return (short) (((in >>> 8) & 0x00FF) | ((in << 8) & 0xFF00));
    }

    public static int swapInt(int in) {
        return ((in >>> 24) & 0x000000FF) | ((in >>> 8) & 0x0000FF00) | ((in << 8) & 0x00FF0000) |
            ((in << 24) & 0xFF000000);
    }

    public static long swapLog(long in) {
        return ((in >>> 56) & 0x00000000000000FFL) |
            ((in >>> 40) & 0x000000000000FF00L) |
            ((in >>> 24) & 0x0000000000FF0000L) |
            ((in >>> 8) & 0x00000000FF000000L) |
            ((in << 8) & 0x000000FF00000000L) |
            ((in << 24) & 0x0000FF0000000000L) |
            ((in << 40) & 0x00FF000000000000L) |
            ((in << 56) & 0xFF00000000000000L);
    }
}
