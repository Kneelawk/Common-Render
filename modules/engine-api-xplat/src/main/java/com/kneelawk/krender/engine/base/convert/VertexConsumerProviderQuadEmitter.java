package com.kneelawk.krender.engine.base.convert;

import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.VertexConsumer;

import com.kneelawk.krender.engine.api.KRenderer;
import com.kneelawk.krender.engine.api.material.RenderMaterial;
import com.kneelawk.krender.engine.base.buffer.BaseQuadFormat;
import com.kneelawk.krender.engine.base.buffer.RootQuadEmitter;

/**
 * Quad emitter that wraps a function from {@link RenderMaterial} to {@link VertexConsumer}.
 */
public class VertexConsumerProviderQuadEmitter extends RootQuadEmitter {
    private final Function<RenderMaterial, VertexConsumer> provider;
    private final VertexConsumerEmitter emitter = new VertexConsumerEmitter();

    private @Nullable RenderMaterial prevMaterial;
    private @Nullable VertexConsumer prevConsumer;

    /**
     * Creates a new {@link VertexConsumerProviderQuadEmitter}.
     *
     * @param renderer the renderer that this quad emitter will be associated with.
     * @param provider the function from {@link RenderMaterial} to {@link VertexConsumer}.
     */
    public VertexConsumerProviderQuadEmitter(KRenderer renderer,
                                             Function<RenderMaterial, VertexConsumer> provider) {
        super(renderer);
        this.provider = provider;
        begin(new int[BaseQuadFormat.TOTAL_STRIDE], 0);
    }

    @Override
    public void emitDirectly() {
        RenderMaterial mat = getMaterial();

        VertexConsumer consumer;
        if (!mat.equals(prevMaterial) || prevConsumer == null) {
            consumer = provider.apply(mat);
            prevMaterial = mat;
            prevConsumer = consumer;
        } else {
            consumer = prevConsumer;
        }

        emitter.emitQuad(this, consumer);
    }
}
