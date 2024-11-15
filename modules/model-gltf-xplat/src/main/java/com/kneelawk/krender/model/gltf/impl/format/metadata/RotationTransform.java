package com.kneelawk.krender.model.gltf.impl.format.metadata;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import org.joml.Matrix4f;
import org.joml.Quaternionf;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.Util;
import net.minecraft.world.phys.Vec3;

public sealed interface RotationTransform extends MetadataTransform {
    MapCodec<RotationTransform> MAP_CODEC = Codec.mapEither(Quaternion.MAP_CODEC, Axis.MAP_CODEC)
        .xmap(either -> either.map(Function.identity(), Function.identity()), trans -> switch (trans) {
            case Axis axis -> Either.right(axis);
            case Quaternion quaternion -> Either.left(quaternion);
        });

    @Override
    default MapCodec<? extends MetadataTransform> getCodec() {
        return MAP_CODEC;
    }

    record Axis(Vec3 axis, float angle) implements RotationTransform {
        public static final MapCodec<Axis> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.either(Vec3.CODEC, Codec.STRING).comapFlatMap(either -> either.map(
                vec -> DataResult.success(vec.normalize()), str -> switch (str.toUpperCase(Locale.ROOT)) {
                    case "X", "+X" -> DataResult.success(new Vec3(1.0, 0.0, 0.0));
                    case "-X" -> DataResult.success(new Vec3(-1.0, 0.0, 0.0));
                    case "Y", "+Y" -> DataResult.success(new Vec3(0.0, 1.0, 0.0));
                    case "-Y" -> DataResult.success(new Vec3(0.0, -1.0, 0.0));
                    case "Z", "+Z" -> DataResult.success(new Vec3(0.0, 0.0, 1.0));
                    case "-Z" -> DataResult.success(new Vec3(0.0, 0.0, -1.0));
                    default -> DataResult.error(() -> "Unknown axis: " + str);
                }), vec -> {
                if (Math.abs(Math.abs(vec.x) - 1.0) < 0.0001 && Math.abs(vec.y) < 0.0001 && Math.abs(vec.z) < 0.0001) {
                    if (vec.x < 0) {
                        return Either.right("-X");
                    } else {
                        return Either.right("+X");
                    }
                }
                if (Math.abs(Math.abs(vec.y) - 1.0) < 0.0001 && Math.abs(vec.x) < 0.0001 && Math.abs(vec.z) < 0.0001) {
                    if (vec.y < 0) {
                        return Either.right("-Y");
                    } else {
                        return Either.right("+Y");
                    }
                }
                if (Math.abs(Math.abs(vec.z) - 1.0) < 0.0001 && Math.abs(vec.y) < 0.0001 && Math.abs(vec.x) < 0.0001) {
                    if (vec.z < 0) {
                        return Either.right("-Z");
                    } else {
                        return Either.right("+Z");
                    }
                }
                return Either.left(vec);
            }).fieldOf("axis").forGetter(Axis::axis),
            Codec.FLOAT.fieldOf("angle").forGetter(Axis::angle)
        ).apply(instance, Axis::new));

        @Override
        public void transform(Matrix4f matrix) {
            matrix.rotate(angle, axis.toVector3f());
        }
    }

    record Quaternion(Quaternionf quaternion) implements RotationTransform {
        public static final MapCodec<Quaternion> MAP_CODEC = Codec.FLOAT.listOf().comapFlatMap(
                list -> Util.fixedSize(list, 4)
                    .map(listx -> new Quaternionf(listx.get(0), listx.get(1), listx.get(2), listx.get(3))),
                quat -> List.of(quat.x, quat.y, quat.z, quat.w)).fieldOf("quaternion")
            .xmap(Quaternion::new, Quaternion::quaternion);

        @Override
        public void transform(Matrix4f matrix) {
            matrix.rotate(quaternion);
        }
    }
}
