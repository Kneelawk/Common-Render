package com.kneelawk.krender.engine.backend.frapi.impl;

import org.jetbrains.annotations.NotNull;

import com.kneelawk.krender.engine.api.KRenderer;
import com.kneelawk.krender.engine.api.material.MaterialManager;
import com.kneelawk.krender.engine.api.model.BakedModelFactory;
import com.kneelawk.krender.engine.backend.frapi.impl.material.FRAPIMaterialManager;
import com.kneelawk.krender.engine.backend.frapi.impl.model.FRAPIBakedModelFactory;

public class FRAPIRenderer implements KRenderer {
    public static final FRAPIRenderer INSTNACE = new FRAPIRenderer();

    @Override
    public @NotNull BakedModelFactory bakedModelFactory() {
        return FRAPIBakedModelFactory.INSTANCE;
    }

    @Override
    public @NotNull MaterialManager materialManager() {
        return FRAPIMaterialManager.INSTANCE;
    }
}
