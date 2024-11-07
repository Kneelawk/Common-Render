package com.kneelawk.krender.model.gltf.impl;

import java.io.InputStream;

import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.ints.Int2IntRBTreeMap;
import it.unimi.dsi.fastutil.ints.Int2IntSortedMap;

import com.kneelawk.krender.model.gltf.impl.format.GltfAccessorComponentType;

public class SparseArrayBufferAccess implements BufferAccess {
    private final @Nullable BufferAccess base;
    private final int count;
    private final BufferAccess indicesView;
    private final int indicesOffset;
    private final GltfAccessorComponentType indicesType;
    private final Int2IntSortedMap reverseIndices = new Int2IntRBTreeMap();
    private final BufferAccess valuesView;
    private final int valuesOffset;

    public SparseArrayBufferAccess(@Nullable BufferAccess base, GltfAccessorComponentType componentType, int count, BufferAccess indicesView, int indicesOffset,
                                   GltfAccessorComponentType indicesType, BufferAccess valuesView, int valuesOffset) {
        this.base = base;
        this.count = count;
        this.indicesView = indicesView;
        this.indicesOffset = indicesOffset;
        this.indicesType = indicesType;
        this.valuesView = valuesView;
        this.valuesOffset = valuesOffset;

        int size = indicesView.size();
        for (int i = 0; i < size; i++) {
            reverseIndices.put(indicesView.getInt(i, indicesType), i);
        }
    }

    @Override
    public byte get(int byteIndex) {
        // TODO: fix this
//        if (reverseIndices.containsKey(byteIndex)) {
//            return 
//        }
        return 0;
    }

    @Override
    public int size() {
        return base.size();
    }

    @Override
    public InputStream createStream() {
        return new BufferAccessInputStream(this);
    }
}
