package com.kneelawk.krender.engine.backend.frapi.impl.model;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import com.kneelawk.krender.engine.api.data.DataHolder;
import com.kneelawk.krender.engine.api.model.BakedModelCore;
import com.kneelawk.krender.engine.api.model.ModelBlockContext;
import com.kneelawk.krender.engine.api.model.ModelItemContext;
import com.kneelawk.krender.engine.backend.frapi.impl.buffer.FRAPIQuadEmitter;
import com.kneelawk.krender.engine.base.model.BakedModelCoreProvider;

/**
 * Basic non-caching baked model impl.
 */
public class FRAPIBakedModelImpl implements BakedModel, BakedModelCoreProvider {
    private final BakedModelCore<?> core;

    public FRAPIBakedModelImpl(BakedModelCore<?> core) {this.core = core;}

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction,
                                             @NotNull RandomSource random) {
        return List.of();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return core.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return core.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return core.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return core.isCustomRenderer();
    }

    @Override
    public @NotNull TextureAtlasSprite getParticleIcon() {
        return core.getParticleIcon();
    }

    @Override
    public @NotNull ItemTransforms getTransforms() {
        return core.getTransforms();
    }

    @Override
    public @NotNull ItemOverrides getOverrides() {
        return core.getOverrides();
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos,
                               Supplier<RandomSource> randomSupplier, RenderContext context) {
        DataHolder data =
            Objects.requireNonNullElse((DataHolder) blockView.getBlockEntityRenderData(pos), DataHolder.empty());
        Object key = core.getBlockKey(new ModelBlockContext(blockView, pos, state, randomSupplier, data));
        ((BakedModelCore<Object>) core).renderBlock(new FRAPIQuadEmitter(context.getEmitter()), key);
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
        core.renderItem(new FRAPIQuadEmitter(context.getEmitter()), new ModelItemContext(stack, randomSupplier));
    }

    @Override
    public BakedModelCore<?> krender$getCore() {
        return core;
    }
}
