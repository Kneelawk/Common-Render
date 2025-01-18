package com.kneelawk.krender.engine.impl;

import net.minecraft.resources.ResourceLocation;

public class KREConstants {
    public static final String MOD_ID = "krender_engine_api";
    public static final String CONFIG_DIR = "krender";
    public static final String PARENT_MOD_ID = "krender";

    public static ResourceLocation prl(String path) {
        return ResourceLocation.fromNamespaceAndPath(PARENT_MOD_ID, path);
    }
}
