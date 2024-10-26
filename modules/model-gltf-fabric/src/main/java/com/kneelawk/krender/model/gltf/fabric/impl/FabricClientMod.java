package com.kneelawk.krender.model.gltf.fabric.impl;

import net.fabricmc.api.ClientModInitializer;

import com.kneelawk.krender.model.gltf.impl.KGltf;

public class FabricClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        KGltf.register();
    }
}
