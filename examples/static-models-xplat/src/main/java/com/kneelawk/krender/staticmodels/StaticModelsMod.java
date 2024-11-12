package com.kneelawk.krender.staticmodels;

import com.kneelawk.commonevents.api.Listen;
import com.kneelawk.commonevents.api.Scan;
import com.kneelawk.kregistry.core.api.KRegistrarSet;
import com.kneelawk.kregistry.core.api.RegisterCallback;

@Scan
public class StaticModelsMod {
    public static final KRegistrarSet REGISTRARS = new KRegistrarSet(SMConstants.MOD_ID);

    @Listen(RegisterCallback.class)
    public static void registerRegistrars(RegisterCallback.Context ctx) {
        ctx.register(REGISTRARS);
    }
}
