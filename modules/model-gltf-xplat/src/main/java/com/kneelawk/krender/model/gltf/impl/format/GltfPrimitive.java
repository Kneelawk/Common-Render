package com.kneelawk.krender.model.gltf.impl.format;

import java.util.Map;
import java.util.OptionalInt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record GltfPrimitive(Map<String, Integer> attributes, OptionalInt indices, OptionalInt material, OptionalInt mode) {
    public static final Codec<GltfPrimitive> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("attributes").forGetter(GltfPrimitive::attributes),
        Codecs.optionalInt(Codec.INT.optionalFieldOf("indices")).forGetter(GltfPrimitive::indices),
        Codecs.optionalInt(Codec.INT.optionalFieldOf("material")).forGetter(GltfPrimitive::material),
        Codecs.optionalInt(Codec.INT.optionalFieldOf("mode")).forGetter(GltfPrimitive::mode)
    ).apply(instance, GltfPrimitive::new));
}
