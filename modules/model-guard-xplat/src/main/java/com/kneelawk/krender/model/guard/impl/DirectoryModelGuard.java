package com.kneelawk.krender.model.guard.impl;

import java.util.Map;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import com.kneelawk.krender.model.guard.api.ModelGuard;

public final class DirectoryModelGuard implements ModelGuard {
    public static final MapCodec<DirectoryModelGuard> MAP_CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("loader").forGetter(DirectoryModelGuard::loader),
            Codec.STRING.fieldOf("directory").forGetter(DirectoryModelGuard::directory),
            Codec.STRING.optionalFieldOf("prefix", "").forGetter(DirectoryModelGuard::prefix)
        ).apply(instance, DirectoryModelGuard::new));

    private final ResourceLocation loader;
    private final String directory;
    private final String prefix;

    public DirectoryModelGuard(ResourceLocation loader, String directory, String prefix) {
        this.loader = loader;
        this.directory = directory;
        this.prefix = prefix;
    }

    @Override
    public MapCodec<? extends ModelGuard> getCodec() {
        return MAP_CODEC;
    }

    @Override
    public ResourceLocation getLoader() {
        return loader;
    }

    @Override
    public Map<ResourceLocation, Resource> load(ResourceManager manager, String suffix) {
        FileToIdConverter converter = new FileToIdConverter(directory, suffix);
        return converter.listMatchingResources(manager).entrySet().stream()
            .map(e -> Pair.of(converter.fileToId(e.getKey()).withPrefix(prefix), e.getValue()))
            .collect(Object2ObjectOpenHashMap::new, (map, pair) -> map.put(pair.key(), pair.value()),
                Object2ObjectOpenHashMap::putAll);
    }

    private ResourceLocation loader() {return loader;}

    private String directory() {return directory;}

    private String prefix() {return prefix;}
}
