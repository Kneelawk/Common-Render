package com.kneelawk.krender.ctcomplicated.client;

import com.kneelawk.krender.model.loading.api.ModelBakeryPlugin;

import static com.kneelawk.krender.ctcomplicated.CTConstants.rl;

public class CTComplicatedClient {
    public static void init() {
        ModelBakeryPlugin.register(ctx -> {
            ctx.addLowLevelModel(rl("block/ct_glass"), new CTGlassUnbakedModel());
            ctx.addLowLevelModel(rl("block/disco_floor"), new DiscoFloorUnbakedModel());
            ctx.addLowLevelModel(rl("item/disco_floor"), new DiscoFloorUnbakedModel());
        });
    }

    public static void syncInit() {

    }
}
