package com.kneelawk.krender.model.obj.impl;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import com.kneelawk.krender.model.guard.api.ModelGuards;
import com.kneelawk.krender.model.loading.api.ModelBakeryPlugin;
import com.kneelawk.krender.model.obj.impl.format.ObjFile;
import com.kneelawk.krender.model.obj.impl.format.metadata.ObjMetadata;

public class KObj {
    public static void register() {
        ModelBakeryPlugin.registerPreparable((resourceManager, prepareExecutor) -> CompletableFuture.supplyAsync(() -> {
            Map<ResourceLocation, ObjUnbakedModel> unbakedModels = new Object2ObjectLinkedOpenHashMap<>();

            ModelGuards guards = ModelGuards.load(resourceManager);
            Map<ResourceLocation, Resource> objResources =
                guards.getModels(resourceManager, KObjConstants.LOADER_ID, ".obj");

            for (var entry : objResources.entrySet()) {
                try {
                    ObjFile file = ObjFile.load(entry.getValue(), entry.getKey(), resourceManager, guards);
                    ObjMetadata metadata = entry.getValue().metadata().getSection(ObjMetadata.Serializer.INSTANCE)
                        .orElse(ObjMetadata.DEFAULT);
                    ObjUnbakedModel model = new ObjUnbakedModel(file, metadata, entry.getKey());
                    unbakedModels.put(entry.getKey(), model);
                } catch (IOException e) {
                    KObjLog.LOG.error("Error loading obj model: '{}'", entry.getKey(), e);
                }
            }

            return unbakedModels;
        }, prepareExecutor), (resource, ctx) -> {
            ctx.addLowLevelModels(resource);
        });
    }
}
