package com.kneelawk.krender.model.gltf.impl;

import java.io.InputStream;
import java.util.Arrays;

import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntRBTreeMap;
import it.unimi.dsi.fastutil.ints.Int2IntSortedMap;

import com.kneelawk.krender.model.gltf.impl.format.GltfAccessorComponentType;
import com.kneelawk.krender.model.gltf.impl.format.GltfAccessorType;

public class SparseBufferAccess implements BufferAccess {
    private final @Nullable BufferAccess base;
    private final int baseElementSize;
    private final int size;
    private final Int2IntSortedMap reverseIndices = new Int2IntRBTreeMap();
    private final BufferAccess valuesView;
    private final int valuesOffset;

    public SparseBufferAccess(@Nullable BufferAccess base, GltfAccessorComponentType baseComponentType,
                              GltfAccessorType baseType, int count, BufferAccess indicesView, int indicesOffset,
                              GltfAccessorComponentType indicesType, BufferAccess valuesView, int valuesOffset) {
        this.base = base;
        this.baseElementSize = baseComponentType.getBytes() * baseType.getComponentCount();
        this.size = this.baseElementSize * count;
        this.valuesView = valuesView;
        this.valuesOffset = valuesOffset;

        for (int i = 0; i < count; i++) {
            reverseIndices.put(indicesView.getInt(i * indicesType.getBytes() + indicesOffset, indicesType), i);
        }
    }

    @Override
    public byte get(int byteIndex) {
        int elementIndex = byteIndex / baseElementSize;
        int byteInElement = byteIndex % baseElementSize;
        if (reverseIndices.containsKey(elementIndex)) {
            return valuesView.get(reverseIndices.get(elementIndex) * baseElementSize + byteInElement + valuesOffset);
        } else if (base != null) {
            return base.get(byteIndex);
        } else {
            return 0;
        }
    }

    @Override
    public void copyBytes(int byteIndex, byte[] to, int offset, int length) {
        if (base != null) {
            base.copyBytes(byteIndex, to, offset, length);
        } else {
            Arrays.fill(to, offset, offset + length, (byte) 0);
        }

        int startElementIndex = byteIndex / baseElementSize;
        int endElementIndex = (byteIndex + length) / baseElementSize;

        Int2IntSortedMap subMap = reverseIndices.subMap(startElementIndex, endElementIndex + 1);
        for (Int2IntMap.Entry entry : subMap.int2IntEntrySet()) {
            int key = entry.getIntKey();
            int value = entry.getIntValue();

            int replaceByteIndex = key * baseElementSize;
            int startByteIndex = Math.max(replaceByteIndex, byteIndex);
            int byteIndexOffset = startByteIndex - replaceByteIndex;
            int offsetOffset = startByteIndex - byteIndex;

            valuesView.copyBytes(value * baseElementSize + byteIndexOffset + valuesOffset, to, offset + offsetOffset,
                Math.min(baseElementSize, length - offsetOffset));
        }
    }

    @Override
    public int size() {
        return size;
    }
}
