package com.kneelawk.krender.model.loading.impl.loading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import net.minecraft.server.packs.resources.ResourceManager;

import com.kneelawk.krender.model.loading.api.ModelBakeryPlugin;
import com.kneelawk.krender.model.loading.api.PreparableModelBakeryPlugin;
import com.kneelawk.krender.model.loading.impl.KRLog;

public class ModelBakeryPluginRegistrar {
    // reload is only executed once at a time, usually on the render thread, so this should be safe
    public static final AtomicReference<CompletableFuture<PreparedModelBakeryPluginList>> PREPARE_FUTURE =
        new AtomicReference<>();

    private static final ReentrantLock lock = new ReentrantLock();
    private static final List<ModelBakeryPlugin> plugins = new ArrayList<>();
    private static final List<PreparableModelBakeryPluginHolder<?>> preparables = new ArrayList<>();

    public static void preparePlugins(ResourceManager resourceManager, Executor prepareExecutor) {
        PREPARE_FUTURE.set(prepare(resourceManager, prepareExecutor));
    }

    private static CompletableFuture<PreparedModelBakeryPluginList> prepare(ResourceManager resourceManager,
                                                                            Executor prepareExecutor) {
        List<CompletableFuture<? extends PreparedModelBakeryPlugin<?>>> preparedList = new ArrayList<>();
        for (PreparableModelBakeryPluginHolder<?> preparable : preparables) {
            try {
                CompletableFuture<? extends PreparedModelBakeryPlugin<?>> load =
                    preparable.load(resourceManager, prepareExecutor).handle((prepared, ex) -> {
                        if (ex == null) {
                            return prepared;
                        } else {
                            KRLog.LOGGER.error("Error preparing plugin", ex);
                            return null;
                        }
                    });
                preparedList.add(load);
            } catch (Exception e) {
                KRLog.LOGGER.error("Error starting plugin preparation", e);
            }
        }

        CompletableFuture<Void> preparedAll = CompletableFuture.allOf(preparedList.toArray(CompletableFuture[]::new));

        return preparedAll.thenApply(void_ -> {
            List<PreparedModelBakeryPlugin<?>> list = new ArrayList<>();
            for (CompletableFuture<? extends PreparedModelBakeryPlugin<?>> preparedModelBakeryPluginCompletableFuture : preparedList) {
                try {
                    PreparedModelBakeryPlugin<?> plugin = preparedModelBakeryPluginCompletableFuture.get();
                    if (plugin != null) list.add(plugin);
                } catch (Exception e) {
                    KRLog.LOGGER.error("Error preparing plugin", e);
                }
            }
            return new PreparedModelBakeryPluginList(plugins, list);
        });
    }

    public static void register(ModelBakeryPlugin plugin) {
        lock.lock();
        try {
            plugins.add(plugin);
        } finally {
            lock.unlock();
        }
    }

    public static <T> void registerPreparable(PreparableModelBakeryPlugin.ResourceLoader<T> loader,
                                              PreparableModelBakeryPlugin<T> plugin) {
        lock.lock();
        try {
            preparables.add(new PreparableModelBakeryPluginHolder<>(loader, plugin));
        } finally {
            lock.unlock();
        }
    }
}
