package com.kneelawk.krender.model.gltf.neoforge.impl;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import com.kneelawk.krender.model.gltf.impl.KGltf;
import com.kneelawk.krender.model.gltf.impl.KGltfConstants;

@EventBusSubscriber(modid = KGltfConstants.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class NeoForgeClientMod {
    @SubscribeEvent
    public static void onClientInit(FMLClientSetupEvent event) {
//        System.out.println("Client init:");
//        KGltf.register();
//        event.enqueueWork(KGltf::registerSync);
    }
}
