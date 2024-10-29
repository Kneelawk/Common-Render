package com.kneelawk.krender.model.gltf.impl.format;

import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;

public enum GltfAccessorType implements StringRepresentable {
    SCALAR("SCALAR", 1),
    VEC2("VEC2", 2),
    VEC3("VEC3", 3),
    VEC4("VEC4", 4),
    MAT2("MAT2", 4),
    MAT3("MAT3", 9),
    MAT4("MAT4", 16);

    public static final Codec<GltfAccessorType> CODEC = StringRepresentable.fromEnum(GltfAccessorType::values);

    private final String serializedName;
    private final int componentCount;

    GltfAccessorType(String serializedName, int componentCount) {
        this.serializedName = serializedName;
        this.componentCount = componentCount;
    }

    public int getComponentCount() {
        return componentCount;
    }

    @Override
    public @NotNull String getSerializedName() {
        return serializedName;
    }
}
