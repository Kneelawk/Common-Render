package com.kneelawk.krender.model.gltf.impl;

import java.io.InputStream;
import java.math.RoundingMode;

import com.google.common.math.IntMath;

import com.kneelawk.krender.model.gltf.impl.format.GltfAccessorComponentType;
import com.kneelawk.krender.model.gltf.impl.format.GltfAccessorType;

public class StrideBufferAccess implements BufferAccess {
    private final BufferAccess delegate;
    private final int byteOffset;
    private final int elementSize;
    private final int stride;
    private final int subElementSize;
    private final int subElementCount;
    private final int subStride;
    private final int size;

    public StrideBufferAccess(BufferAccess delegate, int viewStride, int byteOffset, int count,
                              GltfAccessorComponentType componentType, GltfAccessorType accessorType, boolean padding) {
        this.delegate = delegate;
        this.byteOffset = byteOffset;
        this.stride = viewStride;

        int unpaddedElementSize = componentType.getBytes() * accessorType.getComponentCount();

        if (padding) {
            // handle padding special cases
            if (unpaddedElementSize % 4 != 0 && accessorType.getType() != GltfAccessorType.Type.MAT) {
                elementSize = IntMath.divide(unpaddedElementSize, 4, RoundingMode.UP) * 4;
                subElementSize = unpaddedElementSize;
                subElementCount = 1;
                subStride = elementSize;
            } else if (accessorType == GltfAccessorType.MAT2 && componentType.getBytes() == 1) {
                elementSize = 8;
                subElementSize = 2;
                subElementCount = 2;
                subStride = 4;
            } else if (accessorType == GltfAccessorType.MAT3 && componentType.getBytes() == 1) {
                elementSize = 12;
                subElementSize = 3;
                subElementCount = 3;
                subStride = 4;
            } else if (accessorType == GltfAccessorType.MAT3 && componentType.getBytes() == 2) {
                elementSize = 24;
                subElementSize = 6;
                subElementCount = 3;
                subStride = 8;
            } else {
                elementSize = unpaddedElementSize;
                subElementSize = elementSize;
                subElementCount = 1;
                subStride = elementSize;
            }
        } else {
            elementSize = unpaddedElementSize;
            subElementSize = elementSize;
            subElementCount = 1;
            subStride = elementSize;
        }

        size = subElementSize * subElementCount * count;
    }

    @Override
    public byte get(final int byteIndex) {
        final int subElement = byteIndex / subElementSize;
        final int subIndex = byteIndex % subElementSize;
        final int nonSubIndex = subElement * subStride + subIndex;
        final int superElement = nonSubIndex / elementSize;
        final int superIndex = nonSubIndex % elementSize;
        final int index = superElement * stride + superIndex;
        final int offsetIndex = index + byteOffset;
        return delegate.get(offsetIndex);
    }

    // unlikely to be used, but maybe I'll come back to this
    // TODO: unit test this
    @Override
    public void copyBytes(final int byteIndex, byte[] to, final int offset, final int length) {
        final int startSubElement = byteIndex / subElementSize;
        final int startSubElementIndex = startSubElement % subElementCount;
        final int startSubIndex = byteIndex % subElementSize;
        final int startNonSubIndex = startSubElement * subStride + startSubIndex;
        final int startSuperElement = startNonSubIndex / elementSize;
        final int startSuperIndex = startNonSubIndex % elementSize;
        final int startIndex = startSuperElement * stride + startSuperIndex;
        final int startOffsetIndex = startIndex + byteOffset;

        final int end = offset + length;

        int copyOffset = startOffsetIndex;
        int toOffset = offset;

        // copy rest of sub-element
        if (startSubIndex != 0) {
            int copyLen = Math.min(subElementSize - startSubIndex, length);
            delegate.copyBytes(copyOffset, to, toOffset, copyLen);
            toOffset += copyLen;
            copyOffset += copyLen + (subStride - subElementSize);
        }

        // fast-exit shortcut for if we're complete already
        if (end <= toOffset) return;

        assert
            (copyOffset % stride) % subStride == 0 :
            "copyOffset: " + copyOffset + ", stride: " + stride + ", subStride: " + subStride;

        if (end > toOffset + subElementSize * (subElementCount - startSubElementIndex)) {
            // copy rest of element
            if (startSubElementIndex != 0) {
                for (int i = startSubElementIndex; i < subElementCount; i++) {
                    delegate.copyBytes(copyOffset, to, toOffset, subElementSize);
                    toOffset += subElementSize;
                    copyOffset += subStride;
                }

                copyOffset += stride - elementSize;
            }

            assert copyOffset % stride == 0 : "copyOffset: " + copyOffset + ", stride: " + stride;

            // main loop copy
            while (toOffset + elementSize < end) {
                for (int i = 0; i < subElementCount; i++) {
                    delegate.copyBytes(copyOffset, to, toOffset, subElementSize);
                    toOffset += subElementSize;
                    copyOffset += subStride;
                }
                copyOffset += (stride - elementSize);
            }
        }

        // fast-exit shortcut for if we're complete already
        if (end <= toOffset) return;

        assert copyOffset % stride == 0 : "copyOffset: " + copyOffset + ", stride: " + stride;

        // copy remaining sub elements
        while (toOffset + subElementSize < end) {
            delegate.copyBytes(copyOffset, to, toOffset, subElementSize);
            toOffset += subElementSize;
            copyOffset += subStride;
        }

        // copy final partial sub-element
        if (toOffset < end) {
            int copyLen = end - toOffset;
            delegate.copyBytes(copyOffset, to, toOffset, copyLen);
        }
    }

    @Override
    public int size() {
        return size;
    }
}
