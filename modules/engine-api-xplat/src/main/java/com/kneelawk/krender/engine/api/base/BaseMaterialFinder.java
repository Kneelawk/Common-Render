package com.kneelawk.krender.engine.api.base;

import com.kneelawk.krender.engine.api.TriState;
import com.kneelawk.krender.engine.api.material.BlendMode;
import com.kneelawk.krender.engine.api.material.MaterialFinder;
import com.kneelawk.krender.engine.api.material.MaterialView;

/**
 * Base implementation of {@link MaterialFinder} that can be used for platforms that don't have an existing implementation.
 */
public abstract class BaseMaterialFinder extends BaseMaterialView implements MaterialFinder {
    private final int defaultBits;

    /**
     * Creates a new {@link BaseMaterialFinder} with the given default bits.
     *
     * @param defaultBits the default bits for the new material finder.
     */
    public BaseMaterialFinder(int defaultBits) {
        super(defaultBits);
        this.defaultBits = defaultBits;
    }

    @Override
    public MaterialFinder clear() {
        bits = defaultBits;
        return this;
    }

    @Override
    public MaterialFinder setBlendMode(BlendMode blendMode) {
        bits = (bits & ~BLEND_MODE_MASK) | (blendMode.ordinal() << BLEND_MODE_BIT_OFFSET);
        return this;
    }

    @Override
    public MaterialFinder setColorIndexDisabled(boolean disabled) {
        bits = disabled ? (bits | COLOR_INDEX_FLAG) : (bits & ~COLOR_INDEX_FLAG);
        return this;
    }

    @Override
    public MaterialFinder setEmissive(boolean emissive) {
        bits = emissive ? (bits | EMISSIVE_FLAG) : (bits & ~EMISSIVE_FLAG);
        return this;
    }

    @Override
    public MaterialFinder setDiffuseDisabled(boolean disabled) {
        bits = disabled ? (bits | DIFFUSE_FLAG) : (bits & ~DIFFUSE_FLAG);
        return this;
    }

    @Override
    public MaterialFinder setAmbientOcclusionMode(TriState mode) {
        bits = (bits & ~AO_MASK) | (mode.ordinal() << AO_BIT_OFFSET);
        return this;
    }

    @Override
    public MaterialFinder copyFrom(MaterialView material) {
        if (material instanceof BaseMaterialView view) {
            bits = view.bits;
        } else {
            setBlendMode(material.getBlendMode()).setColorIndexDisabled(material.isColorIndexDisabled())
                .setEmissive(material.isEmissive()).setDiffuseDisabled(material.isDiffuseDisabled())
                .setAmbientOcclusionMode(material.getAmbientOcclusionMode());
        }
        return this;
    }
}
