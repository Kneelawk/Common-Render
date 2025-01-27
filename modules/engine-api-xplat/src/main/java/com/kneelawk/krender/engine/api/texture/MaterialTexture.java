package com.kneelawk.krender.engine.api.texture;

import net.minecraft.resources.ResourceLocation;

import com.kneelawk.krender.engine.api.RendererDependent;

/**
 * A texture managed by a KRender backend for use in a {@link com.kneelawk.krender.engine.api.material.MaterialView}.
 */
public interface MaterialTexture extends RendererDependent {
    /**
     * {@return an integer id of this texture}
     * <p>
     * This is only guaranteed to be the same for a single runtime. Textures' integer ids are likely to change across
     * restarts.
     */
    int intId();

    /**
     * {@return this material texture's id}
     * <p>
     * This material texture's id is the same as the id of the minecraft texture this material texture represents if any.
     */
    ResourceLocation id();
}
