package com.kneelawk.krender.engine.fabric.impl.mixin.impl;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.resources.model.SimpleBakedModel;

import com.kneelawk.krender.engine.api.model.BakedModelCore;
import com.kneelawk.krender.engine.base.model.BakedModelCoreProvider;
import com.kneelawk.krender.engine.impl.VanillaAdapterModelCore;

@Mixin(SimpleBakedModel.class)
public class Mixin_SimpleBakedModel implements BakedModelCoreProvider {
    @Override
    public BakedModelCore<?> krender$getCore() {
        return new VanillaAdapterModelCore((SimpleBakedModel) (Object) this);
    }
}
