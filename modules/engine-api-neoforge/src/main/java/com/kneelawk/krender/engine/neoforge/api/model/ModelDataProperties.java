package com.kneelawk.krender.engine.neoforge.api.model;

import net.neoforged.neoforge.client.model.data.ModelProperty;

import com.kneelawk.krender.engine.api.data.DataHolder;

/**
 * Utility class holding KRender's {@link net.neoforged.neoforge.client.model.data.ModelData} keys.
 */
public final class ModelDataProperties {
    private ModelDataProperties() {}

    /**
     * Common {@link ModelProperty} for holding {@link DataHolder}s passed to {@link net.minecraft.client.resources.model.BakedModel}.
     */
    public static final ModelProperty<DataHolder> DATA_HOLDER_MODEL_PROPERTY = new ModelProperty<>();
}
