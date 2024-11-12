package com.kneelawk.krender.staticmodels;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class SMConstants {
    public static final String MOD_ID = "static_models";

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static MutableComponent tt(String prefix, String suffix, Object... args) {
        return Component.translatable(prefix + "." + MOD_ID + "." + suffix, args);
    }
}
