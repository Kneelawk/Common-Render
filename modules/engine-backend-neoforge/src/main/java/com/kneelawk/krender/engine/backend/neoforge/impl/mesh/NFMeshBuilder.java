package com.kneelawk.krender.engine.backend.neoforge.impl.mesh;

import com.kneelawk.krender.engine.base.buffer.BaseQuadEmitter;
import com.kneelawk.krender.engine.base.buffer.BaseQuadFormat;
import com.kneelawk.krender.engine.base.mesh.BaseMeshBuilder;

public class NFMeshBuilder extends BaseMeshBuilder {
    public NFMeshBuilder(BaseKRendererApi renderer) {
        super(renderer);
    }

    @Override
    protected BaseQuadEmitter createMaker(BaseKRendererApi renderer) {
        return new Maker(renderer);
    }

    private class Maker extends NFRootQuadEmitter {
        public Maker(BaseKRendererApi renderer) {
            super(renderer);
        }

        @Override
        public void emitDirectly() {
            baseIndex = increment(BaseQuadFormat.TOTAL_STRIDE);
        }
    }
}
