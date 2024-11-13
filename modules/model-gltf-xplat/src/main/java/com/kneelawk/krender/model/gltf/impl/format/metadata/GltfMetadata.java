package com.kneelawk.krender.model.gltf.impl.format.metadata;

import com.google.gson.JsonObject;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.world.phys.Vec3;

public record GltfMetadata(Vec3 translation, Vec3 rotation, Vec3 scale) {
    public static final Codec<GltfMetadata> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Vec3.CODEC.optionalFieldOf("translation", Vec3.ZERO).forGetter(GltfMetadata::translation),
        Vec3.CODEC.optionalFieldOf("rotation", Vec3.ZERO).forGetter(GltfMetadata::rotation),
        Vec3.CODEC.optionalFieldOf("scale", new Vec3(1.0, 1.0, 1.0)).forGetter(GltfMetadata::scale)
    ).apply(instance, GltfMetadata::new));
    public static final GltfMetadata DEFAULT = new GltfMetadata(Vec3.ZERO, Vec3.ZERO, new Vec3(1.0, 1.0, 1.0));

    public static final class Serializer implements MetadataSectionSerializer<GltfMetadata> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public String getMetadataSectionName() {
            return "krender:gltf";
        }

        @Override
        public GltfMetadata fromJson(JsonObject json) {
            return CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
        }
    }
}
