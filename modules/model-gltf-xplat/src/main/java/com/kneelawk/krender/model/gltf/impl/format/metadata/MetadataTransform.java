package com.kneelawk.krender.model.gltf.impl.format.metadata;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import org.joml.Matrix4f;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.Util;

public interface MetadataTransform {
    BiMap<String, MapCodec<? extends MetadataTransform>> MAP = Util.make(HashBiMap.create(), map -> {
        map.put("translation", TranslationTransform.MAP_CODEC);
        map.put("rotation", RotationTransform.MAP_CODEC);
        map.put("scale", ScaleTransform.MAP_CODEC);
    });

    Codec<MetadataTransform> CODEC = Codec.STRING.dispatch(trans -> MAP.inverse().get(trans.getCodec()), MAP::get);

    MapCodec<? extends MetadataTransform> getCodec();

    void transform(Matrix4f matrix);
}
