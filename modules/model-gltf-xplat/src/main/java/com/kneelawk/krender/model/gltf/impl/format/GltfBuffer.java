package com.kneelawk.krender.model.gltf.impl.format;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record GltfBuffer(long byteLength, Optional<String> uri) {
    public static final Codec<GltfBuffer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.LONG.fieldOf("byteLength").forGetter(GltfBuffer::byteLength),
        Codec.STRING.optionalFieldOf("uri").forGetter(GltfBuffer::uri)
    ).apply(instance, GltfBuffer::new));
}
