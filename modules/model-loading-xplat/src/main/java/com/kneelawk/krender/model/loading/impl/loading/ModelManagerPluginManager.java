package com.kneelawk.krender.model.loading.impl.loading;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;

import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.UnbakedBlockStateModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.ModelDiscovery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import com.kneelawk.krender.model.loading.impl.mixin.api.Duck_ModelBakeryBakingResult;

public class ModelManagerPluginManager {
    public static final ThreadLocal<ModelManagerPluginManager> CURRENT_MANAGER = new ThreadLocal<>();

    private final Map<ResourceLocation, UnbakedModel> referenceableModels;
    private final Map<BlockState, UnbakedBlockStateModel> blockStateModels;
    private final Set<ResourceLocation> extraModels;

    public ModelManagerPluginManager(Map<ResourceLocation, UnbakedModel> referenceableModels,
                                     Map<BlockState, UnbakedBlockStateModel> blockStateModels,
                                     Set<ResourceLocation> extraModels) {
        this.referenceableModels = referenceableModels;
        this.blockStateModels = blockStateModels;
        this.extraModels = extraModels;
    }

    public Map<ResourceLocation, UnbakedModel> addReferenceableModels(Map<ResourceLocation, UnbakedModel> models) {
        if (!(models instanceof HashMap<ResourceLocation, UnbakedModel>)) {
            models = new LinkedHashMap<>(models);
        }
        models.putAll(referenceableModels);
        return models;
    }

    public BlockStateModelLoader.LoadedModels addBlockStateModels(BlockStateModelLoader.LoadedModels models) {
        if (!(models.models() instanceof HashMap<ModelResourceLocation, BlockStateModelLoader.LoadedModel>)) {
            models = new BlockStateModelLoader.LoadedModels(new LinkedHashMap<>(models.models()));
        }

        Map<ModelResourceLocation, BlockStateModelLoader.LoadedModel> map = models.models();
        for (var entry : blockStateModels.entrySet()) {
            map.put(BlockModelShaper.stateToModelLocation(entry.getKey()),
                new BlockStateModelLoader.LoadedModel(entry.getKey(), entry.getValue()));
        }

        return models;
    }

    public void resolveExtraModels(ModelDiscovery modelDiscovery,
                                   Function<ResourceLocation, UnbakedModel> getBlockModel) {
        for (ResourceLocation extraModelPath : extraModels) {
            getBlockModel.apply(extraModelPath).resolveDependencies(modelDiscovery.new ResolverImpl());
        }
    }

    public void bakeExtraModels(ModelBakery bakery, ModelBakery.TextureGetter textureGetter,
                                ModelBakery.BakingResult bakingResult) {
        Map<ResourceLocation, BakedModel> extraBakedModels = new Object2ObjectLinkedOpenHashMap<>();
        for (ResourceLocation extraModelPath : extraModels) {
            ModelDebugName debugName = () -> extraModelPath.toString() + "#extra";
            BakedModel baked =
                bakery.new ModelBakerImpl(textureGetter, debugName).bake(extraModelPath, BlockModelRotation.X0_Y0);
            extraBakedModels.put(extraModelPath, baked);
        }
        ((Duck_ModelBakeryBakingResult) (Object) bakingResult).krender$setExtraModels(extraBakedModels);
    }
}
