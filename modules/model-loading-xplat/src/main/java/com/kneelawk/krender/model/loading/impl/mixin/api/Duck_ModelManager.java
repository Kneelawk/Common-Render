package com.kneelawk.krender.model.loading.impl.mixin.api;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

public interface Duck_ModelManager {
    BakedModel krender$getExtraModel(ResourceLocation path);
}
