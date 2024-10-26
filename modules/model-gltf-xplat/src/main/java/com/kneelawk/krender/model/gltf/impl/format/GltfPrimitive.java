package com.kneelawk.krender.model.gltf.impl.format;

import java.util.Map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record GltfPrimitive(Map<String, Integer> attributes, int indices, int material, int mode) {
    public static final Codec<GltfPrimitive> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("attributes").forGetter(GltfPrimitive::attributes),
        Codec.INT.fieldOf("indices").forGetter(GltfPrimitive::indices),
        Codec.INT.fieldOf("material").forGetter(GltfPrimitive::material),
        Codec.INT.fieldOf("mode").forGetter(GltfPrimitive::mode)
    ).apply(instance, GltfPrimitive::new));
}
