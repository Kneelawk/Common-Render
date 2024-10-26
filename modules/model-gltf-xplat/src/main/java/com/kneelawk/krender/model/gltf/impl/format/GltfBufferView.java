package com.kneelawk.krender.model.gltf.impl.format;

import java.util.OptionalInt;
import java.util.OptionalLong;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record GltfBufferView(int buffer, long byteLength, long byteOffset, OptionalLong byteStride,
                             OptionalInt target) {
    public static final Codec<GltfBufferView> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("buffer").forGetter(GltfBufferView::buffer),
        Codec.LONG.fieldOf("byteLength").forGetter(GltfBufferView::byteLength),
        Codec.LONG.fieldOf("byteOffset").forGetter(GltfBufferView::byteOffset),
        Codecs.optionalLong(Codec.LONG.optionalFieldOf("byteStride")).forGetter(GltfBufferView::byteStride),
        Codecs.optionalInt(Codec.INT.optionalFieldOf("target")).forGetter(GltfBufferView::target)
    ).apply(instance, GltfBufferView::new));
}
