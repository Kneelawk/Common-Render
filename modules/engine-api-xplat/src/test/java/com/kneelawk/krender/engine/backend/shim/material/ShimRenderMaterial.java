package com.kneelawk.krender.engine.backend.shim.material;

import org.jetbrains.annotations.Nullable;

import com.kneelawk.krender.engine.api.KRenderer;
import com.kneelawk.krender.engine.api.material.RenderMaterial;
import com.kneelawk.krender.engine.backend.shim.ShimRenderer;
import com.kneelawk.krender.engine.base.material.BaseMaterialView;

public class ShimRenderMaterial extends BaseMaterialView implements RenderMaterial {
    /**
     * Creates a new {@link BaseMaterialView} with the given bits.
     *
     * @param bits the bits representing this material.
     */
    public ShimRenderMaterial(int bits) {
        super(bits);
    }

    @Override
    public @Nullable KRenderer getRenderer() {
        return ShimRenderer.INSTANCE;
    }
}
