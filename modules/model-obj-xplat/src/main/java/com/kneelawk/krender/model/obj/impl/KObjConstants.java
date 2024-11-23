package com.kneelawk.krender.model.obj.impl;

import net.minecraft.resources.ResourceLocation;

public class KObjConstants {
    public static final String MOD_ID = "krender_model_obj";
    public static final String PARENT_ID = "krender";

    public static final ResourceLocation LOADER_ID = prl("obj");

    public static ResourceLocation prl(String path) {
        return ResourceLocation.fromNamespaceAndPath(PARENT_ID, path);
    }
}
