package com.kneelawk.krender.model.gltf.impl.format;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;

import com.kneelawk.krender.engine.api.material.BlendMode;

public enum GltfMaterialAlphaMode implements StringRepresentable {
    OPAQUE("OPAQUE", BlendMode.SOLID),
    MASK("MASK", BlendMode.CUTOUT_MIPPED),
    BLEND("BLEND", BlendMode.TRANSLUCENT);
    
    public static final Codec<GltfMaterialAlphaMode> CODEC = StringRepresentable.fromEnum(GltfMaterialAlphaMode::values);

    private final String serializedName;
    private final BlendMode blendMode;

    GltfMaterialAlphaMode(String serializedName, BlendMode blendMode) {
        this.serializedName = serializedName;
        this.blendMode = blendMode;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }

    public BlendMode getBlendMode() {
        return blendMode;
    }
}
