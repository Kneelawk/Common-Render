package com.kneelawk.krender.model.gltf.impl.format;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record GltfMesh(List<GltfPrimitive> primitives) {
    public static final Codec<GltfMesh> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        GltfPrimitive.CODEC.listOf().fieldOf("primitives").forGetter(GltfMesh::primitives)
    ).apply(instance, GltfMesh::new));
}
