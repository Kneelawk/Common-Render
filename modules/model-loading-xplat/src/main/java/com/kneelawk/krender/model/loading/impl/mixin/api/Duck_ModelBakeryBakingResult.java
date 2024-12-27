package com.kneelawk.krender.model.loading.impl.mixin.api;

import java.util.Map;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

public interface Duck_ModelBakeryBakingResult {
    void krender$setExtraModels(Map<ResourceLocation, BakedModel> extraModels);

    Map<ResourceLocation, BakedModel> krender$getExtraModels();
}
