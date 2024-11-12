package com.kneelawk.krender.model.gltf.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

public class DenseArrayBufferAccess implements BufferAccess {
    private final byte[] data;
    private final int byteOffset;
    private final int byteLength;

    public DenseArrayBufferAccess(byte[] data) {
        this(data, 0, data.length);
    }

    public DenseArrayBufferAccess(byte[] data, int byteOffset, int byteLength) {
        this.data = data;
        this.byteOffset = byteOffset;
        this.byteLength = byteLength;
    }

    @Override
    public byte get(int byteIndex) {
        return data[byteIndex + byteOffset];
    }

    @Override
    public int size() {
        return byteLength;
    }

    @Override
    public InputStream createStream() {
        return new ByteArrayInputStream(data, byteOffset, byteLength);
    }

    @Override
    public void copyBytes(int byteIndex, byte[] to, int offset, int length) {
        System.arraycopy(data, byteIndex, to, offset, length);
    }

    public static DenseArrayBufferAccess fromBase64(String base64, int byteOffset, int byteLength) {
        byte[] data = Base64.getDecoder().decode(base64);
        return new DenseArrayBufferAccess(data, byteOffset, byteLength);
    }

    public static DenseArrayBufferAccess fromBase64(String base64) {
        byte[] data = Base64.getDecoder().decode(base64);
        return new DenseArrayBufferAccess(data);
    }
}
