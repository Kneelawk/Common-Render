package com.kneelawk.krender.engine.backend.shim.material;

import com.kneelawk.krender.engine.backend.shim.ShimRenderer;
import com.kneelawk.krender.engine.base.material.BaseMaterialManager;

public class ShimMaterialManager extends BaseMaterialManager<ShimRenderMaterial> {
    public ShimMaterialManager() {
        super(ShimRenderer.INSTANCE, ShimRenderMaterial::new);
    }
}
