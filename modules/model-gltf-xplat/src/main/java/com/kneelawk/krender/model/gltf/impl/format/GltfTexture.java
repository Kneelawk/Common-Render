package com.kneelawk.krender.model.gltf.impl.format;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record GltfTexture(int sampler, int source) {
    public static final Codec<GltfTexture> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("sampler").forGetter(GltfTexture::sampler),
        Codec.INT.fieldOf("source").forGetter(GltfTexture::source)
    ).apply(instance, GltfTexture::new));
}
