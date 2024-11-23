package com.kneelawk.krender.model.obj.impl.format.metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.gson.JsonObject;

import org.joml.Matrix4f;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.world.phys.Vec3;

public record ObjMetadata(Vec3 translation, Vec3 rotation, Vec3 scale, List<MetadataTransform> transforms,
                          Matrix4f matrix, MaterialOverride materialOverride,
                          Map<String, MaterialOverride> materialOverrides,
                          boolean useAmbientOcclusion, boolean gui3d,
                          Optional<ResourceLocation> particle,
                          float transformGranularity) {

    private static final Codec<Matrix4f> MATRIX_CODEC =
        Codec.FLOAT.listOf().comapFlatMap(list -> Util.fixedSize(list, 16).map(listx -> {
            float[] floats = new float[16];
            for (int i = 0; i < 16; i++) {
                floats[i] = listx.get(i);
            }
            return new Matrix4f().set(floats);
        }), matrix -> {
            ArrayList<Float> floatList = new ArrayList<>(16);
            float[] floatArray = new float[16];
            matrix.get(floatArray);
            for (int i = 0; i < 16; i++) {
                floatList.add(floatArray[i]);
            }
            return floatList;
        });

    public static final Codec<ObjMetadata> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Vec3.CODEC.optionalFieldOf("translation", Vec3.ZERO).forGetter(ObjMetadata::translation),
        Vec3.CODEC.optionalFieldOf("rotation", Vec3.ZERO).forGetter(ObjMetadata::rotation),
        ScaleTransform.SCALE_CODEC.optionalFieldOf("scale", new Vec3(1.0, 1.0, 1.0)).forGetter(ObjMetadata::scale),
        MetadataTransform.CODEC.listOf().optionalFieldOf("transforms", List.of()).forGetter(ObjMetadata::transforms),
        MATRIX_CODEC.optionalFieldOf("matrix", new Matrix4f()).forGetter(ObjMetadata::matrix),
        MaterialOverride.CODEC.optionalFieldOf("materialOverride", MaterialOverride.DEFAULT)
            .forGetter(ObjMetadata::materialOverride),
        Codec.unboundedMap(Codec.STRING, MaterialOverride.CODEC).optionalFieldOf("materialOverrides", Map.of())
            .forGetter(ObjMetadata::materialOverrides),
        Codec.BOOL.optionalFieldOf("useAmbientOcclusion", true).forGetter(ObjMetadata::useAmbientOcclusion),
        Codec.BOOL.optionalFieldOf("gui3d", true).forGetter(ObjMetadata::gui3d),
        ResourceLocation.CODEC.optionalFieldOf("particle").forGetter(ObjMetadata::particle),
        Codec.FLOAT.optionalFieldOf("transformGranularity", 0f).forGetter(ObjMetadata::transformGranularity)
    ).apply(instance, ObjMetadata::new));
    public static final ObjMetadata DEFAULT =
        new ObjMetadata(Vec3.ZERO, Vec3.ZERO, new Vec3(1.0, 1.0, 1.0), List.of(), new Matrix4f(),
            MaterialOverride.DEFAULT, Map.of(), true, true, Optional.empty(), 0f);

    public void transformMatrix(Matrix4f matrix) {
        matrix.translate(translation.toVector3f());
        matrix.rotate((float) (rotation.z * Math.PI / 180.0), 0f, 0f, 1f)
            .rotate((float) (rotation.y * Math.PI / 180.0), 0f, 1f, 0f)
            .rotate((float) (rotation.x * Math.PI / 180.0), 1f, 0f, 0f);
        matrix.scale(scale.toVector3f());

        for (MetadataTransform transform : transforms) {
            transform.transform(matrix);
        }

        matrix.mul(this.matrix);
    }

    public MaterialOverride getMaterial(String key, MaterialOverride old) {
        if (materialOverrides.containsKey(key)) {
            return materialOverrides.get(key).overlay(old);
        }
        return old;
    }

    public static final class Serializer implements MetadataSectionSerializer<ObjMetadata> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public String getMetadataSectionName() {
            return "krender:obj";
        }

        @Override
        public ObjMetadata fromJson(JsonObject json) {
            return CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
        }
    }
}
