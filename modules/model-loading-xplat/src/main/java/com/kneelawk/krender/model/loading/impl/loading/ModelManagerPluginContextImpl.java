package com.kneelawk.krender.model.loading.impl.loading;

import java.util.Map;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

import net.minecraft.client.renderer.block.model.UnbakedBlockStateModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import com.kneelawk.krender.model.loading.api.ModelManagerPlugin;

public class ModelManagerPluginContextImpl implements ModelManagerPlugin.Context {
    private final Map<ResourceLocation, UnbakedModel> referenceableModels = new Object2ObjectLinkedOpenHashMap<>();
    private final Map<BlockState, UnbakedBlockStateModel> blockStateModels = new Object2ObjectLinkedOpenHashMap<>();
    private final Set<ResourceLocation> extraModels = new ObjectLinkedOpenHashSet<>();

    public ModelManagerPluginManager createManager() {
        return new ModelManagerPluginManager(referenceableModels, blockStateModels, extraModels);
    }

    @Override
    public void linkBlockStateToModel(BlockState state, ResourceLocation name) {
        blockStateModels.put(state, new DelegatingBlockStateModel(name));
    }

    @Override
    public void linkBlockStatesToModels(Map<BlockState, ResourceLocation> models) {
        for (var entry : models.entrySet()) {
            blockStateModels.put(entry.getKey(), new DelegatingBlockStateModel(entry.getValue()));
        }
    }

    @Override
    public void addBlockStateModel(BlockState state, UnbakedBlockStateModel model) {
        blockStateModels.put(state, model);
    }

    @Override
    public void addBlockStateModels(Map<BlockState, ? extends UnbakedBlockStateModel> models) {
        blockStateModels.putAll(models);
    }

    @Override
    public void addReferenceableModel(ResourceLocation name, UnbakedModel model) {
        referenceableModels.put(name, model);
    }

    @Override
    public void addReferenceableModels(Map<ResourceLocation, ? extends UnbakedModel> models) {
        referenceableModels.putAll(models);
    }

    @Override
    public void addExtraModel(ResourceLocation name) {
        extraModels.add(name);
    }

    @Override
    public void addExtraModels(Set<ResourceLocation> names) {
        extraModels.addAll(names);
    }
}
