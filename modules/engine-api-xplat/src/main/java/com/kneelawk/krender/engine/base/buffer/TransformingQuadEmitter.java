package com.kneelawk.krender.engine.base.buffer;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import com.kneelawk.krender.engine.api.buffer.PooledQuadEmitter;
import com.kneelawk.krender.engine.api.buffer.PooledVertexEmitter;
import com.kneelawk.krender.engine.api.buffer.QuadEmitter;
import com.kneelawk.krender.engine.api.buffer.QuadTransform;
import com.kneelawk.krender.engine.base.BaseKRendererApi;

/**
 * A quad emitter that transforms quads before emitting them to the next emitter.
 */
public class TransformingQuadEmitter extends BaseQuadEmitter implements PooledQuadEmitter, PooledVertexEmitter {
    /**
     * The transform stack that this emitter is from.
     */
    protected final TransformStack transformStack;
    /**
     * Whether this emitter has been returned to the stack.
     */
    protected boolean reclaimed;

    /**
     * The context object passed to the quad transform.
     */
    protected @UnknownNullability Object context;
    /**
     * The quad transform itself.
     */
    protected @Nullable QuadTransform<?> transform;
    /**
     * The emitter that the transform will write to.
     */
    protected @Nullable QuadEmitter output;
    /**
     * Whether output should be closed when this is closed.
     */
    protected boolean chaining;

    /**
     * Creates a new transforming quad emitter. This should only be called by {@link TransformStack}.
     *
     * @param renderer       the renderer that this emitter is to be associated with.
     * @param transformStack the transform stack that this emitter came from and will be returned to.
     */
    public TransformingQuadEmitter(BaseKRendererApi renderer, TransformStack transformStack) {
        super(renderer);
        this.transformStack = transformStack;
        begin(new int[BaseQuadFormat.TOTAL_STRIDE], 0);
    }

    /**
     * Prepares this transforming quad emitter for use in a transform stack.
     *
     * @param context   the context to pass to the quad transform.
     * @param transform the quad transform itself.
     * @param output    the quad emitter to emit finished quads to.
     * @param chaining  whether output should be closed when this is closed.
     * @return this quad emitter.
     */
    public TransformingQuadEmitter prepare(@UnknownNullability Object context, QuadTransform<?> transform,
                                           QuadEmitter output, boolean chaining) {
        clear();
        this.context = context;
        this.transform = transform;
        this.output = output;
        this.chaining = chaining;
        reclaimed = false;
        return this;
    }

    @Override
    public PooledQuadEmitter withTransformQuad(QuadTransform<?>[] transforms, @UnknownNullability Object[] contexts) {
        return transformStack.getTransforms(transforms, contexts, this);
    }

    @Override
    public PooledVertexEmitter withTransformVertex(QuadTransform<?>[] transforms,
                                                   @UnknownNullability Object[] contexts) {
        return transformStack.getTransforms(transforms, contexts, this);
    }

    @Override
    public <C> PooledQuadEmitter withTransformQuad(QuadTransform<C> transform, @UnknownNullability C context) {
        return transformStack.getTransform(transform, context, this);
    }

    @Override
    public <C> PooledVertexEmitter withTransformVertex(QuadTransform<C> transform, @UnknownNullability C context) {
        return transformStack.getTransform(transform, context, this);
    }

    @Override
    public void close() {
        flushVertices();
        if (chaining && output != null) {
            output.close();
        }
        if (!reclaimed) {
            reclaimed = true;
            transformStack.reclaim(this);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void emitDirectly() {
        Objects.requireNonNull(transform, "This TransformingQuadEmitter has not been prepared!");
        Objects.requireNonNull(output, "This TransformingQuadEmitter has not been prepared!");

        ((QuadTransform<Object>) transform).transform(context, this, output);
    }

    @Override
    public boolean isTransformer() {
        return true;
    }
}
