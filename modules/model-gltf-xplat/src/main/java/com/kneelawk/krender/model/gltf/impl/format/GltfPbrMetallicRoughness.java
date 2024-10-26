package com.kneelawk.krender.model.gltf.impl.format;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record GltfPbrMetallicRoughness(float[] baseColorFactor, Optional<GltfTextureRef> baseColorTexture) {
    public static final Codec<GltfPbrMetallicRoughness> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codecs.FLOAT_ARRAY.optionalFieldOf("baseColorFactor", new float[0])
            .forGetter(GltfPbrMetallicRoughness::baseColorFactor),
        GltfTextureRef.CODEC.optionalFieldOf("baseColorTexture").forGetter(GltfPbrMetallicRoughness::baseColorTexture)
    ).apply(instance, GltfPbrMetallicRoughness::new));
}
