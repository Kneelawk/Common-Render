package com.kneelawk.krender.engine.backend.frapi.impl.model;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
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

import com.kneelawk.krender.engine.api.model.BakedModelCore;
import com.kneelawk.krender.engine.api.model.ModelBlockContext;
import com.kneelawk.krender.engine.api.model.ModelItemContext;
import com.kneelawk.krender.engine.backend.frapi.impl.KRBFRLog;
import com.kneelawk.krender.engine.backend.frapi.impl.buffer.FRAPIQuadEmitter;
import com.kneelawk.krender.engine.base.model.BakedModelCoreProvider;

public class FRAPICachedBakedModelImpl implements BakedModel, BakedModelCoreProvider {
    private final BakedModelCore<?> core;

    private final LoadingCache<ModelKeyHolder, Mesh> meshCache =
        CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build(CacheLoader.from(key -> {
            MeshBuilder builder =
                Objects.requireNonNull(RendererAccess.INSTANCE.getRenderer(), "No FRAPI renderer").meshBuilder();
            render(key, builder);
            return builder.build();
        }));

    @SuppressWarnings("unchecked")
    private void render(ModelKeyHolder key, MeshBuilder builder) {
        try {
            ((BakedModelCore<Object>) core).renderBlock(new FRAPIQuadEmitter(builder.getEmitter()), key.modelKey());
        } catch (Exception e) {
            KRBFRLog.LOG.error("Error rendering cached quads for model");
        }
    }

    public FRAPICachedBakedModelImpl(BakedModelCore<?> core) {this.core = core;}

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
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
    public TextureAtlasSprite getParticleIcon() {
        return core.getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
        return core.getTransforms();
    }

    @Override
    public ItemOverrides getOverrides() {
        return core.getOverrides();
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos,
                               Supplier<RandomSource> randomSupplier, RenderContext context) {
        Object key = core.getBlockKey(new ModelBlockContext(blockView, pos, state, randomSupplier));
        try {
            Mesh mesh = meshCache.get(new ModelKeyHolder(key));
            mesh.outputTo(context.getEmitter());
        } catch (ExecutionException e) {
            KRBFRLog.LOG.error("Error caching model", e);
        }
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
