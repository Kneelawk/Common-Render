package com.kneelawk.krender.model.guard.api;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

/**
 * A type of model guard.
 */
public interface ModelGuard {
    /**
     * The codec for model guards.
     */
    Codec<ModelGuard> CODEC =
        ModelGuards.CODEC_REGISTRY.byNameCodec().dispatch(ModelGuard::getCodec, Function.identity());

    /**
     * The codec for model guard files.
     */
    Codec<List<ModelGuard>> FILE_CODEC = CODEC.listOf().fieldOf("guards").codec();

    /**
     * {@return this model guard's codec}
     */
    MapCodec<? extends ModelGuard> getCodec();

    /**
     * {@return the loader this guard loads for}
     */
    ResourceLocation getLoader();

    /**
     * Actually loads resources for a model loader.
     *
     * @param manager the resource manager to load from.
     * @param suffix  the suffix of model files to load.
     * @return all loaded resources.
     */
    Map<ResourceLocation, Resource> loadAll(ResourceManager manager, String suffix);

    /**
     * Loads a single resource for a model loader.
     *
     * @param manager the resource manager to load from.
     * @param id      the id of the resource to load.
     * @return the single loaded resource, if present.
     */
    Optional<Resource> load(ResourceManager manager, ResourceLocation id);
}
