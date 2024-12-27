package com.kneelawk.krender.ctcomplicated.client;

import com.kneelawk.krender.model.loading.api.ModelManagerPlugin;

import static com.kneelawk.krender.ctcomplicated.CTConstants.rl;

public class CTComplicatedClient {
    public static void init() {
        ModelManagerPlugin.register(ctx -> {
            ctx.addReferenceableModel(rl("block/ct_glass"), new CTGlassUnbakedModel());
            ctx.addReferenceableModel(rl("block/disco_floor"), new DiscoFloorUnbakedModel());
            ctx.addReferenceableModel(rl("item/disco_floor"), new DiscoFloorUnbakedModel());
        });
    }

    public static void syncInit() {

    }
}
