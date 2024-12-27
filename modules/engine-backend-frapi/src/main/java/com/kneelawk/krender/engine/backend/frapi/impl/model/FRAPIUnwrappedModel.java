package com.kneelawk.krender.engine.backend.frapi.impl.model;

import org.jetbrains.annotations.UnknownNullability;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;

import com.kneelawk.krender.engine.api.KRenderer;
import com.kneelawk.krender.engine.api.buffer.QuadEmitter;
import com.kneelawk.krender.engine.api.mesh.Mesh;
import com.kneelawk.krender.engine.api.mesh.MeshBuilder;
import com.kneelawk.krender.engine.api.model.BakedModelCore;
import com.kneelawk.krender.engine.api.model.ModelBlockContext;
import com.kneelawk.krender.engine.api.model.ModelItemContext;

public class FRAPIUnwrappedModel implements BakedModelCore<FRAPIUnwrappedModel.Quads> {
    private final BakedModel bakedModel;

    public FRAPIUnwrappedModel(BakedModel bakedModel) {this.bakedModel = bakedModel;}

    @Override
    public boolean useAmbientOcclusion() {
        return bakedModel.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return bakedModel.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return bakedModel.usesBlockLight();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return bakedModel.getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
        return bakedModel.getTransforms();
    }

    @Override
    public FRAPIUnwrappedModel.@UnknownNullability Quads getBlockKey(ModelBlockContext ctx) {
        MeshBuilder builder = KRenderer.getDefault().meshBuilder();
        bakedModel.emitBlockQuads(ctx.level(), ctx.state(), ctx.pos(), ctx.random(),
            new FRAPIBlockContext(ctx, builder.emitter()));
        return new Quads(builder.build());
    }

    @Override
    public void renderBlock(QuadEmitter renderTo, @UnknownNullability Quads blockKey) {
        blockKey.mesh().outputTo(renderTo);
    }

    @Override
    public void renderItem(QuadEmitter renderTo, ModelItemContext ctx) {
        bakedModel.emitItemQuads(ctx.stack(), ctx.randomSupplier(), new FRAPIItemContext(ctx, renderTo));
    }

    public record Quads(Mesh mesh) {}
}
