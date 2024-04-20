package com.kneelawk.commonrender.impl.loading;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import net.minecraft.server.packs.resources.ResourceManager;

import com.kneelawk.commonrender.api.loading.PreparableModelBakeryPlugin;

public record PreparableModelBakeryPluginHolder<T>(PreparableModelBakeryPlugin.ResourceLoader<T> loader,
                                                   PreparableModelBakeryPlugin<T> plugin) {
    CompletableFuture<PreparedModelBakeryPlugin<T>> load(ResourceManager resourceManager,
                                                         Executor prepareExecutor) {
        return loader.loadResource(resourceManager, prepareExecutor)
            .thenApply(resource -> new PreparedModelBakeryPlugin<>(resource, plugin));
    }
}
