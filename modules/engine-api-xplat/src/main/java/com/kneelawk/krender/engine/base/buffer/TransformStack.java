package com.kneelawk.krender.engine.base.buffer;

import java.util.function.BiFunction;

import org.jetbrains.annotations.UnknownNullability;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import com.kneelawk.krender.engine.api.buffer.QuadEmitter;
import com.kneelawk.krender.engine.api.buffer.QuadSink;
import com.kneelawk.krender.engine.api.buffer.QuadTransform;
import com.kneelawk.krender.engine.base.BaseKRendererApi;

/**
 * Manages the stacking of {@link TransformingQuadEmitter}s that can be returned by
 * {@link QuadSink#withTransformQuad(QuadTransform, Object)} or {@link QuadSink#withTransformVertex(QuadTransform, Object)}.
 */
public class TransformStack {
    /**
     * This stack's pool of unstacked quad emitters.
     */
    protected final ObjectArrayList<TransformingQuadEmitter> pool = new ObjectArrayList<>();

    /**
     * The renderer that all quad emitters from this stack will be associated with.
     */
    protected final BaseKRendererApi renderer;

    /**
     * The constructor for the type of transforming quad emitter this transform stack deals in.
     */
    protected final BiFunction<BaseKRendererApi, TransformStack, TransformingQuadEmitter> ctor;

    /**
     * Creates a new transform stack.
     *
     * @param renderer the renderer that all quad emitters from this stack will be associated with.
     */
    public TransformStack(BaseKRendererApi renderer) {
        this(renderer, TransformingQuadEmitter::new);
    }

    /**
     * Creates a new transform stack.
     *
     * @param renderer the renderer that all quad emitters from this stack will be associated with.
     * @param ctor     constructor for the type of transforming quad emitter this transform stack deals in.
     */
    protected TransformStack(BaseKRendererApi renderer,
                             BiFunction<BaseKRendererApi, TransformStack, TransformingQuadEmitter> ctor) {
        this.renderer = renderer;
        this.ctor = ctor;
    }

    /**
     * Gets or creates a new transforming quad emitter with the given context, transform, and output.
     *
     * @param transform the quad transform itself.
     * @param context   the context supplied to the transform.
     * @param output    the quad emitter to write transformed quads to.
     * @return the newly prepared transforming quad emitter.
     */
    public TransformingQuadEmitter getTransform(QuadTransform<?> transform, @UnknownNullability Object context,
                                                QuadEmitter output) {
        return getTransform(transform, context, output, false);
    }

    /**
     * Gets or creates a new transforming quad emitter with the given transform, context, and output.
     *
     * @param transform the quad transform itself.
     * @param context   the context supplied to the transform.
     * @param output    the quad emitter to write transformed quads to.
     * @param chaining  whether the returned quad emitter should close its output.
     * @return the newly prepared transforming quad emitter.
     */
    public TransformingQuadEmitter getTransform(QuadTransform<?> transform, @UnknownNullability Object context,
                                                QuadEmitter output, boolean chaining) {
        return (pool.isEmpty() ? ctor.apply(renderer, this) : pool.pop()).prepare(context, transform,
            output, chaining);
    }

    /**
     * Gets or creates a chain of new transforming quad emitters with the given transforms, contexts, and output.
     *
     * @param transforms the quad transforms.
     * @param contexts   the contexts supplied to their associated transforms.
     * @param output     the final output that all quads are emitted to.
     * @return the newly prepared transforming quad emitter chain.
     */
    public TransformingQuadEmitter getTransforms(QuadTransform<?>[] transforms, @UnknownNullability Object[] contexts,
                                                 QuadEmitter output) {
        int len = transforms.length;
        if (len != contexts.length) {
            throw new IllegalArgumentException("transforms and contexts must have equal length");
        }
        if (len < 1) {
            throw new IllegalArgumentException("there must be at least 1 transform");
        }
        if (len == 1) {
            return getTransform(transforms[0], contexts[0], output, false);
        }

        pool.ensureCapacity(pool.size() + transforms.length);

        TransformingQuadEmitter transforming = getTransform(transforms[0], contexts[0], output, false);
        for (int i = 1; i < len; i++) {
            transforming = getTransform(transforms[i], contexts[i], transforming, true);
        }

        return transforming;
    }

    /**
     * Reclaims a stacked {@link TransformingQuadEmitter} and adds it back to the pool of unstacked emitters.
     *
     * @param emitter the emitter to unstack.
     */
    public void reclaim(TransformingQuadEmitter emitter) {
        pool.add(emitter);
    }
}
