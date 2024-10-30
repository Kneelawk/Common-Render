package com.kneelawk.krender.model.gltf.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

public class DenseArrayBufferAccess implements BufferAccess {
    private final byte[] data;

    public DenseArrayBufferAccess(byte[] data) {this.data = data;}

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

    public static DenseArrayBufferAccess fromBase64(String base64) {
        byte[] data = Base64.getDecoder().decode(base64);
        return new DenseArrayBufferAccess(data);
    }
}
