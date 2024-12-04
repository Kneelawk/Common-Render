package com.kneelawk.krender.engine.backend.shim;

import com.kneelawk.krender.engine.api.convert.TypeConverter;
import com.kneelawk.krender.engine.api.mesh.MeshBuilder;
import com.kneelawk.krender.engine.api.model.BakedModelFactory;
import com.kneelawk.krender.engine.api.model.BakedModelUnwrapper;
import com.kneelawk.krender.engine.backend.shim.material.ShimMaterialManager;
import com.kneelawk.krender.engine.base.BaseKRendererApi;
import com.kneelawk.krender.engine.base.material.BaseMaterialManagerApi;
import com.kneelawk.krender.engine.base.mesh.BaseMeshBuilder;

public class ShimRenderer implements BaseKRendererApi {
    public static final ShimRenderer INSTANCE = new ShimRenderer();

    private final ShimMaterialManager materialManager = new ShimMaterialManager();

    @Override
    public BakedModelFactory bakedModelFactory() {
        return null;
    }

    @Override
    public BakedModelUnwrapper bakedModelUnwrapper() {
        return null;
    }

    @Override
    public MeshBuilder meshBuilder() {
        return new BaseMeshBuilder(this);
    }

    @Override
    public BaseMaterialManagerApi<?> materialManager() {
        return materialManager;
    }

    @Override
    public TypeConverter converter() {
        return null;
    }
}
