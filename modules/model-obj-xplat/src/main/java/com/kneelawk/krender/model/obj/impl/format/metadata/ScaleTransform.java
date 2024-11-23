package com.kneelawk.krender.model.obj.impl.format.metadata;

import java.util.function.Function;

import org.joml.Matrix4f;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.world.phys.Vec3;

public record ScaleTransform(Vec3 scale) implements MetadataTransform {
    public static final Codec<Vec3> SCALE_CODEC = Codec.either(Vec3.CODEC, Codec.FLOAT)
        .xmap(either -> either.map(Function.identity(), f -> new Vec3(f, f, f)), vec -> {
            if (Math.abs(vec.x - vec.y) < 0.0001 && Math.abs(vec.x - vec.z) < 0.0001) {
                return Either.right((float) vec.x);
            } else {
                return Either.left(vec);
            }
        });

    public static final MapCodec<ScaleTransform> MAP_CODEC =
        SCALE_CODEC.xmap(ScaleTransform::new, ScaleTransform::scale).fieldOf("scale");

    @Override
    public MapCodec<? extends MetadataTransform> getCodec() {
        return MAP_CODEC;
    }

    @Override
    public void transform(Matrix4f matrix) {
        matrix.scale(scale.toVector3f());
    }
}
