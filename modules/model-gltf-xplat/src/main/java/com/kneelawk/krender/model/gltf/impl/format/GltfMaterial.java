package com.kneelawk.krender.model.gltf.impl.format;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record GltfMaterial(Optional<String> name, Optional<GltfPbrMetallicRoughness> pbrMetallicRoughness,
                           float[] emissiveFactor, Optional<GltfMaterialAlphaMode> alphaMode) {
    public static final Codec<GltfMaterial> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.optionalFieldOf("name").forGetter(GltfMaterial::name),
        GltfPbrMetallicRoughness.CODEC.optionalFieldOf("pbrMetallicRoughness")
            .forGetter(GltfMaterial::pbrMetallicRoughness),
        Codecs.FLOAT_ARRAY.optionalFieldOf("emissiveFactor", new float[0]).forGetter(GltfMaterial::emissiveFactor),
        GltfMaterialAlphaMode.CODEC.optionalFieldOf("alphaMode").forGetter(GltfMaterial::alphaMode)
    ).apply(instance, GltfMaterial::new));
}
