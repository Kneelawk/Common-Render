package com.kneelawk.krender.engine.base.material;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.RenderType;

import com.kneelawk.krender.engine.api.material.BlendMode;
import com.kneelawk.krender.engine.api.material.GlintMode;
import com.kneelawk.krender.engine.api.material.MaterialView;
import com.kneelawk.krender.engine.api.util.TriState;

/**
 * Base implementation of {@link MaterialView} that can be used for platforms that don't have an existing implementation.
 */
public abstract class BaseMaterialView implements MaterialView {
    /**
     * This material's bits.
     */
    protected int bits;

    /**
     * This material's name.
     */
    protected String name;

    /**
     * Creates a new {@link BaseMaterialView} with the given bits.
     *
     * @param bits the bits representing this material.
     */
    public BaseMaterialView(int bits, String name) {
        this.bits = bits;
        this.name = name;
    }

    /**
     * {@return this material view's material format}
     */
    protected BaseMaterialFormat format() {
        return BaseMaterialFormat.get(getRendererOrDefault());
    }

    @Override
    public BlendMode getBlendMode() {
        return format().blendMode.getI(bits);
    }

    @Override
    public boolean isEmissive() {
        return format().emissive.getI(bits);
    }

    @Override
    public boolean isDiffuseDisabled() {
        return format().diffuseDisabled.getI(bits);
    }

    @Override
    public TriState getAmbientOcclusionMode() {
        return format().ambientOcclusion.getI(bits);
    }

    @Override
    public GlintMode getGlintMode() {
        return format().glintMode.getI(bits);
    }

    @Override
    public int getTextureIntId() {
        return format().texture.getI(bits);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public @Nullable RenderType toVanillaBlock() {
        return getBlendMode().blockRenderType;
    }

    @Override
    public @Nullable RenderType toVanillaItem() {
        if (isEmissive()) return RenderType.entityTranslucentEmissive(getTexture().id());

        return switch (getBlendMode()) {
            case DEFAULT -> null;
            case SOLID -> RenderType.entitySolid(getTexture().id());
            case CUTOUT_MIPPED, CUTOUT -> RenderType.entityCutout(getTexture().id());
            case TRANSLUCENT -> RenderType.itemEntityTranslucentCull(getTexture().id());
        };
    }

    @Override
    public @Nullable RenderType toVanillaEntity() {
        if (isEmissive()) return RenderType.entityTranslucentEmissive(getTexture().id());

        return switch (getBlendMode()) {
            case DEFAULT -> null;
            case SOLID -> RenderType.entitySolid(getTexture().id());
            case CUTOUT_MIPPED, CUTOUT -> RenderType.entityCutoutNoCull(getTexture().id());
            case TRANSLUCENT -> RenderType.entityTranslucent(getTexture().id());
        };
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof BaseMaterialView that)) return false;

        return bits == that.bits;
    }

    @Override
    public int hashCode() {
        return bits;
    }
}
