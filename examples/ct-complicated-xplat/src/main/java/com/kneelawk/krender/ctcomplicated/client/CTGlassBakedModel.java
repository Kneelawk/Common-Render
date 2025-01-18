package com.kneelawk.krender.ctcomplicated.client;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import com.kneelawk.krender.engine.api.buffer.QuadEmitter;
import com.kneelawk.krender.engine.api.material.RenderMaterial;
import com.kneelawk.krender.engine.api.model.BakedModelCore;
import com.kneelawk.krender.engine.api.model.ModelBlockContext;
import com.kneelawk.krender.engine.api.model.ModelItemContext;

public record CTGlassBakedModel(boolean doCorners, boolean interiorBorder, TextureAtlasSprite particle,
                                TextureAtlasSprite[] sprites, RenderMaterial material)
    implements BakedModelCore<CTUtils.Data> {

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return particle;
    }

    @Override
    public ItemTransforms getTransforms() {
        return ItemTransforms.NO_TRANSFORMS;
    }

    @Override
    public CTUtils.Data getBlockKey(ModelBlockContext ctx) {
        return CTUtils.getData(doCorners, interiorBorder, ctx);
    }

    @Override
    public void renderBlock(QuadEmitter renderTo, CTUtils.Data blockKey) {
        CTUtils.render(material, sprites, renderTo, blockKey);
    }

    @Override
    public void renderItem(QuadEmitter renderTo, ModelItemContext ctx) {
    }
}
