package com.kneelawk.krender.engine.api.material;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.RenderType;

import com.kneelawk.krender.engine.api.RendererDependent;
import com.kneelawk.krender.engine.api.util.TriState;

/**
 * Provides access to the values of a material.
 * <p>
 * Values returned may not match those set, but should be more accurate to the material actually rendered.
 */
public interface MaterialView extends RendererDependent {
    /**
     * {@return this material's blend mode}
     */
    BlendMode getBlendMode();

    /**
     * {@return whether this material is emissive}
     * <p>
     * Emissive materials always render at full-brightness, ignoring provided lightmap values.
     */
    boolean isEmissive();

    /**
     * {@return whether diffuse shading is disabled for this material}
     */
    boolean isDiffuseDisabled();

    /**
     * {@return whether ambient occlusion is enabled}
     */
    TriState getAmbientOcclusionMode();

    /**
     * Makes a best-effort attempt to convert this material view to a {@link RenderType} suitable for rendering terrain.
     *
     * @return the vanilla render type closest to this material if any.
     */
    @Nullable RenderType toVanillaBlock();

    /**
     * Makes a best-effort attempt to convert this material view into a {@link RenderType} suitable for rendering items.
     *
     * @return the vanilla render type closest to this material if any.
     */
    @Nullable RenderType toVanillaItem();

    /**
     * Makes a best-effort attempt to convert this material view into a {@link RenderType} suitable for rendering entities.
     *
     * @return the vanilla render type closest to this material if any.
     */
    @Nullable RenderType toVanillaEntity();
}
