package com.kneelawk.krender.model.gltf.neoforge.impl;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;

import com.kneelawk.krender.model.gltf.impl.KGltf;
import com.kneelawk.krender.model.gltf.impl.KGltfConstants;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD, modid = KGltfConstants.MOD_ID)
public class NeoForgeClientMod {
    @SubscribeEvent
    public static void onInit(FMLConstructModEvent event) {
        KGltf.init();
        event.enqueueWork(KGltf::initSync);
    }
}
