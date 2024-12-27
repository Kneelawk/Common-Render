package com.kneelawk.krender.model.loading.api;

import java.util.Set;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;

import com.kneelawk.krender.model.loading.impl.mixin.api.Duck_ModelManager;

/**
 * Extra {@link net.minecraft.client.resources.model.ModelManager} utilities and additions.
 */
public final class ModelManagerUtils {
    private ModelManagerUtils() {}

    /**
     * Gets an extra model registered via a {@link ModelManagerPlugin}.
     *
     * @param manager the model manager to get the extra model from.
     * @param name    the name of the extra model.
     * @return the extra model loaded and baked.
     * @see ModelManagerPlugin.Context#addExtraModel(ResourceLocation)
     * @see ModelManagerPlugin.Context#addExtraModels(Set)
     */
    public static BakedModel getExtraModel(ModelManager manager, ResourceLocation name) {
        return ((Duck_ModelManager) manager).krender$getExtraModel(name);
    }
}
