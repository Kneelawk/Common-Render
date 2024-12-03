package com.kneelawk.krender.engine.backend.neoforge.impl.mixin.impl;

import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.extensions.IBlockEntityExtension;

import org.spongepowered.asm.mixin.Mixin;

import com.kneelawk.krender.engine.api.data.DataProviderBlockEntity;
import com.kneelawk.krender.engine.neoforge.api.model.ModelDataProperties;

@Mixin(value = DataProviderBlockEntity.class, remap = false)
public abstract class Mixin_DataProviderBlockEntity implements IBlockEntityExtension {
    @Override
    public ModelData getModelData() {
        return ModelData.builder().with(ModelDataProperties.DATA_HOLDER_MODEL_PROPERTY,
            ((DataProviderBlockEntity) (Object) this).getRenderDataHolder()).build();
    }
}
