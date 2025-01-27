package com.kneelawk.krender.engine.backend.neoforge.impl.mesh;

import com.kneelawk.krender.engine.base.buffer.TransformStack;

public class NFTransformStack extends TransformStack {
    public NFTransformStack(BaseKRendererApi renderer) {
        super(renderer, NFTransformingQuadEmitter::new);
    }
}
