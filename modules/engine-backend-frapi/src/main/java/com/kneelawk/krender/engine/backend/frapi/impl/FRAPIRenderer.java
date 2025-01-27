package com.kneelawk.krender.engine.backend.frapi.impl;

import org.jetbrains.annotations.NotNull;

import com.kneelawk.krender.engine.api.convert.TypeConverter;
import com.kneelawk.krender.engine.api.mesh.MeshBuilder;
import com.kneelawk.krender.engine.api.model.BakedModelFactory;
import com.kneelawk.krender.engine.api.model.BakedModelUnwrapper;
import com.kneelawk.krender.engine.backend.frapi.impl.material.FRAPIMaterialManager;
import com.kneelawk.krender.engine.backend.frapi.impl.material.FRAPIRenderMaterial;
import com.kneelawk.krender.engine.backend.frapi.impl.model.FRAPIBakedModelFactory;
import com.kneelawk.krender.engine.backend.frapi.impl.model.FRAPIUnwrapper;
import com.kneelawk.krender.engine.base.convert.BaseTypeConverter;
import com.kneelawk.krender.engine.base.mesh.BaseMeshBuilder;

public class FRAPIRenderer implements BaseKRendererApi {
    public static final FRAPIRenderer INSTNACE = new FRAPIRenderer();

    private final FRAPIUnwrapper unwrapper = new FRAPIUnwrapper();
    private final FRAPIMaterialManager materialManager = new FRAPIMaterialManager();
    private final BaseTypeConverter typeConverter = new BaseTypeConverter(this);

    @Override
    public @NotNull BakedModelFactory bakedModelFactory() {
        return new FRAPIBakedModelFactory();
    }

    @Override
    public BakedModelUnwrapper bakedModelUnwrapper() {
        return unwrapper;
    }

    @Override
    public @NotNull MeshBuilder meshBuilder() {
        return new BaseMeshBuilder(this);
    }

    @Override
    public @NotNull BaseMaterialManagerApi<FRAPIRenderMaterial> materialManager() {
        return materialManager;
    }

    @Override
    public @NotNull TypeConverter converter() {
        return typeConverter;
    }
}
