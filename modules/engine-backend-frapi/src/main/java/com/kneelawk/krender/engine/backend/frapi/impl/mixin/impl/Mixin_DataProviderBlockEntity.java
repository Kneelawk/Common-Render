package com.kneelawk.krender.engine.backend.frapi.impl.mixin.impl;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import net.fabricmc.fabric.api.blockview.v2.RenderDataBlockEntity;

import com.kneelawk.krender.engine.api.data.DataProviderBlockEntity;

@Mixin(value = DataProviderBlockEntity.class, remap = false)
public class Mixin_DataProviderBlockEntity implements RenderDataBlockEntity {
    @Override
    public @Nullable Object getRenderData() {
        return ((DataProviderBlockEntity) (Object) this).getData();
    }
}
