package com.kneelawk.krender.model.loading.impl.loading;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import net.minecraft.server.packs.resources.ResourceManager;

import com.kneelawk.krender.model.loading.api.PreparableModelManagerPlugin;

public record PreparableModelManagerPluginHolder<T>(PreparableModelManagerPlugin.ResourceLoader<T> loader,
                                                    PreparableModelManagerPlugin<T> plugin) {
    CompletableFuture<PreparedModelManagerPlugin<T>> load(ResourceManager resourceManager,
                                                          Executor prepareExecutor) {
        return loader.loadResource(resourceManager, prepareExecutor)
            .thenApply(resource -> new PreparedModelManagerPlugin<>(resource, plugin));
    }
}
