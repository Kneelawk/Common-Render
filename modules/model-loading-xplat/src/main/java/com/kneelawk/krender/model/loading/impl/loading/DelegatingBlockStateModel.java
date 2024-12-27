package com.kneelawk.krender.model.loading.impl.loading;

import net.minecraft.client.renderer.block.model.UnbakedBlockStateModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class DelegatingBlockStateModel implements UnbakedBlockStateModel {
    private final ResourceLocation reference;

    public DelegatingBlockStateModel(ResourceLocation reference) {this.reference = reference;}

    @Override
    public BakedModel bake(ModelBaker baker) {
        return baker.bake(reference, BlockModelRotation.X0_Y0);
    }

    @Override
    public Object visualEqualityGroup(BlockState state) {
        return this;
    }

    @Override
    public void resolveDependencies(Resolver resolver) {
        resolver.resolve(reference);
    }
}
