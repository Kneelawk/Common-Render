package com.kneelawk.krender.model.gltf.impl.format;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record GltfAccessor(int bufferView, long byteOffset, GltfAccessorComponentType componentType, long count,
                           GltfAccessorType type, Optional<GltfAccessorSparse> sparse) {
    public static final Codec<GltfAccessor> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("bufferView").forGetter(GltfAccessor::bufferView),
        Codec.LONG.fieldOf("byteOffset").forGetter(GltfAccessor::byteOffset),
        GltfAccessorComponentType.CODEC.fieldOf("componentType").forGetter(GltfAccessor::componentType),
        Codec.LONG.fieldOf("count").forGetter(GltfAccessor::count),
        GltfAccessorType.CODEC.fieldOf("type").forGetter(GltfAccessor::type),
        GltfAccessorSparse.CODEC.optionalFieldOf("sparse").forGetter(GltfAccessor::sparse)
    ).apply(instance, GltfAccessor::new));
}
