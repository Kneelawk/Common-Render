package com.kneelawk.krender.model.gltf.impl.format;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record GltfSparseIndices(int bufferView, long byteOffset, GltfAccessorComponentType componentType) {
    public static final Codec<GltfSparseIndices> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("bufferView").forGetter(GltfSparseIndices::bufferView),
        Codec.LONG.fieldOf("byteOffset").forGetter(GltfSparseIndices::byteOffset),
        GltfAccessorComponentType.CODEC.fieldOf("componentType").forGetter(GltfSparseIndices::componentType)
    ).apply(instance, GltfSparseIndices::new));
}
