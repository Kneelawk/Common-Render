package com.kneelawk.krender.engine.base.convert;

import java.util.function.Function;

import com.mojang.blaze3d.vertex.VertexConsumer;

import com.kneelawk.krender.engine.api.material.RenderMaterial;
import com.kneelawk.krender.engine.base.BaseKRendererApi;
import com.kneelawk.krender.engine.base.buffer.RootQuadEmitter;

/**
 * Quad emitter that wraps a function from {@link RenderMaterial} to {@link VertexConsumer}.
 */
public class VertexConsumerProviderQuadEmitter extends RootQuadEmitter {
    private final Function<RenderMaterial, VertexConsumer> provider;
    private final VertexConsumerEmitter emitter = new VertexConsumerEmitter();

    /**
     * Creates a new {@link VertexConsumerProviderQuadEmitter}.
     *
     * @param renderer the renderer that this quad emitter will be associated with.
     * @param provider the function from {@link RenderMaterial} to {@link VertexConsumer}.
     */
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
