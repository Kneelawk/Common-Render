package com.kneelawk.krender.model.gltf.impl.format;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record GltfAccessorSparse(GltfSparseIndices indices, GltfSparseValues values) {
    public static final Codec<GltfAccessorSparse> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        GltfSparseIndices.CODEC.fieldOf("indices").forGetter(GltfAccessorSparse::indices),
        GltfSparseValues.CODEC.fieldOf("values").forGetter(GltfAccessorSparse::values)
    ).apply(instance, GltfAccessorSparse::new));
}
