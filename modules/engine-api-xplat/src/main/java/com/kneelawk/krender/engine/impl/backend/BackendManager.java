package com.kneelawk.krender.engine.impl.backend;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.jetbrains.annotations.Nullable;

import net.minecraft.server.packs.PackType;

import com.kneelawk.commonevents.api.Listen;
import com.kneelawk.commonevents.api.Scan;
import com.kneelawk.krender.engine.api.KRenderer;
import com.kneelawk.krender.engine.api.backend.BackendRegistrationCallback;
import com.kneelawk.krender.engine.api.backend.KRenderBackend;
import com.kneelawk.krender.engine.impl.KRELog;
import com.kneelawk.krender.reloadlistener.api.ReloadContext;
import com.kneelawk.krender.reloadlistener.api.ReloadListenerEvents;
import com.kneelawk.krender.reloadlistener.api.ReloadListenerStatus;

@Scan(side = Scan.Side.CLIENT)
public class BackendManager {
    private static final ReentrantReadWriteLock backendLock = new ReentrantReadWriteLock();
    private static volatile Map<String, KRenderer> renderersByName;
    private static volatile List<KRenderer> renderersByPriority;
    private static volatile KRenderer defaultRenderer;

    static {
        if (!ReloadListenerStatus.isReloadingResourcePacks()) {
            loadBackends();
        }
    }

    @Listen(ReloadListenerEvents.Pre.class)
    public static void reloadResources(ReloadContext ctx) {
        if (ctx.getPackType() == PackType.CLIENT_RESOURCES) {
            loadBackends();
        }
    }

    private static void loadBackends() {
        backendLock.writeLock().lock();

        try {
            KRELog.LOG.info("[KRender] Loading KRender Engine backends...");

            BackendRegistrationContext ctx = new BackendRegistrationContext();
            BackendRegistrationCallback.EVENT.invoker().registerBackends(ctx);
            Map<String, KRenderBackend> backends = ctx.getBackends();

            KRELog.LOG.info("[KRender] KRender Engine backends: {}", backends.keySet());

            Map<String, Integer> priorities = backends.entrySet().stream()
                .collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, e -> e.getValue().getPriority()));
            renderersByName = backends.entrySet().stream()
                .collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, e -> e.getValue().getRenderer()));

            if (backends.isEmpty()) {
                KRELog.LOG.error("[KRender] No KRender Engine backends present! KRender Engine will not work.");
                renderersByPriority = List.of();
            } else {
                String bestRenderer = priorities.entrySet().stream().min(Map.Entry.comparingByValue()).get().getKey();
                KRELog.LOG.info("[KRender] Best KRender Engine backend: {}", bestRenderer);

                Map<String, Integer> newPriorities = BackendConfig.getDefaultBackend(priorities);
                ArrayList<String> sortedBackends = new ArrayList<>(backends.keySet());
                sortedBackends.sort(Comparator.comparing(newPriorities::get));
                String defaultName = sortedBackends.getFirst();

                KRELog.LOG.info("[KRender] Sorted KRender Engine Backends: {}", sortedBackends);

                KRELog.LOG.info("[KRender] Default KRender Engine backend: {}", defaultName);

                renderersByPriority =
                    sortedBackends.stream().map(renderersByName::get).collect(ImmutableList.toImmutableList());
                defaultRenderer = renderersByPriority.getFirst();

                KRELog.LOG.info("[KRender] KRender Engine backends loaded.");
            }
        } finally {
            backendLock.writeLock().unlock();
        }
    }

    public static KRenderer getDefault() {
        backendLock.readLock().lock();
        try {
            KRenderer defaultRenderer = BackendManager.defaultRenderer;
            if (defaultRenderer == null) throw new RuntimeException("No KRender Engine backends are present");
            return defaultRenderer;
        } finally {
            backendLock.readLock().unlock();
        }
    }

    public static @Nullable KRenderer tryGetDefault() {
        backendLock.readLock().lock();
        try {
            return defaultRenderer;
        } finally {
            backendLock.readLock().unlock();
        }
    }

    public static KRenderer get(String name) {
        backendLock.readLock().lock();
        try {
            KRenderer renderer = renderersByName.get(name);
            if (renderer == null) throw new RuntimeException("No KRender Engine backend with name: " + name);
            return renderer;
        } finally {
            backendLock.readLock().unlock();
        }
    }

    public static @Nullable KRenderer tryGet(String name) {
        backendLock.readLock().lock();
        try {
            return renderersByName.get(name);
        } finally {
            backendLock.readLock().unlock();
        }
    }

    public static List<KRenderer> getByPriority() {
        backendLock.readLock().lock();
        try {
            return renderersByPriority;
        } finally {
            backendLock.readLock().unlock();
        }
    }
}
