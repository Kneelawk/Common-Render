package com.kneelawk.krender.model.guard.api;

import java.io.BufferedReader;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;

import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import com.kneelawk.kregistry.lite.api.LiteRegistry;
import com.kneelawk.krender.model.guard.impl.DirectoryModelGuard;
import com.kneelawk.krender.model.guard.impl.KRMGLog;

import static com.kneelawk.krender.model.guard.impl.KRMGConstants.prl;
import static com.kneelawk.krender.model.guard.impl.KRMGConstants.rl;

/**
 * A set of all model guards. Useful for allowing model loaders to know which models they are allowed to load.
 */
public class ModelGuards {
    /**
     * The registry of guard codecs.
     */
    public static final LiteRegistry<MapCodec<? extends ModelGuard>> CODEC_REGISTRY =
        LiteRegistry.simple(rl("guard_codec"));
    /**
     * The location where guards are stored in each resource pack.
     */
    public static final String GUARDS_PATH = "krender/model_guards";
    private static final FileToIdConverter GUARDS_CONVERTER = FileToIdConverter.json(GUARDS_PATH);

    private final Map<ResourceLocation, List<ModelGuard>> guards;

    private ModelGuards(Map<ResourceLocation, List<ModelGuard>> guards) {this.guards = guards;}

    /**
     * Actually loads the model resources that all guards have permitted to be loaded by the given loader.
     *
     * @param manager    the resource manager to load resources from.
     * @param loaderName the name of the model loader to load resources for.
     * @return the model resources for the given loader.
     */
    public Map<ResourceLocation, Resource> getModels(ResourceManager manager, ResourceLocation loaderName,
                                                     String suffix) {
        List<ModelGuard> guardList = guards.get(loaderName);
        if (guardList == null) return Map.of();

        Map<ResourceLocation, Resource> loaded = new Object2ObjectLinkedOpenHashMap<>();
        for (ModelGuard guard : guardList) {
            loaded.putAll(guard.load(manager, suffix));
        }

        return loaded;
    }

    /**
     * Loads a {@link ModelGuards} collection instance from the given resource manager.
     * <p>
     * This collects all guards present into a group that can be used to find models.
     *
     * @param manager the resource manager used to load the guard jsons.
     * @return the new model guards collection.
     */
    public static ModelGuards load(ResourceManager manager) {
        Map<ResourceLocation, List<ModelGuard>> guards = new Object2ObjectLinkedOpenHashMap<>();

        for (var entry : GUARDS_CONVERTER.listMatchingResources(manager).entrySet()) {
            ResourceLocation location = entry.getKey();
            Resource res = entry.getValue();
            try (BufferedReader br = res.openAsReader()) {
                JsonElement element = JsonParser.parseReader(br);
                ModelGuard guard = ModelGuard.CODEC.parse(JsonOps.INSTANCE, element).getPartialOrThrow();

                guards.computeIfAbsent(guard.getLoader(), l -> new ObjectArrayList<>()).add(guard);
            } catch (Exception e) {
                KRMGLog.LOG.error("Error loading model guard '{}'", location, e);
            }
        }

        return new ModelGuards(guards);
    }

    static {
        CODEC_REGISTRY.register(prl("directory"), DirectoryModelGuard.MAP_CODEC);
    }
}
