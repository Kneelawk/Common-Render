package com.kneelawk.krender.engine.base.material;

import com.kneelawk.krender.engine.api.material.BlendMode;
import com.kneelawk.krender.engine.api.material.GlintMode;
import com.kneelawk.krender.engine.api.material.RenderMaterial;
import com.kneelawk.krender.engine.api.util.TriState;

/**
 * Base {@link RenderMaterial} implementation.
 */
public abstract class BaseRenderMaterial extends BaseMaterialView implements RenderMaterial {
    /**
     * This material's integer id.
     */
    protected final int intId;

    /**
     * This material's blend mode.
     */
    protected final BlendMode blendMode;

    /**
     * Whether this material is emissive.
     */
    protected final boolean emissive;

    /**
     * Whether this material has diffuse lighting disabled.
     */
    protected final boolean diffuseDisabled;

    /**
     * This material's ambient occlusion mode.
     */
    protected final TriState ambientOcclusionMode;

    /**
     * This material's glint mode.
     */
    protected final GlintMode glintMode;

    /**
     * The integer id of this material's texture.
     */
    protected final int textureIntId;

    /**
     * Creates a new {@link BaseRenderMaterial} with the given bits.
     *
     * @param finder the material finder used to create this material.
     */
    public BaseRenderMaterial(BaseMaterialView finder, int intId) {
        super(finder.bits, finder.name);
        this.intId = intId;

        blendMode = finder.getBlendMode();
        emissive = finder.isEmissive();
        diffuseDisabled = finder.isDiffuseDisabled();
        ambientOcclusionMode = finder.getAmbientOcclusionMode();
        glintMode = finder.getGlintMode();
        textureIntId = finder.getTextureIntId();
    }

    @Override
    public int intId() {
        return intId;
    }

    @Override
    public BlendMode getBlendMode() {
        return blendMode;
    }

    @Override
    public boolean isEmissive() {
        return emissive;
    }

    @Override
    public boolean isDiffuseDisabled() {
        return super.isDiffuseDisabled();
    }

    @Override
    public TriState getAmbientOcclusionMode() {
        return ambientOcclusionMode;
    }

    @Override
    public GlintMode getGlintMode() {
        return glintMode;
    }

    @Override
    public int getTextureIntId() {
        return textureIntId;
    }
}
