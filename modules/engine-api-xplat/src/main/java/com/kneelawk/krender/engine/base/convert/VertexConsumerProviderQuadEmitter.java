package com.kneelawk.krender.engine.base.convert;

import java.util.function.Function;

import com.mojang.blaze3d.vertex.VertexConsumer;

import com.kneelawk.krender.engine.api.material.RenderMaterial;
import com.kneelawk.krender.engine.base.BaseKRendererApi;
import com.kneelawk.krender.engine.base.buffer.RootQuadEmitter;

/**
 *
 */
public class VertexConsumerProviderQuadEmitter extends RootQuadEmitter {
    private final Function<RenderMaterial, VertexConsumer> provider;
    private final VertexConsumerEmitter emitter = new VertexConsumerEmitter();

    public VertexConsumerProviderQuadEmitter(BaseKRendererApi renderer,
                                             Function<RenderMaterial, VertexConsumer> provider) {
        super(renderer);
        this.provider = provider;
    }

    @Override
    public void emitDirectly() {
        RenderMaterial mat = getMaterial();
        VertexConsumer consumer = provider.apply(mat);

        emitter.emitQuad(this, consumer);
    }
}
