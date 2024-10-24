package com.kneelawk.krender.model.guard.impl;

import net.minecraft.resources.ResourceLocation;

public class KRMGConstants {
    public static final String MOD_ID = "krender_model_guard";
    public static final String PARENT_MOD_ID = "krender";

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static ResourceLocation prl(String path) {
        return ResourceLocation.fromNamespaceAndPath(PARENT_MOD_ID, path);
    }
}
