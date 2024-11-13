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
     * @return this quad emitter.
     */
    public TransformingQuadEmitter prepare(@UnknownNullability Object context, QuadTransform<?> transform,
                                           QuadEmitter output) {
        clear();
        this.context = context;
        this.transform = transform;
        this.output = output;
        reclaimed = false;
        return this;
    }

    @Override
    public <C> PooledQuadEmitter withTransformQuad(@UnknownNullability C context, QuadTransform<C> transform) {
        return transformStack.getTransform(context, transform, this);
    }

    @Override
    public <C> PooledVertexEmitter withTransformVertex(@UnknownNullability C context, QuadTransform<C> transform) {
        return transformStack.getTransform(context, transform, this);
    }

    @Override
    public void close() {
        flushVertices();
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
