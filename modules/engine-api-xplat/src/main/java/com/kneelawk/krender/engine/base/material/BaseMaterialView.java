package com.kneelawk.krender.engine.base.material;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.TextureAtlas;

import com.kneelawk.krender.engine.api.TriState;
import com.kneelawk.krender.engine.api.material.BlendMode;
import com.kneelawk.krender.engine.api.material.MaterialView;

/**
 * Base implementation of {@link MaterialView} that can be used for platforms that don't have an existing implementation.
 */
public abstract class BaseMaterialView implements MaterialView, BaseMaterialViewApi {
    /**
     * This material's bits.
     */
    protected int bits;

    /**
     * Creates a new {@link BaseMaterialView} with the given bits.
     *
     * @param bits the bits representing this material.
     */
    public BaseMaterialView(int bits) {
        this.bits = bits;
    }

    @Override
    public int getBits() {
        return bits;
    }

    @Override
    public BlendMode getBlendMode() {
        return BLEND_MODES[(bits & BLEND_MODE_MASK) >>> BLEND_MODE_BIT_OFFSET];
    }

    @Override
    public boolean isEmissive() {
        return (bits & EMISSIVE_FLAG) != 0;
    }

    @Override
    public boolean isDiffuseDisabled() {
        return (bits & DIFFUSE_FLAG) != 0;
    }

    @Override
    public TriState getAmbientOcclusionMode() {
        return TRI_STATES[(bits & AO_MASK) >>> AO_BIT_OFFSET];
    }

    @Override
    public @Nullable RenderType toVanillaBlock() {
        return getBlendMode().blockRenderType;
    }

    @Override
    public @Nullable RenderType toVanillaItem() {
        return switch (getBlendMode()) {
            case DEFAULT -> null;
            case SOLID -> Sheets.solidBlockSheet();
            case CUTOUT_MIPPED, CUTOUT -> Sheets.cutoutBlockSheet();
            case TRANSLUCENT -> Sheets.translucentItemSheet();
        };
    }

    @Override
    public @Nullable RenderType toVanillaEntity() {
        if (isEmissive()) return RenderType.entityTranslucentEmissive(TextureAtlas.LOCATION_BLOCKS);

        return switch (getBlendMode()) {
            case DEFAULT -> null;
            case SOLID -> RenderType.entitySolid(TextureAtlas.LOCATION_BLOCKS);
            case CUTOUT_MIPPED, CUTOUT -> RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS);
            case TRANSLUCENT -> RenderType.entityTranslucent(TextureAtlas.LOCATION_BLOCKS);
        };
    }
}
