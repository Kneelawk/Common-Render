package com.kneelawk.krender.model.gltf.impl;

import java.io.InputStream;

import com.kneelawk.krender.model.gltf.impl.format.GltfAccessorComponentType;

public interface BufferAccess {
    /**
     * Gets a byte at the given index in the underlying buffer.
     *
     * @param byteIndex the index within the buffer to get the byte.
     * @return the byte at the requested index.
     */
    byte get(int byteIndex);

    int size();

    InputStream createStream();

    default void copyBytes(int byteIndex, byte[] to, int offset, int length) {
        for (int i = 0; i < length; i++) {
            to[i + offset] = get(i + byteIndex);
        }
    }

    default short getShort(int byteIndex) {
        // little endian
        return (short) ((get(byteIndex) & 0x00FF) | ((get(byteIndex + 1) << 8) & 0xFF00));
    }

    default int getInt(int byteIndex) {
        return (get(byteIndex) & 0x000000FF) |
            ((get(byteIndex + 1) << 8) & 0x0000FF00) |
            ((get(byteIndex + 2) << 16) & 0x00FF0000) |
            ((get(byteIndex + 3) << 24) & 0xFF000000);
    }

    default long getLong(int byteIndex) {
        return (get(byteIndex) & 0x00000000000000FFL) |
            ((get(byteIndex + 1) << 8) & 0x000000000000FF00L) |
            ((get(byteIndex + 2) << 16) & 0x0000000000FF0000L) |
            ((get(byteIndex + 3) << 24) & 0x00000000FF000000L) |
            (((long) get(byteIndex + 4) << 32) & 0x000000FF00000000L) |
            (((long) get(byteIndex + 5) << 40) & 0x0000FF0000000000L) |
            (((long) get(byteIndex + 6) << 48) & 0x00FF000000000000L) |
            (((long) get(byteIndex + 7) << 56) & 0xFF00000000000000L);
    }

    default float getFloat(int byteIndex) {
        return Float.intBitsToFloat(getInt(byteIndex));
    }

    default int getInt(int byteIndex, GltfAccessorComponentType componentType) {
        return switch (componentType) {
            case SIGNED_BYTE -> get(byteIndex);
            case UNSIGNED_BYTE -> get(byteIndex) & 0xFF;
            case SIGNED_SHORT -> getShort(byteIndex);
            case UNSIGNED_SHORT -> getShort(byteIndex) & 0xFFFF;
            case UNSIGNED_INT -> getInt(byteIndex);
            case FLOAT -> (int) getFloat(byteIndex);
        };
    }
}
