package com.kneelawk.krender.model.gltf.impl;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;

import com.kneelawk.commonevents.api.Listen;
import com.kneelawk.commonevents.api.Scan;
import com.kneelawk.krender.model.gltf.impl.format.metadata.GltfMetadata;
import com.kneelawk.krender.model.gltf.impl.mixin.impl.Accessor_SpriteSources;
import com.kneelawk.krender.model.guard.api.ModelGuards;
import com.kneelawk.krender.model.loading.api.ModelBakeryPlugin;
import com.kneelawk.krender.reloadlistener.api.ReloadContext;
import com.kneelawk.krender.reloadlistener.api.ReloadListenerEvents;

import static com.kneelawk.krender.model.gltf.impl.KGltfConstants.prl;

@Scan(side = Scan.Side.CLIENT)
public class KGltf {
    private static final AtomicBoolean initialized = new AtomicBoolean(false);

    @Listen(ReloadListenerEvents.Pre.class)
    public static void reloadResources(ReloadContext ctx) {
        System.out.println("Reloading resources...");
        if (ctx.getPackType() == PackType.CLIENT_RESOURCES) {
            if (!initialized.getAndSet(true)) {
                init();
            }
        }
    }

    public static void init() {
        register();
        registerSync();
    }

    public static void register() {
        ModelBakeryPlugin.registerPreparable((resourceManager, prepareExecutor) -> CompletableFuture.supplyAsync(() -> {
            Map<ResourceLocation, GltfUnbakedModel> unbakedModels = new Object2ObjectLinkedOpenHashMap<>();

            ModelGuards guards = ModelGuards.load(resourceManager);
            Map<ResourceLocation, Resource> gltfResources =
                guards.getModels(resourceManager, KGltfConstants.LOADER_ID, ".gltf");
            Map<ResourceLocation, Resource> glbResources =
                guards.getModels(resourceManager, KGltfConstants.LOADER_ID, ".glb");

            for (var entry : gltfResources.entrySet()) {
                try {
                    GltfFile file = GltfFile.loadGltf(entry.getValue(), resourceManager, guards);
                    GltfMetadata metadata = entry.getValue().metadata().getSection(GltfMetadata.Serializer.INSTANCE)
                        .orElse(GltfMetadata.DEFAULT);
                    GltfUnbakedModel unbakedModel = new GltfUnbakedModel(file, metadata, entry.getKey());
                    unbakedModels.put(entry.getKey(), unbakedModel);
                } catch (IOException e) {
                    KGltfLog.LOG.error("Error loading glTF model: '{}'", entry.getKey(), e);
                }
            }

            for (var entry : glbResources.entrySet()) {
                try {
                    GltfFile file = GltfFile.loadGlb(entry.getValue(), resourceManager, guards);
                    GltfMetadata metadata = entry.getValue().metadata().getSection(GltfMetadata.Serializer.INSTANCE)
                        .orElse(GltfMetadata.DEFAULT);
                    GltfUnbakedModel unbakedModel = new GltfUnbakedModel(file, metadata, entry.getKey());
                    unbakedModels.put(entry.getKey(), unbakedModel);
                } catch (IOException e) {
                    KGltfLog.LOG.error("Error loading glb model: '{}'", entry.getKey(), e);
                }
            }

            return unbakedModels;
        }, prepareExecutor), (resource, ctx) -> ctx.addLowLevelModels(resource));
    }

    public static void registerSync() {
        System.out.println("Adding...");
        Accessor_SpriteSources.krender$types().put(prl("gltf"), GltfSpriteSource.TYPE);
    }
}
