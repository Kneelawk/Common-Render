package com.kneelawk.krender.engine.backend.frapi.impl.model;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.resources.model.BakedModel;

import com.kneelawk.krender.engine.api.model.BakedModelCore;
import com.kneelawk.krender.engine.api.model.BakedModelUnwrapper;

public class FRAPIUnwrapper implements BakedModelUnwrapper {
    @Override
    public @Nullable BakedModelCore<?> unwrap(BakedModel model) {
        if (model.isVanillaAdapter()) return null;

        return new FRAPIUnwrappedModel(model);
    }
}
