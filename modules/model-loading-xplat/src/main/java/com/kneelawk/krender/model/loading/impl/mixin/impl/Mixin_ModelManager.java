package com.kneelawk.krender.model.loading.impl.mixin.impl;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelDiscovery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;

import com.kneelawk.krender.model.loading.impl.loading.ModelManagerPluginManager;
import com.kneelawk.krender.model.loading.impl.loading.ModelManagerPluginRegistrar;
import com.kneelawk.krender.model.loading.impl.loading.PreparedModelManagerPluginList;
import com.kneelawk.krender.model.loading.impl.mixin.api.Duck_ModelBakeryBakingResult;
import com.kneelawk.krender.model.loading.impl.mixin.api.Duck_ModelManager;

@Mixin(ModelManager.class)
public class Mixin_ModelManager implements Duck_ModelManager {
    @Shadow
    private BakedModel missingModel;

    @Unique
    private @Nullable Map<ResourceLocation, BakedModel> krender$extraModels;

    @Inject(method = "reload", at = @At("HEAD"))
    private void krender$prepare(PreparableReloadListener.PreparationBarrier barrier, ResourceManager manager,
                                 Executor backgroundExecutor, Executor gameExecutor,
                                 CallbackInfoReturnable<CompletableFuture<Void>> cir, @Share("pluginManager")
                                 LocalRef<CompletableFuture<ModelManagerPluginManager>> pluginManager) {
        pluginManager.set(ModelManagerPluginRegistrar.prepare(manager, backgroundExecutor)
            .thenApply(PreparedModelManagerPluginList::loadPlugins));
    }

    @ModifyExpressionValue(method = "reload", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/resources/model/ModelManager;loadBlockModels(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"))
    private CompletableFuture<Map<ResourceLocation, UnbakedModel>> krender$addReferenceableModels(
        CompletableFuture<Map<ResourceLocation, UnbakedModel>> original,
        @Local(argsOnly = true, ordinal = 0) Executor backgroundExecutor,
        @Share("pluginManager") LocalRef<CompletableFuture<ModelManagerPluginManager>> pluginManager) {
        return original.thenCombineAsync(pluginManager.get(),
            (models, manager) -> manager.addReferenceableModels(models), backgroundExecutor);
    }

    @ModifyExpressionValue(method = "reload", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/resources/model/BlockStateModelLoader;loadBlockStates(Lnet/minecraft/client/resources/model/UnbakedModel;Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"))
    private CompletableFuture<BlockStateModelLoader.LoadedModels> krender$addBlockStateModels(
        CompletableFuture<BlockStateModelLoader.LoadedModels> original,
        @Local(argsOnly = true, ordinal = 0) Executor backgroundExecutor,
        @Share("pluginManager") LocalRef<CompletableFuture<ModelManagerPluginManager>> pluginManager) {
        return original.thenCombineAsync(pluginManager.get(), (models, manager) -> manager.addBlockStateModels(models),
            backgroundExecutor);
    }

    @ModifyArg(method = "reload", at = @At(value = "INVOKE",
        target = "Ljava/util/concurrent/CompletableFuture;thenApplyAsync(Ljava/util/function/Function;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;",
        ordinal = 1))
    private Function<Void, ModelDiscovery> krender$passManagerToDiscoverer(Function<Void, ModelDiscovery> fn,
                                                                           @Share("pluginManager")
                                                                           LocalRef<CompletableFuture<ModelManagerPluginManager>> pluginManager) {
        CompletableFuture<ModelManagerPluginManager> future = pluginManager.get();
        return unused -> {
            if (future == null) {
                return fn.apply(unused);
            } else {
                ModelManagerPluginManager.CURRENT_MANAGER.set(future.join());
                ModelDiscovery ret = fn.apply(unused);
                ModelManagerPluginManager.CURRENT_MANAGER.remove();
                return ret;
            }
        };
    }

    @ModifyArg(method = "reload", at = @At(value = "INVOKE",
        target = "Ljava/util/concurrent/CompletableFuture;thenApplyAsync(Ljava/util/function/Function;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;",
        ordinal = 3))
    private Function<Void, Object> krender$passManagerToBakery(Function<Void, Object> fn, @Share("pluginManager")
    LocalRef<CompletableFuture<ModelManagerPluginManager>> pluginManager) {
        CompletableFuture<ModelManagerPluginManager> future = pluginManager.get();
        return unused -> {
            if (future == null) {
                return fn.apply(unused);
            } else {
                ModelManagerPluginManager.CURRENT_MANAGER.set(future.join());
                Object ret = fn.apply(unused);
                ModelManagerPluginManager.CURRENT_MANAGER.remove();
                return ret;
            }
        };
    }

    @Inject(method = "apply", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/resources/model/ModelBakery$BakingResult;missingItemModel()Lnet/minecraft/client/renderer/item/ItemModel;"))
    private void krender$apply(CallbackInfo ci, @Local() ModelBakery.BakingResult bakingResult) {
        krender$extraModels = ((Duck_ModelBakeryBakingResult) (Object) bakingResult).krender$getExtraModels();
    }

    @Override
    public BakedModel krender$getExtraModel(ResourceLocation path) {
        if (krender$extraModels != null && krender$extraModels.containsKey(path)) return krender$extraModels.get(path);
        return missingModel;
    }
}
