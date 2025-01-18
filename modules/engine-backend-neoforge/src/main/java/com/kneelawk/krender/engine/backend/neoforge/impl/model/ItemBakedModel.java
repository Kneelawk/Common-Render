package com.kneelawk.krender.engine.backend.neoforge.impl.model;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import com.kneelawk.krender.engine.api.model.BakedModelCore;

public class ItemBakedModel implements BakedModel {
    private final BakedModelCore<?> core;
    private final RenderType type;
    private final List<BakedQuad> quads;

    public ItemBakedModel(BakedModelCore<?> core, RenderType type, List<BakedQuad> quads) {
        this.core = core;
        this.type = type;
        this.quads = quads;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction,
                                    RandomSource randomSource) {
        return quads;
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
    public RenderType getRenderType(ItemStack itemStack) {
        return type;
    }
}
