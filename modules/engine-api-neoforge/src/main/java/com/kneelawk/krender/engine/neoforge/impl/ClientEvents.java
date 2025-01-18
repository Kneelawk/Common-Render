package com.kneelawk.krender.engine.neoforge.impl;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterItemModelsEvent;

import com.kneelawk.krender.engine.impl.KREConstants;
import com.kneelawk.krender.engine.impl.model.ModelCoreItemModel;

@EventBusSubscriber(value = Dist.CLIENT, modid = KREConstants.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ClientEvents {
    @SubscribeEvent
    public static void onRegisterItemModels(RegisterItemModelsEvent event) {
        event.register(KREConstants.prl("model"), ModelCoreItemModel.Unbaked.MAP_CODEC);
    }
}
