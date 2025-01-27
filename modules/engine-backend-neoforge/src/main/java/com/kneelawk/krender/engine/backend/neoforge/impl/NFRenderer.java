package com.kneelawk.krender.engine.backend.neoforge.impl;

import org.jetbrains.annotations.NotNull;

import com.kneelawk.krender.engine.api.convert.TypeConverter;
import com.kneelawk.krender.engine.api.mesh.MeshBuilder;
import com.kneelawk.krender.engine.api.model.BakedModelFactory;
import com.kneelawk.krender.engine.api.model.BakedModelUnwrapper;
import com.kneelawk.krender.engine.backend.neoforge.impl.material.NFRenderMaterial;
import com.kneelawk.krender.engine.backend.neoforge.impl.mesh.NFMeshBuilder;
import com.kneelawk.krender.engine.backend.neoforge.impl.model.NFBakedModelFactory;
import com.kneelawk.krender.engine.backend.neoforge.impl.model.NFUnwrapper;
import com.kneelawk.krender.engine.base.convert.BaseTypeConverter;
import com.kneelawk.krender.engine.base.material.BaseMaterialManager;

public class NFRenderer implements BaseKRendererApi {
    public static final NFRenderer INSTANCE = new NFRenderer();

    public final NFUnwrapper unwrapper = new NFUnwrapper();
    public final BaseMaterialManager<NFRenderMaterial> materialManater =
        new BaseMaterialManager<>(INSTANCE, NFRenderMaterial::new);
    public final BaseTypeConverter typeConverter = new BaseTypeConverter(this);

    @Override
    public @NotNull BakedModelFactory bakedModelFactory() {
        return new NFBakedModelFactory();
    }

    @Override
    public BakedModelUnwrapper bakedModelUnwrapper() {
        return unwrapper;
    }

    @Override
    public @NotNull MeshBuilder meshBuilder() {
        return new NFMeshBuilder(this);
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
