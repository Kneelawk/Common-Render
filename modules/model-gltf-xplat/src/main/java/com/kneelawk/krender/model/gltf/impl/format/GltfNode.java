package com.kneelawk.krender.model.gltf.impl.format;

import java.util.Optional;
import java.util.OptionalInt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record GltfNode(Optional<String> name, int[] children, float[] rotation, float[] scale, float[] translation,
                       float[] matrix, OptionalInt mesh) {
    public static final Codec<GltfNode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.optionalFieldOf("name").forGetter(GltfNode::name),
        Codecs.INT_ARRAY.optionalFieldOf("children", new int[0]).forGetter(GltfNode::children),
        Codecs.FLOAT_ARRAY.optionalFieldOf("rotation", new float[0]).forGetter(GltfNode::rotation),
        Codecs.FLOAT_ARRAY.optionalFieldOf("scale", new float[0]).forGetter(GltfNode::scale),
        Codecs.FLOAT_ARRAY.optionalFieldOf("translation", new float[0]).forGetter(GltfNode::translation),
        Codecs.FLOAT_ARRAY.optionalFieldOf("matrix", new float[0]).forGetter(GltfNode::matrix),
        Codecs.optionalInt(Codec.INT.optionalFieldOf("mesh")).forGetter(GltfNode::mesh)
    ).apply(instance, GltfNode::new));
}
