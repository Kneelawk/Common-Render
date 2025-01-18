package com.kneelawk.krender.engine.backend.neoforge.impl.model;

import java.util.List;
import java.util.Objects;

import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.util.TriState;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import com.kneelawk.krender.engine.api.buffer.QuadEmitter;
import com.kneelawk.krender.engine.api.data.DataHolder;
import com.kneelawk.krender.engine.api.model.BakedModelCore;
import com.kneelawk.krender.engine.api.model.ModelBlockContext;
import com.kneelawk.krender.engine.api.model.ModelItemContext;
import com.kneelawk.krender.engine.base.model.BakedModelCoreProvider;
import com.kneelawk.krender.engine.neoforge.api.model.ModelDataProperties;

/**
 * Non-Caching implementation that calls {@link BakedModelCore#renderBlock(QuadEmitter, Object)} multiple times.
 */
public class NFBakedModelImpl implements BakedModel, BakedModelCoreProvider {
    private static final ThreadLocal<RandomSource> RANDOM_SOURCES = ThreadLocal.withInitial(RandomSource::create);

    private final BakedModelCore<?> core;

    public NFBakedModelImpl(BakedModelCore<?> core) {this.core = core;}

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction,
                                    RandomSource randomSource) {
        return getQuads(blockState, direction, randomSource, ModelData.EMPTY, null);
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
    public TextureAtlasSprite getParticleIcon() {
        return core.getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
        return core.getTransforms();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand,
                                    ModelData data, @Nullable RenderType renderType) {
        if (state != null) {
            // we're in block mode
            ModelKeyHolder holder = data.get(ModelKeyHolder.PROPERTY);
            if (holder == null) return List.of();

            FilteringQuadBaker baker = FilteringQuadBaker.get(side, renderType, state);
            ((BakedModelCore<Object>) core).renderBlock(baker.emitter(), holder.modelKey());
            return baker.bake();
        } else {
            // TODO: item mode
            return List.of();
        }
    }

    @Override
    public TriState useAmbientOcclusion(BlockState state, ModelData data, RenderType renderType) {
        // default impl for now
        return BakedModel.super.useAmbientOcclusion(state, data, renderType);
    }

    @Override
    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData) {
        RandomSource random = RANDOM_SOURCES.get();
        long seed = state.getSeed(pos);
        Object key = core.getBlockKey(new ModelBlockContext(level, pos, state, () -> {
            random.setSeed(seed);
            return random;
        }, Objects.requireNonNullElse(modelData.get(ModelDataProperties.DATA_HOLDER_MODEL_PROPERTY),
            DataHolder.empty())));
        return modelData.derive().with(ModelKeyHolder.PROPERTY, new ModelKeyHolder(key)).build();
    }

    @Override
    public TextureAtlasSprite getParticleIcon(ModelData data) {
        // default impl for now
        return BakedModel.super.getParticleIcon(data);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
        ModelKeyHolder holder = data.get(ModelKeyHolder.PROPERTY);
        if (holder == null) return ChunkRenderTypeSet.none();

        RenderTypeCollector collector = RenderTypeCollector.get(state);
        ((BakedModelCore<Object>) core).renderBlock(collector.emitter(), holder.modelKey());
        return collector.getRenderTypes();
    }

    @Override
    public List<BakedModel> getRenderPasses(ItemStack itemStack) {
        RandomSource random = RANDOM_SOURCES.get();
        ItemSplittingQuadBaker baker = ItemSplittingQuadBaker.get();
        core.renderItem(baker.emitter(), new ModelItemContext(itemStack, () -> {
            random.setSeed(42L);
            return random;
        }));
        return baker.bake(core);
    }

    @Override
    public BakedModelCore<?> krender$getCore() {
        return core;
    }
}
