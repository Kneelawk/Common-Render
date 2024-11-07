package com.kneelawk.krender.model.gltf.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.kneelawk.krender.model.gltf.impl.format.GltfAccessorComponentType;
import com.kneelawk.krender.model.gltf.impl.format.GltfAccessorType;

public class StrideBufferAccessTests {
    @Test
    void testCopyBytes1() {
        DenseArrayBufferAccess denseAccess = new DenseArrayBufferAccess(new byte[]{0, 1, 2, 3, 4, 5, 6, 7}, 0);
        StrideBufferAccess strideAccess =
            new StrideBufferAccess(denseAccess, 4, 0, 2, GltfAccessorComponentType.SIGNED_BYTE, GltfAccessorType.VEC3,
                true);

        byte[] read = new byte[6];
        strideAccess.copyBytes(0, read, 0, 6);

        Assertions.assertArrayEquals(new byte[]{0, 1, 2, 4, 5, 6}, read);
    }

    @Test
    void testCopyBytes2() {
        DenseArrayBufferAccess denseAccess =
            new DenseArrayBufferAccess(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15}, 0);
        StrideBufferAccess strideAccess =
            new StrideBufferAccess(denseAccess, 4, 0, 2, GltfAccessorComponentType.SIGNED_BYTE, GltfAccessorType.MAT2,
                true);

        byte[] read = new byte[8];
        strideAccess.copyBytes(0, read, 0, 8);

        Assertions.assertArrayEquals(new byte[]{0, 1, 4, 5, 8, 9, 12, 13}, read);
    }
}
