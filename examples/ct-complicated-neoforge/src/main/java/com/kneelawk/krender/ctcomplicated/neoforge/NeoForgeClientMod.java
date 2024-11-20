package com.kneelawk.krender.ctcomplicated.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;

import com.kneelawk.krender.ctcomplicated.CTConstants;
import com.kneelawk.krender.ctcomplicated.client.CTComplicatedClient;

@EventBusSubscriber(value = Dist.CLIENT, modid = CTConstants.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class NeoForgeClientMod {
    @SubscribeEvent
    public static void onInit(FMLConstructModEvent event) {
        CTComplicatedClient.init();
        event.enqueueWork(CTComplicatedClient::syncInit);
    }
}
