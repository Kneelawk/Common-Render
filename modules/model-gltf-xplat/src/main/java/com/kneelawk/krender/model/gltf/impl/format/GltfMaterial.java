package com.kneelawk.krender.model.gltf.impl.format;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record GltfMaterial(Optional<String> name, Optional<GltfPbrMetallicRoughness> pbrMetallicRoughness) {
    public static final Codec<GltfMaterial> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.optionalFieldOf("name").forGetter(GltfMaterial::name),
        GltfPbrMetallicRoughness.CODEC.optionalFieldOf("pbrMetallicRoughness")
            .forGetter(GltfMaterial::pbrMetallicRoughness)
    ).apply(instance, GltfMaterial::new));
}
