package com.kneelawk.krender.engine.base.material;

import net.minecraft.client.renderer.RenderType;

import com.kneelawk.krender.engine.api.material.BlendMode;
import com.kneelawk.krender.engine.api.material.MaterialFinder;
import com.kneelawk.krender.engine.api.material.MaterialView;
import com.kneelawk.krender.engine.api.util.TriState;

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
        bits = format.blendMode.setI(bits, blendMode);
        return this;
    }

    @Override
    public MaterialFinder setEmissive(boolean emissive) {
        bits = format.emissive.setI(bits, emissive);
        return this;
    }

    @Override
    public MaterialFinder setDiffuseDisabled(boolean disabled) {
        bits = format.diffuseDisabled.setI(bits, disabled);
        return this;
    }

    @Override
    public MaterialFinder setAmbientOcclusionMode(TriState mode) {
        bits = format.ambientOcclusion.setI(bits, mode);
        return this;
    }

    @Override
    public MaterialFinder copyFrom(MaterialView material) {
        if (material instanceof BaseMaterialView view) {
            bits = view.bits;
        } else {
            setBlendMode(material.getBlendMode())
                .setEmissive(material.isEmissive()).setDiffuseDisabled(material.isDiffuseDisabled())
                .setAmbientOcclusionMode(material.getAmbientOcclusionMode());
        }
        return this;
    }

    @Override
    public MaterialFinder fromVanilla(RenderType type) {
        clear();

        // FIXME: only supports terrain render types
        if (type == RenderType.solid()) {
            setBlendMode(BlendMode.SOLID);
        } else if (type == RenderType.cutout()) {
            setBlendMode(BlendMode.CUTOUT);
        } else if (type == RenderType.cutoutMipped()) {
            setBlendMode(BlendMode.CUTOUT_MIPPED);
        } else if (type == RenderType.translucent()) {
            setBlendMode(BlendMode.TRANSLUCENT);
        }

        return this;
    }
}
