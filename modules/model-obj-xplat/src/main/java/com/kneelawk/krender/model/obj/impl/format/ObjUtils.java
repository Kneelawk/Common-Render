package com.kneelawk.krender.model.obj.impl.format;

import java.io.IOException;

public final class ObjUtils {
    public static float[] parseFloats(String[] strs, int off, int len) throws IOException {
        float[] floats = new float[len];
        for (int i = 0; i < len; i++) {
            floats[i] = parseFloat(strs[i + off], "Bad float '" + strs[i + off] + "' at index " + i);
        }
        return floats;
    }

    public static float parseFloat(String str, String error) throws IOException {
        try {
            return Float.parseFloat(str);
        } catch (NumberFormatException e) {
            throw new IOException(error, e);
        }
    }
    
    public static int parseInt(String str, String error) throws IOException {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            throw new IOException(error, e);
        }
    }
}
