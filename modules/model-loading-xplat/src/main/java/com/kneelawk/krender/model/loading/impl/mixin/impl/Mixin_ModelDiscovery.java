package com.kneelawk.krender.model.loading.impl.mixin.impl;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.resources.model.ModelDiscovery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

import com.kneelawk.krender.model.loading.impl.loading.ModelManagerPluginManager;

@Mixin(ModelDiscovery.class)
public abstract class Mixin_ModelDiscovery {
    @Shadow
    abstract UnbakedModel getBlockModel(ResourceLocation modelLocation);

    @Unique
    private ModelManagerPluginManager krender$manager;

    @Inject(method = "name=/<init>/", at = @At("RETURN"))
    private void krender$init(CallbackInfo ci) {
        krender$manager = ModelManagerPluginManager.CURRENT_MANAGER.get();
    }

    @Inject(method = "discoverDependencies", at = @At("RETURN"))
    private void krender$referenceExtraModels(CallbackInfo ci) {
        if (krender$manager != null) {
            krender$manager.resolveExtraModels((ModelDiscovery) (Object) this, this::getBlockModel);
        }
    }
}
