package com.kneelawk.krender.model.loading.impl.mixin.impl;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.block.model.UnbakedBlockStateModel;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

import com.kneelawk.krender.model.loading.impl.loading.ModelManagerPluginManager;

@Mixin(ModelBakery.class)
public class Mixin_ModelBakery {
    @Unique
    private ModelManagerPluginManager krender$manager;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void krender$init(EntityModelSet entityModelSet,
                              Map<ModelResourceLocation, UnbakedBlockStateModel> unbakedBlockStateModels,
                              Map<ResourceLocation, ClientItem> unbakedItemStackModels,
                              Map<ResourceLocation, UnbakedModel> unbakedPlainModels, UnbakedModel missingModel,
                              CallbackInfo ci) {
        krender$manager = ModelManagerPluginManager.CURRENT_MANAGER.get();
    }

    @Inject(method = "bakeModels", at = @At("RETURN"))
    private void krender$bakeExtraModels(ModelBakery.TextureGetter textureGetter,
                                         CallbackInfoReturnable<ModelBakery.BakingResult> cir) {
        krender$manager.bakeExtraModels((ModelBakery) (Object) this, textureGetter, cir.getReturnValue());
    }
}
