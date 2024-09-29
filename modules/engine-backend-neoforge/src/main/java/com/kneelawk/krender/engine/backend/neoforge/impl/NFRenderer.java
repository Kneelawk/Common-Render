package com.kneelawk.krender.engine.backend.neoforge.impl;

import org.jetbrains.annotations.NotNull;

import com.kneelawk.krender.engine.api.convert.TypeConverter;
import com.kneelawk.krender.engine.api.mesh.MeshBuilder;
import com.kneelawk.krender.engine.api.model.BakedModelFactory;
import com.kneelawk.krender.engine.backend.neoforge.impl.material.NFRenderMaterial;
import com.kneelawk.krender.engine.backend.neoforge.impl.model.NFBakedModelFactory;
import com.kneelawk.krender.engine.base.BaseKRendererApi;
import com.kneelawk.krender.engine.base.convert.BaseTypeConverter;
import com.kneelawk.krender.engine.base.material.BaseMaterialManager;
import com.kneelawk.krender.engine.base.material.BaseMaterialManagerApi;
import com.kneelawk.krender.engine.base.mesh.BaseMeshBuilder;

public class NFRenderer implements BaseKRendererApi {
    public static final NFRenderer INSTANCE = new NFRenderer();

    public final NFBakedModelFactory bakedModelFactory = new NFBakedModelFactory();
    public final BaseMaterialManager<NFRenderMaterial> materialManater =
        new BaseMaterialManager<>(INSTANCE, NFRenderMaterial::new);
    public final BaseTypeConverter typeConverter = new BaseTypeConverter(this);

    @Override
    public @NotNull BakedModelFactory bakedModelFactory() {
        return bakedModelFactory;
    }

    @Override
    public @NotNull MeshBuilder meshBuilder() {
        return new BaseMeshBuilder(this);
    }

    @Override
    public @NotNull BaseMaterialManagerApi<NFRenderMaterial> materialManager() {
        return materialManater;
    }

    @Override
    public @NotNull TypeConverter converter() {
        return typeConverter;
    }
}
