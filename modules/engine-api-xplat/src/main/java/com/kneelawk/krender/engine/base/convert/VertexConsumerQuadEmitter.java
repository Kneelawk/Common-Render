package com.kneelawk.krender.engine.base.convert;

import com.mojang.blaze3d.vertex.VertexConsumer;

import com.kneelawk.krender.engine.base.BaseKRendererApi;
import com.kneelawk.krender.engine.base.buffer.RootQuadEmitter;

/**
 * Quad emitter that wraps a vertex consumer.
 */
public class VertexConsumerQuadEmitter extends RootQuadEmitter {
    private final VertexConsumer consumer;
    private final VertexConsumerEmitter emitter = new VertexConsumerEmitter();

    /**
     * Creates a new {@link VertexConsumerQuadEmitter}.
     *
     * @param renderer the renderer this quad emitter is associated with.
     * @param consumer the vertex consumer to wrap.
     */
    public VertexConsumerQuadEmitter(BaseKRendererApi renderer, VertexConsumer consumer) {
        super(renderer);
        this.consumer = consumer;
    }

    @Override
    public void emitDirectly() {
        emitter.emitQuad(this, consumer);
    }
}
