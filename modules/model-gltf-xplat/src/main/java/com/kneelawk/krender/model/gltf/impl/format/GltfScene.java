package com.kneelawk.krender.model.gltf.impl.format;

import java.util.Optional;
import java.util.stream.IntStream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record GltfScene(Optional<String> name, int[] nodes) {
    public static final Codec<GltfScene> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.optionalFieldOf("name").forGetter(GltfScene::name),
        Codec.INT_STREAM.xmap(IntStream::toArray, IntStream::of).fieldOf("nodes").forGetter(GltfScene::nodes)
    ).apply(instance, GltfScene::new));
}
