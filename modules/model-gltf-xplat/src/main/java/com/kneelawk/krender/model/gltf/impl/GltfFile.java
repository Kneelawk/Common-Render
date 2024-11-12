package com.kneelawk.krender.model.gltf.impl;

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

import com.kneelawk.krender.model.gltf.impl.format.GltfAccessor;
import com.kneelawk.krender.model.gltf.impl.format.GltfAccessorSparse;
import com.kneelawk.krender.model.gltf.impl.format.GltfBuffer;
import com.kneelawk.krender.model.gltf.impl.format.GltfBufferView;
import com.kneelawk.krender.model.gltf.impl.format.GltfImage;
import com.kneelawk.krender.model.gltf.impl.format.GltfRoot;
import com.kneelawk.krender.model.guard.api.ModelGuards;
import com.kneelawk.krender.model.guard.impl.KRMGConstants;

import static com.kneelawk.krender.model.gltf.impl.GltfUtils.swapInt;

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
        if (result.isError()) {
            KGltfLog.LOG.warn("Encountered salvageable error while parsing gltf json: {}",
                result.error().get().message());
        }

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
            int _version = swapInt(dis.readInt());
            int fileLength = swapInt(dis.readInt());

            int jsonChunkLength = swapInt(dis.readInt());
            int jsonChunkType = dis.readInt();
            if (jsonChunkType != 0x4A534F4E) throw new IOException("First chunk is not json");

            byte[] jsonBytes = new byte[jsonChunkLength];
            dis.readFully(jsonBytes);
            String json = new String(jsonBytes, StandardCharsets.UTF_8);
            JsonElement element = JsonParser.parseString(json);

            DataResult<GltfRoot> result = GltfRoot.CODEC.parse(JsonOps.INSTANCE, element);
            if (!result.hasResultOrPartial())
                throw new IOException("Error parsing glb json: " + result.error().get().message());
            if (result.isError()) {
                KGltfLog.LOG.warn("Encountered salvageable error while parsing glb json: {}",
                    result.error().get().message());
            }

            GltfRoot root = result.getPartialOrThrow();
            checkImages(root);

            if (fileLength > jsonChunkLength + 12) {
                // binary blob is included too
                int binChunkLength = swapInt(dis.readInt());
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
                continue;
            } else {
                throw new IOException("Invalid Minecraft glTF image " + i + " uri: '" + uri +
                    "'. Allowed uri types: 'rl:', 'resourcelocation:', 'id:', 'identifier:', and 'data:'.");
            }

            try {
                ResourceLocation.parse(rlStr);
            } catch (ResourceLocationException e) {
                throw new IOException("Invalid uri resource location: '" + rlStr + "' in image " + i, e);
            }
        }
    }

    public BufferAccess getRawBufferView(int index) throws IOException {
        List<GltfBufferView> bufferViews = root.bufferViews();
        if (index < 0 || index >= bufferViews.size())
            throw new IOException("Attempted to access buffer view " + index + " which does not exist");
        GltfBufferView view = bufferViews.get(index);

        List<GltfBuffer> buffers = root.buffers();
        if (view.buffer() < 0 || view.buffer() >= buffers.size())
            throw new IOException("Buffer view " + index + " has invalid buffer index " + view.buffer());
        GltfBuffer buffer = buffers.get(view.buffer());
        if (buffer.uri().isPresent()) {
            String uri = buffer.uri().get();

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
                int start = uri.indexOf(';');
                String base64 = uri.substring(start + 1);
                try {
                    return DenseArrayBufferAccess.fromBase64(base64, (int) view.byteOffset(),
                        (int) buffer.byteLength());
                } catch (IllegalArgumentException e) {
                    throw new IOException("Data url in buffer " + view.buffer() + " contained invalid base-64 data", e);
                }
            } else {
                throw new IOException("Invalid Minecraft glTF buffer " + view.buffer() + " uri: '" + uri +
                    "'. Allowed uri types: 'rl:', 'resourcelocation:', 'id:', 'identifier:', and 'data:'.");
            }

            ResourceLocation bufferLocation;
            try {
                bufferLocation = ResourceLocation.parse(rlStr);
            } catch (ResourceLocationException e) {
                throw new IOException("Invalid uri resource location: '" + rlStr + "' in buffer " + index, e);
            }
            byte[] data = dependencies.get(bufferLocation);
            return new DenseArrayBufferAccess(data, (int) view.byteOffset(), (int) view.byteLength());
        } else {
            byte[] data = this.buffer;
            if (data == null) throw new IOException("Attempted to access GLB buffer, but this file is not a GLB file");
            return new DenseArrayBufferAccess(data, (int) view.byteOffset(), (int) view.byteLength());
        }
    }

    public @Nullable ResourceLocation getImageLocation(int index) throws IOException {
        List<GltfImage> images = root.images();
        if (index < 0 || index >= images.size())
            throw new IOException("Attempted to access image " + index + " which does not exist");
        GltfImage image = images.get(index);
        if (image.bufferView().isPresent()) {
            return null;
        } else if (image.uri().isPresent()) {
            String uri = image.uri().get();

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
                return null;
            } else {
                throw new IOException("Invalid Minecraft glTF image " + index + " uri: '" + uri +
                    "'. Allowed uri types: 'rl:', 'resourcelocation:', 'id:', 'identifier:', and 'data:'.");
            }

            try {
                return ResourceLocation.parse(rlStr);
            } catch (ResourceLocationException e) {
                throw new IOException("Invalid uri resource location: '" + rlStr + "' in image " + index);
            }
        } else {
            throw new IOException("Image " + index + " does not have a uri or buffer referenced");
        }
    }

    public @Nullable BufferAccess getImageBuffer(int index) throws IOException {
        List<GltfImage> images = root.images();
        if (index < 0 || index >= images.size())
            throw new IOException("Attempted to access image " + index + " which does not exist");
        GltfImage image = images.get(index);
        if (image.bufferView().isPresent()) {
            return getRawBufferView(image.bufferView().getAsInt());
        } else if (image.uri().isPresent()) {
            String uri = image.uri().get();

            if (uri.startsWith("rl:") || uri.startsWith("resourcelocation:") || uri.startsWith("id:") ||
                uri.startsWith("identifier:")) {
                return null;
            } else if (uri.startsWith("data:")) {
                int start = uri.indexOf(';');
                String base64 = uri.substring(start + 1);
                try {
                    return DenseArrayBufferAccess.fromBase64(base64);
                } catch (IllegalArgumentException e) {
                    throw new IOException("Data url in image " + index + " contained invalid base-64 data", e);
                }
            } else {
                throw new IOException("Invalid Minecraft glTF image " + index + " uri: '" + uri +
                    "'. Allowed uri types: 'rl:', 'resourcelocation:', 'id:', 'identifier:', and 'data:'.");
            }
        } else {
            throw new IOException("Image " + index + " does not have a uri or buffer referenced");
        }
    }

    public Accessor getAccessor(int index) throws IOException {
        List<GltfAccessor> accessors = root.accessors();
        if (index < 0 || index >= accessors.size())
            throw new IOException("Attempted to access accessor " + index + " which does not exist");
        GltfAccessor accessor = accessors.get(index);

        if (accessor.bufferView().isPresent()) {
            int viewIndex = accessor.bufferView().getAsInt();
            List<GltfBufferView> views = root.bufferViews();
            if (viewIndex < 0 || viewIndex >= views.size()) throw new IOException(
                "Accessor " + index + " references buffer view " + accessor.bufferView() + " which does not exist");
            GltfBufferView view = views.get(viewIndex);

            BufferAccess access = getRawBufferView(viewIndex);

            if (view.byteStride().isPresent()) {
                access =
                    new StrideBufferAccess(access, (int) view.byteStride().getAsLong(), (int) accessor.byteOffset(),
                        (int) accessor.count(), accessor.componentType(), accessor.type(), true);
            }

            if (accessor.sparse().isPresent()) {
                GltfAccessorSparse sparse = accessor.sparse().get();
                access = new SparseBufferAccess(access, accessor.componentType(), accessor.type(), (int) sparse.count(),
                    getRawBufferView(sparse.indices().bufferView()), (int) sparse.indices().byteOffset(),
                    sparse.indices().componentType(), getRawBufferView(sparse.values().bufferView()),
                    (int) sparse.values().byteOffset());
            }

            return new Accessor(accessor, access);
        } else if (accessor.sparse().isPresent()) {
            GltfAccessorSparse sparse = accessor.sparse().get();
            return new Accessor(accessor,
                new SparseBufferAccess(null, accessor.componentType(), accessor.type(), (int) sparse.count(),
                    getRawBufferView(sparse.indices().bufferView()), (int) sparse.indices().byteOffset(),
                    sparse.indices().componentType(), getRawBufferView(sparse.values().bufferView()),
                    (int) sparse.values().byteOffset()));
        } else {
            throw new IOException("Accessor " + index + " does not reference a buffer and is not sparse");
        }
    }

    public record Accessor(GltfAccessor accessor, BufferAccess buffer) {}
}
