package com.kneelawk.krender.ctcomplicated.client;

import com.kneelawk.commonevents.api.Listen;
import com.kneelawk.commonevents.api.Scan;
import com.kneelawk.krender.model.loading.api.ModelBakeryInitCallback;
import com.kneelawk.krender.model.loading.api.ModelBakeryPlugin;

import static com.kneelawk.krender.ctcomplicated.CTConstants.rl;

@Scan(side = Scan.Side.CLIENT)
public class CTComplicatedClient {
    @Listen(ModelBakeryInitCallback.class)
    public static void registerModels(ModelBakeryInitCallback.Context ctx0) {
        ModelBakeryPlugin.register(ctx -> {
            ctx.addLowLevelModel(rl("block/ct_glass"), new CTGlassUnbakedModel());
            ctx.addLowLevelModel(rl("block/disco_floor"), new DiscoFloorUnbakedModel());
            ctx.addLowLevelModel(rl("item/disco_floor"), new DiscoFloorUnbakedModel());
        });
    }

    public static void init() {

    }

    public static void syncInit() {

    }
}
