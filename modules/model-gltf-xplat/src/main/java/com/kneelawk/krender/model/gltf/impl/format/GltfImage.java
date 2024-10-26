package com.kneelawk.krender.model.gltf.impl.format;

import java.util.Optional;
import java.util.OptionalInt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record GltfImage(Optional<String> uri, OptionalInt bufferView, Optional<String> mimeType) {
    public static final Codec<GltfImage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.optionalFieldOf("uri").forGetter(GltfImage::uri),
        Codecs.optionalInt(Codec.INT.optionalFieldOf("bufferView")).forGetter(GltfImage::bufferView),
        Codec.STRING.optionalFieldOf("mimeType").forGetter(GltfImage::mimeType)
    ).apply(instance, GltfImage::new));
}
