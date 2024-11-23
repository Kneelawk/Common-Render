package com.kneelawk.krender.model.obj.neoforge.impl;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;

import com.kneelawk.krender.model.obj.impl.KObj;
import com.kneelawk.krender.model.obj.impl.KObjConstants;

@EventBusSubscriber(modid = KObjConstants.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class NeoForgeClientMod {
    @SubscribeEvent
    public static void onClientInit(FMLConstructModEvent event) {
        KObj.register();
    }
}
