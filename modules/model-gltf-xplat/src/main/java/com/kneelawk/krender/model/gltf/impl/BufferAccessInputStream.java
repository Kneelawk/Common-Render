package com.kneelawk.krender.model.gltf.impl;

import java.io.InputStream;

import org.jetbrains.annotations.NotNull;

public class BufferAccessInputStream extends InputStream {
    private final BufferAccess access;
    private int index = 0;

    public BufferAccessInputStream(BufferAccess access) {this.access = access;}

    @Override
    public int read() {
        if (index >= access.size()) return -1;
        return access.get(index++) & 0xFF;
    }

    @Override
    public int read(byte @NotNull [] b, int off, int len) {
        int size = access.size();
        if (index >= size) {
            return -1;
        }
        if (index + len > size) {
            len = size - index;
        }

        access.copyBytes(index, b, off, len);
        index += len;

        return len;
    }
}
