package com.kneelawk.krender.model.loading.impl.loading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantLock;

import net.minecraft.server.packs.resources.ResourceManager;

import com.kneelawk.krender.model.loading.api.ModelManagerPlugin;
import com.kneelawk.krender.model.loading.api.PreparableModelManagerPlugin;
import com.kneelawk.krender.model.loading.impl.KRLog;

public class ModelManagerPluginRegistrar {
    private static final ReentrantLock lock = new ReentrantLock();
    private static final List<ModelManagerPlugin> plugins = new ArrayList<>();
    private static final List<PreparableModelManagerPluginHolder<?>> preparables = new ArrayList<>();

    public static CompletableFuture<PreparedModelManagerPluginList> prepare(ResourceManager resourceManager,
                                                                            Executor prepareExecutor) {
        List<CompletableFuture<? extends PreparedModelManagerPlugin<?>>> preparedList = new ArrayList<>();
        for (PreparableModelManagerPluginHolder<?> preparable : preparables) {
            try {
                CompletableFuture<? extends PreparedModelManagerPlugin<?>> load =
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
            List<PreparedModelManagerPlugin<?>> list = new ArrayList<>();
            for (CompletableFuture<? extends PreparedModelManagerPlugin<?>> preparedModelBakeryPluginCompletableFuture : preparedList) {
                try {
                    PreparedModelManagerPlugin<?> plugin = preparedModelBakeryPluginCompletableFuture.get();
                    if (plugin != null) list.add(plugin);
                } catch (Exception e) {
                    KRLog.LOGGER.error("Error preparing plugin", e);
                }
            }
            return new PreparedModelManagerPluginList(new ArrayList<>(plugins), list);
        });
    }

    public static void register(ModelManagerPlugin plugin) {
        lock.lock();
        try {
            plugins.add(plugin);
        } finally {
            lock.unlock();
        }
    }

    public static <T> void registerPreparable(PreparableModelManagerPlugin.ResourceLoader<T> loader,
                                              PreparableModelManagerPlugin<T> plugin) {
        lock.lock();
        try {
            preparables.add(new PreparableModelManagerPluginHolder<>(loader, plugin));
        } finally {
            lock.unlock();
        }
    }
}
