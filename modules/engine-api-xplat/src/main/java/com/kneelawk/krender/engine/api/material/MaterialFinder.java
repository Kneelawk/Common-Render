package com.kneelawk.krender.engine.api.material;

import net.minecraft.client.renderer.RenderType;

import com.kneelawk.krender.engine.api.texture.MaterialTexture;
import com.kneelawk.krender.engine.api.texture.MaterialTextureManager;
import com.kneelawk.krender.engine.api.util.TriState;

/**
 * Used for finding/building materials.
 */
public interface MaterialFinder extends MaterialView {
    /**
     * Gets or creates the render material specified by this material finder.
     *
     * @return the requested material.
     */
    RenderMaterial find();

    /**
     * Resets this material finder back to its default values.
     *
     * @return this material finder.
     */
    MaterialFinder clear();

    /**
     * Sets this material's blend mode.
     *
     * @param blendMode the new blend mode.
     * @return this material finder.
     */
    MaterialFinder setBlendMode(BlendMode blendMode);

    /**
     * Sets whether this material is emissive. This causes it to ignore lighting values.
     *
     * @param emissive the emissive value.
     * @return this material finder.
     */
    MaterialFinder setEmissive(boolean emissive);

    /**
     * Sets whether diffuse shading is disabled.
     *
     * @param disabled whether to disable diffuse shading.
     * @return this material finder.
     */
    MaterialFinder setDiffuseDisabled(boolean disabled);

    /**
     * Sets whether ambient occlusion is force enabled, disabled, or left up to the model.
     * <p>
     * Note: not all backends may respect this value, or some may only respect a {@code FALSE} value, treating a
     * {@code TRUE} value the same as a {@code DEFAULT} value.
     *
     * @param mode whether ambient occlusion is force enabled, disabled, or left up to the model.
     * @return this material finder.
     */
    MaterialFinder setAmbientOcclusionMode(TriState mode);

    /**
     * Sets the kind of glint used by this material.
     * <p>
     * This is usually not supported on terrain rendering on most backends.
     *
     * @param glintMode the kind of glint to be used by this material.
     * @return this material finder.
     */
    MaterialFinder setGlintMode(GlintMode glintMode);

    /**
     * Sets the texture or texture atlas to be used on quads rendered with this material, looking up by integer id.
     * <p>
     * When rendering terrain, most backends only support the {@link MaterialTextureManager#blockAtlas()} texture.
     *
     * @param textureIntId the integer id of the texture to be associated with this material.
     * @return this material finder.
     */
    MaterialFinder setTextureIntId(int textureIntId);

    /**
     * Sets the texture or texture atlas to be used on quads rendered with this material.
     * <p>
     * When rendering terrain, most backends only support the {@link MaterialTextureManager#blockAtlas()} texture.
     *
     * @param texture the texture to be associated with this material.
     * @return this material finder.
     */
    default MaterialFinder setTexture(MaterialTexture texture) {
        return setTextureIntId(texture.intId());
    }

    /**
     * Sets this material's name.
     * <p>
     * Note: material names are not considered when checking material equality and should only be used for debugging.
     * A material may display a different name than the one it was created with if it was first created with the
     * different name.
     *
     * @param name the name for this material.
     * @return this material finder.
     */
    MaterialFinder setName(String name);

    /**
     * Copies all properties from the given material.
     *
     * @param material the material to copy from.
     * @return this material finder.
     */
    MaterialFinder copyFrom(MaterialView material);

    /**
     * Makes a best-effort attempt to copy material information from a {@link RenderType}.
     *
     * @param type the render type to copy material information from.
     * @return this material finder.
     */
    MaterialFinder fromVanilla(RenderType type);
}
