package com.kneelawk.krender.model.gltf.impl;

import net.minecraft.resources.ResourceLocation;

public class KGltfConstants {
    public static final String MOD_ID = "krender_model_gltf";
    public static final String PARENT_ID = "krender";

    public static final ResourceLocation LOADER_ID = prl("gltf");

    public static ResourceLocation prl(String path) {
        return ResourceLocation.fromNamespaceAndPath(PARENT_ID, path);
    }

    public static ResourceLocation getImageName(ResourceLocation modelId, int imageIndex) {
        return modelId.withSuffix("/images/" + imageIndex);
    }
}
