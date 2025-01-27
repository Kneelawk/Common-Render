package com.kneelawk.krender.engine.api.material;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import com.kneelawk.krender.engine.api.RendererDependent;
import com.kneelawk.krender.engine.api.texture.MaterialTexture;
import com.kneelawk.krender.engine.api.util.TriState;
import com.kneelawk.krender.engine.impl.KREConstants;

/**
 * Provides access to the values of a material.
 * <p>
 * Values returned may not match those set, but should be more accurate to the material actually rendered.
 */
public interface MaterialView extends RendererDependent {
    /**
     * The id of the missing material.
     */
    ResourceLocation MISSING_ID = KREConstants.prl("missing");

    /**
     * The id of the default material.
     */
    ResourceLocation DEFAULT_ID = KREConstants.prl("default");

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
     * {@return the type of glint used by this material}
     */
    GlintMode getGlintMode();

    /**
     * {@return the integer id of the texture or texture atlas associated with this material}
     */
    int getTextureIntId();

    /**
     * {@return the texture or texture atlas associated with this material}
     */
    default MaterialTexture getTexture() {
        return getRendererOrDefault().textureManager().textureByIntId(getTextureIntId());
    }

    /**
     * {@return the name of this material}
     * <p>
     * Note: material names are not considered when checking material equality and should only be used for debugging.
     * A material may display a different name than the one it was created with if it was first created with the
     * different name.
     */
    String getName();

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
