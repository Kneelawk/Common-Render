package com.kneelawk.krender.model.gltf.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.MapCodec;

import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceMetadata;

import com.kneelawk.krender.model.guard.api.ModelGuards;

public class GltfSpriteSource implements SpriteSource {
    public static final GltfSpriteSource INSTANCE = new GltfSpriteSource();
    public static final MapCodec<GltfSpriteSource> MAP_CODEC = MapCodec.unit(INSTANCE);
    public static final SpriteSourceType TYPE = new SpriteSourceType(MAP_CODEC);

    @Override
    public void run(ResourceManager resourceManager, Output output) {
        ModelGuards guards = ModelGuards.load(resourceManager);
        Map<ResourceLocation, Resource> gltfResources =
            guards.getModels(resourceManager, KGltfConstants.LOADER_ID, ".gltf");
        Map<ResourceLocation, Resource> glbResources =
            guards.getModels(resourceManager, KGltfConstants.LOADER_ID, ".glb");

        for (var entry : gltfResources.entrySet()) {
            try {
                GltfFile file = GltfFile.loadGltf(entry.getValue(), resourceManager, guards);
                addImages(output, entry.getKey(), file);
            } catch (Exception e) {
                KGltfLog.LOG.error("Error loading glTF file {}", entry.getKey(), e);
            }
        }

        for (var entry : glbResources.entrySet()) {
            try {
                GltfFile file = GltfFile.loadGlb(entry.getValue(), resourceManager, guards);
                addImages(output, entry.getKey(), file);
            } catch (Exception e) {
                KGltfLog.LOG.error("Error loading glb file {}", entry.getKey(), e);
            }
        }
    }

    private static void addImages(Output output, ResourceLocation modelName, GltfFile file)
        throws IOException {
        int imageCount = file.root().images().size();
        for (int i = 0; i < imageCount; i++) {
            BufferAccess access = file.getImageBuffer(i);
            if (access != null) {
                ResourceLocation imageName = KGltfConstants.getImageName(modelName, i);
                output.add(imageName, new BufferAccessSpriteSupplier(imageName, access));
            }
        }
    }

    @Override
    public SpriteSourceType type() {
        return TYPE;
    }

    private record BufferAccessSpriteSupplier(ResourceLocation name, BufferAccess buffer) implements SpriteSupplier {
        @Override
        public SpriteContents apply(SpriteResourceLoader spriteResourceLoader) {
            NativeImage image;
            try (InputStream is = buffer.createStream()) {
                image = NativeImage.read(is);
            } catch (IOException e) {
                KGltfLog.LOG.error("Error loading glTF image {}", name, e);
                return null;
            }

            return new SpriteContents(name, new FrameSize(image.getWidth(), image.getHeight()), image,
                ResourceMetadata.EMPTY);
        }
    }
}
