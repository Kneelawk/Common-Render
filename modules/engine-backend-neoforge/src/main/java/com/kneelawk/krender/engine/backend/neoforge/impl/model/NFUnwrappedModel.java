package com.kneelawk.krender.engine.backend.neoforge.impl.model;

import net.neoforged.neoforge.client.model.data.ModelData;

import org.jetbrains.annotations.UnknownNullability;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import com.kneelawk.krender.engine.api.buffer.QuadEmitter;
import com.kneelawk.krender.engine.api.material.MaterialFinder;
import com.kneelawk.krender.engine.api.material.RenderMaterial;
import com.kneelawk.krender.engine.api.model.BakedModelCore;
import com.kneelawk.krender.engine.api.model.ModelBlockContext;
import com.kneelawk.krender.engine.api.model.ModelItemContext;
import com.kneelawk.krender.engine.api.util.DirectionIds;

public class NFUnwrappedModel implements BakedModelCore<NFUnwrappedModel.Storage> {
    private final BakedModel model;

    public NFUnwrappedModel(BakedModel model) {this.model = model;}

    @Override
    public boolean useAmbientOcclusion() {
        return model.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return model.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return model.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return model.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return model.getParticleIcon(ModelData.EMPTY);
    }

    @Override
    public ItemTransforms getTransforms() {
        return model.getTransforms();
    }

    @Override
    public ItemOverrides getOverrides() {
        return model.getOverrides();
    }

    @Override
    public NFUnwrappedModel.@UnknownNullability Storage getBlockKey(ModelBlockContext ctx) {
        return new Storage(ctx.state(), model.getModelData(ctx.level(), ctx.pos(), ctx.state(), ModelData.EMPTY));
    }

    @Override
    public void renderBlock(QuadEmitter emitter, @UnknownNullability Storage storage) {
        final RandomSource random = RandomSource.create(42);
        final MaterialFinder finder = emitter.getRendererOrDefault().materialManager().materialFinder();

        for (RenderType renderType : model.getRenderTypes(storage.state(), random, storage.data())) {
            RenderMaterial material = finder.clear().fromVanilla(renderType).find();

            for (int i = 0; i < DirectionIds.DIRECTION_COUNT; i++) {
                Direction face = DirectionIds.idToDirection(i);

                for (BakedQuad quad : model.getQuads(storage.state(), face, random, storage.data(), renderType)) {
                    emitter.fromVanilla(quad, material, face);
                    emitter.emit();
                }
            }
        }
    }

    @Override
    public void renderItem(QuadEmitter emitter, ModelItemContext ctx) {
        final MaterialFinder finder = emitter.getRendererOrDefault().materialManager().materialFinder();

        for (BakedModel subModel : model.getRenderPasses(ctx.stack(), true)) {
            for (RenderType renderType : subModel.getRenderTypes(ctx.stack(), true)) {
                RenderMaterial material = finder.clear().fromVanilla(renderType).find();

                for (int i = 0; i < DirectionIds.DIRECTION_COUNT; i++) {
                    Direction face = DirectionIds.idToDirection(i);

                    // it's deprecated, but apparently it's still used by ItemRenderer???
                    for (BakedQuad quad : model.getQuads(null, face, ctx.randomSupplier().get())) {
                        emitter.fromVanilla(quad, material, face);
                        emitter.emit();
                    }
                }
            }
        }
    }

    public record Storage(BlockState state, ModelData data) {}
}
