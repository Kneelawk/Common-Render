package com.kneelawk.krender.engine.fabric.impl;

import net.fabricmc.api.ClientModInitializer;

import net.minecraft.client.renderer.item.ItemModels;

import com.kneelawk.krender.engine.impl.KREConstants;
import com.kneelawk.krender.engine.impl.model.ModelCoreItemModel;

public class ClientEntrypoint implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ItemModels.ID_MAPPER.put(KREConstants.prl("model"), ModelCoreItemModel.Unbaked.MAP_CODEC);
    }
}
