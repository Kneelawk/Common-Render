package com.kneelawk.krender.model.loading.impl.loading;

import com.kneelawk.krender.model.loading.api.ModelBakeryInitCallback;
import com.kneelawk.krender.model.loading.api.ModelBakeryPlugin;
import com.kneelawk.krender.model.loading.api.PreparableModelBakeryPlugin;

public class ModelBakeryInitContext implements ModelBakeryInitCallback.Context {
    public static final ModelBakeryInitContext INSTANCE = new ModelBakeryInitContext();

    @Override
    public void register(ModelBakeryPlugin plugin) {
        ModelBakeryPlugin.register(plugin);
    }

    @Override
    public <T> void registerPreparable(PreparableModelBakeryPlugin.ResourceLoader<T> loader,
                                       PreparableModelBakeryPlugin<T> plugin) {
        ModelBakeryPlugin.registerPreparable(loader, plugin);
    }
}
