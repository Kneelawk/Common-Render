package com.kneelawk.krender.ctcomplicated;

import com.kneelawk.kregistry.core.api.KRegistrarSet;
import com.kneelawk.krender.ctcomplicated.block.CTBlocks;
import com.kneelawk.krender.ctcomplicated.misc.CTCreativeTabs;

public class CTComplicated {
    public static final KRegistrarSet REGISTRARS = new KRegistrarSet(CTConstants.MOD_ID);

    public static void init() {
        CTBlocks.register();
        CTCreativeTabs.register();
    }

    public static void initSync() {
    }
}
