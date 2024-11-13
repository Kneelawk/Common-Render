package com.kneelawk.krender.engine.api.util.transform;

import org.jetbrains.annotations.UnknownNullability;

import com.kneelawk.krender.engine.api.buffer.QuadEmitter;
import com.kneelawk.krender.engine.api.buffer.QuadTransform;
import com.kneelawk.krender.engine.api.buffer.QuadView;
import com.kneelawk.krender.engine.api.material.RenderMaterial;

/**
 * Quad transform that sets the material of everything passed through.
 */
public class MaterialQuadTransform implements QuadTransform<RenderMaterial> {
    private static final MaterialQuadTransform INSTANCE = new MaterialQuadTransform();

    private MaterialQuadTransform() {}

    /**
     * {@return the quad transform instance}
     */
    public static MaterialQuadTransform getInstance() {
        return INSTANCE;
    }

    @Override
    public void transform(@UnknownNullability RenderMaterial context, QuadView input, QuadEmitter output) {
        input.copyTo(output);
        output.setMaterial(context);
        output.emit();
    }
}
