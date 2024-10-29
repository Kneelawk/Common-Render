package com.kneelawk.krender.model.gltf.impl.format;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.objects.Object2ReferenceLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import com.kneelawk.krender.model.guard.api.ModelGuards;
import com.kneelawk.krender.model.guard.impl.KRMGConstants;

public record GltfFile(GltfRoot root, byte @Nullable [] buffer, Map<ResourceLocation, byte[]> dependencies) {
    public static GltfFile loadGltf(Resource resource, ResourceManager manager, ModelGuards guards) throws IOException {
        JsonElement json;
        try (InputStream is = resource.open()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            json = JsonParser.parseReader(reader);
        }

        DataResult<GltfRoot> result = GltfRoot.CODEC.parse(JsonOps.INSTANCE, json);
        if (!result.hasResultOrPartial())
            throw new IOException("Error parsing gltf json: " + result.error().get().message());

        GltfRoot root = result.getPartialOrThrow();
        checkImages(root);

        // load dependencies
        Map<ResourceLocation, byte[]> dependencies = loadDependencies(manager, guards, root);

        return new GltfFile(root, null, dependencies);
    }

    public static GltfFile loadGlb(Resource resource, ResourceManager manager, ModelGuards guards) throws IOException {
        try (InputStream is = resource.open();
             BufferedInputStream bis = new BufferedInputStream(is);
             DataInputStream dis = new DataInputStream(bis)) {

            // readInt is big-endian while GLB files are little-endian
            int magic = dis.readInt();
            if (magic != 0x676C5446) throw new IOException("Not GLB data");
            int _version = swap(dis.readInt());
            int fileLength = swap(dis.readInt());

            int jsonChunkLength = swap(dis.readInt());
            int jsonChunkType = dis.readInt();
            if (jsonChunkType != 0x4A534F4E) throw new IOException("First chunk is not json");

            byte[] jsonBytes = new byte[jsonChunkLength];
            dis.readFully(jsonBytes);
            String json = new String(jsonBytes, StandardCharsets.UTF_8);
            JsonElement element = JsonParser.parseString(json);
            DataResult<GltfRoot> result = GltfRoot.CODEC.parse(JsonOps.INSTANCE, element);
            if (!result.hasResultOrPartial())
                throw new IOException("Error parsing glb json: " + result.error().get().message());
            GltfRoot root = result.getPartialOrThrow();
            checkImages(root);

            if (fileLength > jsonChunkLength + 12) {
                // binary blob is included too
                int binChunkLength = swap(dis.readInt());
                int binChunkType = dis.readInt();
                if (binChunkType != 0x42494E00) throw new IOException("Second chunk is not binary");

                byte[] bin = new byte[binChunkLength];
                dis.readFully(bin);

                // load dependencies
                Map<ResourceLocation, byte[]> dependencies = loadDependencies(manager, guards, root);

                return new GltfFile(root, bin, dependencies);
            } else {
                // load dependencies
                Map<ResourceLocation, byte[]> dependencies = loadDependencies(manager, guards, root);

                return new GltfFile(root, null, dependencies);
            }
        }
    }

    private static int swap(int in) {
        return ((in >> 24) & 0x000000FF) | ((in >> 8) & 0x0000FF00) | ((in << 8) & 0x00FF0000) |
            ((in << 24) & 0xFF000000);
    }

    private static @NotNull Map<ResourceLocation, byte[]> loadDependencies(ResourceManager manager, ModelGuards guards,
                                                                           GltfRoot root) throws IOException {
        // gltf dependencies will usually be unique, so we can just store them
        Set<ResourceLocation> dependencyNames = findAndCheckDependencies(root);
        Map<ResourceLocation, byte[]> dependencies = new Object2ReferenceLinkedOpenHashMap<>();
        for (ResourceLocation dep : dependencyNames) {
            Optional<Resource> depResOpt = guards.getResource(manager, KRMGConstants.LOADER_NAME, dep);
            if (depResOpt.isEmpty()) throw new IOException("glTF tried to load missing resource: " + dep);

            Resource depRes = depResOpt.get();
            byte[] depBytes;
            try (InputStream depIs = depRes.open()) {
                depBytes = IOUtils.toByteArray(depIs);
            }

            dependencies.put(dep, depBytes);
        }
        return dependencies;
    }

    private static Set<ResourceLocation> findAndCheckDependencies(GltfRoot root)
        throws IOException {
        Set<ResourceLocation> dependencies = new ObjectLinkedOpenHashSet<>();

        List<GltfBuffer> buffers = root.buffers();
        for (int i = 0; i < buffers.size(); i++) {
            GltfBuffer buffer = buffers.get(i);
            Optional<String> uriOpt = buffer.uri();
            if (uriOpt.isEmpty()) continue;
            String uri = uriOpt.get();

            String rlStr;
            if (uri.startsWith("rl:")) {
                rlStr = uri.substring("rl:".length());
            } else if (uri.startsWith("resourcelocation:")) {
                rlStr = uri.substring("resourcelocation:".length());
            } else if (uri.startsWith("id:")) {
                rlStr = uri.substring("id:".length());
            } else if (uri.startsWith("identifier:")) {
                rlStr = uri.substring("identifier:".length());
            } else if (uri.startsWith("data:")) {
                continue;
            } else {
                throw new IOException("Invalid Minecraft glTF buffer " + i + " uri: '" + uri +
                    "'. Allowed uri types: 'rl:', 'resourcelocation:', 'id:', 'identifier:', and 'data:'.");
            }

            ResourceLocation rl;
            try {
                rl = ResourceLocation.parse(rlStr);
            } catch (ResourceLocationException e) {
                throw new IOException("Invalid uri resource location: '" + rlStr + "' in buffer " + i, e);
            }
            dependencies.add(rl);
        }

        return dependencies;
    }

    private static void checkImages(GltfRoot root) throws IOException {
        List<GltfImage> images = root.images();
        for (int i = 0; i < images.size(); i++) {
            GltfImage image = images.get(i);
            Optional<String> uriOpt = image.uri();
            if (uriOpt.isEmpty()) {
                if (image.bufferView().isPresent()) continue; // we're referencing a buffer;
                else throw new IOException("Image " + i + " does not have either a uri or buffer associated with it");
            }
            String uri = uriOpt.get();

            if (uri.startsWith("data:")) {
                int start = "data:".length();
                int end = uri.indexOf(';');
                String uriMimeType = uri.substring(start, end);
                Optional<String> mimeTypeOpt = image.mimeType();
                if (mimeTypeOpt.isPresent() && !uriMimeType.equals(mimeTypeOpt.get())) throw new IOException(
                    "Image " + i + " has mismatched mime-type (" + mimeTypeOpt.get() + ") and uri mime-type (" +
                        uriMimeType + ")");
                if (!"image/png".equals(uriMimeType)) throw new IOException(
                    "Image " + i + " has invalid data mime type: '" + uriMimeType +
                        "'. Allowed data mime types: 'image/png'.");
            } else {
                throw new IOException(
                    "Invalid Minecraft glTF image " + i + " uri: '" + uri + "'. Allowed uri types: 'data:'.");
            }
        }
    }
}
