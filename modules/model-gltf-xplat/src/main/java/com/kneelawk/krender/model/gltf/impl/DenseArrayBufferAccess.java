package com.kneelawk.krender.model.gltf.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

public class DenseArrayBufferAccess implements BufferAccess {
    private final byte[] data;
    private final int byteOffset;

    public DenseArrayBufferAccess(byte[] data, int byteOffset) {
        this.data = data;
        this.byteOffset = byteOffset;
    }

    @Override
    public byte get(int byteIndex) {
        return data[byteIndex];
    }

    @Override
    public int size() {
        return data.length;
    }

    @Override
    public InputStream createStream() {
        return new ByteArrayInputStream(data);
    }

    @Override
    public void copyBytes(int byteIndex, byte[] to, int offset, int length) {
        System.arraycopy(data, byteIndex, to, offset, length);
    }

    public static DenseArrayBufferAccess fromBase64(String base64, int byteOffset) {
        byte[] data = Base64.getDecoder().decode(base64);
        return new DenseArrayBufferAccess(data, byteOffset);
    }
}
