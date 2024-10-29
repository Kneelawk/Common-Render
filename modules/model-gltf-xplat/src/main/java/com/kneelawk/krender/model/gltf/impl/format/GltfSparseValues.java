package com.kneelawk.krender.model.gltf.impl.format;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record GltfSparseValues(int bufferView, long byteOffset) {
    public static final Codec<GltfSparseValues> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("bufferView").forGetter(GltfSparseValues::bufferView),
        Codec.LONG.fieldOf("byteOffset").forGetter(GltfSparseValues::byteOffset)
    ).apply(instance, GltfSparseValues::new));
}
