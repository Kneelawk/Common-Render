package com.kneelawk.krender.engine.backend.frapi.impl.material;

import net.fabricmc.fabric.api.renderer.v1.Renderer;

import net.minecraft.resources.ResourceLocation;

import com.kneelawk.krender.engine.backend.frapi.impl.FRAPIRenderer;
import com.kneelawk.krender.engine.base.material.BaseMaterialManager;

public class FRAPIMaterialManager extends BaseMaterialManager<FRAPIRenderMaterial> {
    public FRAPIMaterialManager() {
        super(FRAPIRenderer.INSTNACE, FRAPIRenderMaterial::new);
    }

    @Override
    protected boolean registerMaterialImpl(ResourceLocation id, FRAPIRenderMaterial material) {
        boolean res = super.registerMaterialImpl(id, material);
        if (res) {
            Renderer.get().registerMaterial(id, material.material);
        }
        return res;
    }

    @Override
    protected boolean registerOrUpdateMaterialImpl(ResourceLocation id, FRAPIRenderMaterial material) {
        Renderer.get().registerMaterial(id, material.material);
        return super.registerOrUpdateMaterialImpl(id, material);
    }
}
