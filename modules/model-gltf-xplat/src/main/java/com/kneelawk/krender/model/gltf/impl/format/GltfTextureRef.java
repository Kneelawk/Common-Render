package com.kneelawk.krender.model.gltf.impl.format;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record GltfTextureRef(int index, int texCoord) {
    public static final Codec<GltfTextureRef> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("index").forGetter(GltfTextureRef::index),
        Codec.INT.fieldOf("texCoord").forGetter(GltfTextureRef::texCoord)
    ).apply(instance, GltfTextureRef::new));
}
