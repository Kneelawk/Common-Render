package com.kneelawk.krender.model.gltf.impl.mixin.impl;

import com.google.common.collect.BiMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.resources.ResourceLocation;

@Mixin(SpriteSources.class)
public interface Accessor_SpriteSources {
    @Accessor("TYPES")
    static BiMap<ResourceLocation, SpriteSourceType> krender$types() {
        throw new AssertionError("mixin did not inject!");
    }
}
