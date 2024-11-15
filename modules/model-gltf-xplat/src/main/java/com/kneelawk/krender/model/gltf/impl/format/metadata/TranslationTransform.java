package com.kneelawk.krender.model.gltf.impl.format.metadata;

import org.joml.Matrix4f;

import com.mojang.serialization.MapCodec;

import net.minecraft.world.phys.Vec3;

public record TranslationTransform(Vec3 translation) implements MetadataTransform {
    public static final MapCodec<TranslationTransform> MAP_CODEC =
        Vec3.CODEC.fieldOf("translation").xmap(TranslationTransform::new, TranslationTransform::translation);

    @Override
    public MapCodec<? extends MetadataTransform> getCodec() {
        return MAP_CODEC;
    }

    @Override
    public void transform(Matrix4f matrix) {
        matrix.translate(translation.toVector3f());
    }
}
