package com.kneelawk.krender.model.gltf.impl.format;

import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;

public enum GltfAccessorType implements StringRepresentable {
    SCALAR("SCALAR", 1, Type.SCALAR),
    VEC2("VEC2", 2, Type.VEC),
    VEC3("VEC3", 3, Type.VEC),
    VEC4("VEC4", 4, Type.VEC),
    MAT2("MAT2", 4, Type.MAT),
    MAT3("MAT3", 9, Type.MAT),
    MAT4("MAT4", 16, Type.MAT);

    public static final Codec<GltfAccessorType> CODEC = StringRepresentable.fromEnum(GltfAccessorType::values);

    private final String serializedName;
    private final int componentCount;
    private final Type type;

    GltfAccessorType(String serializedName, int componentCount, Type type) {
        this.serializedName = serializedName;
        this.componentCount = componentCount;
        this.type = type;
    }

    public int getComponentCount() {
        return componentCount;
    }

    @Override
    public @NotNull String getSerializedName() {
        return serializedName;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        SCALAR,
        VEC,
        MAT
    }
}
