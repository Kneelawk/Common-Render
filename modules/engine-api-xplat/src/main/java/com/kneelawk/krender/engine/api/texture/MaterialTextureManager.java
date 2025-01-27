package com.kneelawk.krender.engine.api.texture;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;

import com.kneelawk.krender.engine.api.RendererDependent;

/**
 * Manages textures, texture lookups, texture caching, and texture finding.
 */
public interface MaterialTextureManager extends RendererDependent {
    /**
     * Gets a material texture by its integer id if a material texture with that id exists.
     *
     * @param id the id of the texture to look up.
     * @return the material texture with the given integer id.
     * @throws IllegalStateException if the specified id does not exist.
     * @see MaterialTexture#intId()
     */
    MaterialTexture textureByIntId(int id);

    /**
     * {@return the maximum integer id that a material texture can have}
     */
    int maxIntId();

    /**
     * {@return the none texture, indicating no texture is to be rendered}
     */
    MaterialTexture none();

    /**
     * {@return the missing texture, indicating that a texture was requested that does not exist}
     */
    MaterialTexture missing();

    /**
     * {@return a material texture for the minecraft block atlas}
     * <p>
     * This is the default texture for most materials and is the only valid texture on most platforms when rendering
     * terrain.
     */
    MaterialTexture blockAtlas();

    /**
     * Looks up a possibly cached material texture for the minecraft texture with the given id.
     *
     * @param id the minecraft texture id.
     * @return the material texture corresponding to the minecraft texture with the given id, if one exists.
     */
    @Nullable MaterialTexture textureById(ResourceLocation id);
}
