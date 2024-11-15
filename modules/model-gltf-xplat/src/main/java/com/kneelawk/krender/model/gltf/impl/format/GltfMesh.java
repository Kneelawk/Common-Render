package com.kneelawk.krender.model.gltf.impl.format;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record GltfMesh(Optional<String> name, List<GltfPrimitive> primitives) {
    public static final Codec<GltfMesh> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.optionalFieldOf("name").forGetter(GltfMesh::name),
        GltfPrimitive.CODEC.listOf().fieldOf("primitives").forGetter(GltfMesh::primitives)
    ).apply(instance, GltfMesh::new));
}
