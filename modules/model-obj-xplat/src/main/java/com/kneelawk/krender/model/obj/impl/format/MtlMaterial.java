package com.kneelawk.krender.model.obj.impl.format;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;

public record MtlMaterial(String name, float[] diffuseColor, float dissolve, @Nullable ResourceLocation diffuseTexture,
                          boolean emissive) {
    public MtlMaterial(String name) {
        this(name, new float[0], 1f, null, false);
    }

    public MtlMaterial withDiffuseColor(float[] diffuseColor) {
        return new MtlMaterial(name, diffuseColor, dissolve, diffuseTexture, emissive);
    }

    public MtlMaterial withDissolve(float dissolve) {
        return new MtlMaterial(name, diffuseColor, dissolve, diffuseTexture, emissive);
    }

    public MtlMaterial withDiffuseTexture(ResourceLocation diffuseTexture) {
        return new MtlMaterial(name, diffuseColor, dissolve, diffuseTexture, emissive);
    }

    public MtlMaterial withEmissive(boolean emissive) {
        return new MtlMaterial(name, diffuseColor, dissolve, diffuseTexture, emissive);
    }
}
