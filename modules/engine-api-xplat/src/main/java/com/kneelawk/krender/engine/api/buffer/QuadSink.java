package com.kneelawk.krender.engine.api.buffer;

import java.util.ArrayList;

import org.jetbrains.annotations.UnknownNullability;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

import com.kneelawk.krender.engine.api.RendererDependent;

/**
 * Universal interface for API-users to send vertex data to a backend.
 * <p>
 * This supports both the per-quad style of the Fabric Render API and the per-vertex style of vanilla.
 */
public interface QuadSink extends MultiBufferSource, RendererDependent {
    /**
     * {@return a view of this quad sink as a per-quad model-data emitter}
     */
    QuadEmitter asQuadEmitter();

    /**
     * {@return a view of this quad sink as a per-vertex model-data emitter}
     */
    VertexEmitter asVertexEmitter();

    /**
     * Gets a view of this quad sink as a per-quad model-data emitter that transforms its quads with the given transforms.
     *
     * @param transforms the array of quad transforms that are applied to evey quad.
     * @param contexts   the array of contexts that are passed to their associated transforms.
     * @return the per-quad model-data emitter that transforms quads.
     */
    PooledQuadEmitter withTransformQuad(QuadTransform<?>[] transforms, @UnknownNullability Object[] contexts);

    /**
     * Gets a view of this quad sink as a per-vertex model-data emitter that transforms its quads with the given transforms.
     *
     * @param transforms the array of quad transforms that are applied to evey quad.
     * @param contexts   the array of contexts that are passed to their associated transforms.
     * @return the per-vertex model-data emitter that transforms quads.
     */
    PooledVertexEmitter withTransformVertex(QuadTransform<?>[] transforms, @UnknownNullability Object[] contexts);

    /**
     * Gets a view of this quad sink as a per-quad model-data emitter that transforms its quads with the given transform.
     *
     * @param <C>       the type of context to be passed to the transform.
     * @param transform the quad transform that applies to every quad.
     * @param context   the context to be passed to the transform.
     * @return the per-quad model-data emitter that transforms quads.
     */
    default <C> PooledQuadEmitter withTransformQuad(QuadTransform<C> transform, @UnknownNullability C context) {
        return withTransformQuad(new QuadTransform[]{transform}, new Object[]{context});
    }

    /**
     * Gets a view of this quad sink as a per-vertex model-data emitter that transforms its quads with the given transform.
     *
     * @param <C>       the type of context to be passed to the transform.
     * @param transform the quad transform that applies to every quad.
     * @param context   the context to be passed to the transform.
     * @return the per-vertex model-data emitter that transforms quads.
     */
    default <C> PooledVertexEmitter withTransformVertex(QuadTransform<C> transform, @UnknownNullability C context) {
        return withTransformVertex(new QuadTransform[]{transform}, new Object[]{context});
    }

    /**
     * Gets a view of this quad sink as a per-quad model-data emitter that transforms its quads with the given transforms.
     *
     * @param transform1 the first quad transform that applies to every quad.
     * @param context1   the context to be passed to the first transform.
     * @param transform2 the second quad transform that applies to every quad.
     * @param context2   the context to be passed to the second transform.
     * @param <C1>       the type of context to be passed to the first transform.
     * @param <C2>       the type of context to be passed to the second transform.
     * @return the per-quad model-data emitter that transforms quads.
     */
    default <C1, C2> PooledQuadEmitter withTransformQuad(QuadTransform<C1> transform1, @UnknownNullability C1 context1,
                                                         QuadTransform<C2> transform2,
                                                         @UnknownNullability C2 context2) {
        return withTransformQuad(new QuadTransform[]{transform1, transform2}, new Object[]{context1, context2});
    }

    /**
     * Gets a view of this quad sink as a per-vertex model-data emitter that transforms its quads with the given transforms.
     *
     * @param transform1 the first quad transform that applies to every quad.
     * @param context1   the context to be passed to the first transform.
     * @param transform2 the second quad transform that applies to every quad.
     * @param context2   the context to be passed to the second transform.
     * @param <C1>       the type of context to be passed to the first transform.
     * @param <C2>       the type of context to be passed to the second transform.
     * @return the per-vertex model-data emitter that transforms quads.
     */
    default <C1, C2> PooledVertexEmitter withTransformVertex(QuadTransform<C1> transform1,
                                                             @UnknownNullability C1 context1,
                                                             QuadTransform<C2> transform2,
                                                             @UnknownNullability C2 context2) {
        return withTransformVertex(new QuadTransform[]{transform1, transform2}, new Object[]{context1, context2});
    }

    /**
     * Gets a view of this quad sink as a per-quad model-data emitter that transforms its quads with the given transforms.
     *
     * @param transform1 the first quad transform that applies to every quad.
     * @param context1   the context to be passed to the first transform.
     * @param transform2 the second quad transform that applies to every quad.
     * @param context2   the context to be passed to the second transform.
     * @param transform3 the third quad transform that applies to every quad.
     * @param context3   the context to be passed to the third transform.
     * @param <C1>       the type of context passed to the first transform.
     * @param <C2>       the type of context passed to the second transform.
     * @param <C3>       the type of context passed to the third transform.
     * @return the per-quad model-data emitter that transforms quads.
     */
    default <C1, C2, C3> PooledQuadEmitter withTransformQuad(QuadTransform<C1> transform1,
                                                             @UnknownNullability C1 context1,
                                                             QuadTransform<C2> transform2,
                                                             @UnknownNullability C2 context2,
                                                             QuadTransform<C3> transform3,
                                                             @UnknownNullability C3 context3) {
        return withTransformQuad(new QuadTransform[]{transform1, transform2, transform3},
            new Object[]{context1, context2, context3});
    }

    /**
     * Gets a view of this quad sink as a per-vertex model-data emitter that transforms its quads with the given transforms.
     *
     * @param transform1 the first quad transform that applies to every quad.
     * @param context1   the context to be passed to the first transform.
     * @param transform2 the second quad transform that applies to every quad.
     * @param context2   the context to be passed to the second transform.
     * @param transform3 the third quad transform that applies to every quad.
     * @param context3   the context to be passed to the third transform.
     * @param <C1>       the type of context passed to the first transform.
     * @param <C2>       the type of context passed to the second transform.
     * @param <C3>       the type of context passed to the third transform.
     * @return the per-vertex model-data emitter that transforms quads.
     */
    default <C1, C2, C3> PooledVertexEmitter withTransformVertex(QuadTransform<C1> transform1,
                                                                 @UnknownNullability C1 context1,
                                                                 QuadTransform<C2> transform2,
                                                                 @UnknownNullability C2 context2,
                                                                 QuadTransform<C3> transform3,
                                                                 @UnknownNullability C3 context3) {
        return withTransformVertex(new QuadTransform[]{transform1, transform2, transform3},
            new Object[]{context1, context2, context3});
    }

    /**
     * Gets a view of this quad sink as a per-quad model-data emitter that transforms its quads with the given transforms.
     *
     * @param transform1            the first quad transform that applies to every quad.
     * @param context1              the context to be passed to the first transform.
     * @param transform2            the second quad transform that applies to every quad.
     * @param context2              the context to be passed to the second transform.
     * @param transform3            the third quad transform that applies to every quad.
     * @param context3              the context to be passed to the third transform.
     * @param transformsAndContexts the remaining transforms and their associated contexts, alternating between transform and context.
     * @param <C1>                  the type of context passed to the first transform.
     * @param <C2>                  the type of context passed to the second transform.
     * @param <C3>                  the type of context passed to the third transform.
     * @return the per-quad model-data emitter that transforms quads.
     */
    default <C1, C2, C3> PooledQuadEmitter withTransformQuad(QuadTransform<C1> transform1,
                                                             @UnknownNullability C1 context1,
                                                             QuadTransform<C2> transform2,
                                                             @UnknownNullability C2 context2,
                                                             QuadTransform<C3> transform3,
                                                             @UnknownNullability C3 context3,
                                                             Object... transformsAndContexts) {
        int len = transformsAndContexts.length;

        ArrayList<QuadTransform<?>> transforms = new ArrayList<>(len / 2 + 3);
        ArrayList<Object> contexts = new ArrayList<>(len / 2 + 3);
        transforms.add(transform1);
        contexts.add(context1);
        transforms.add(transform2);
        contexts.add(context2);
        transforms.add(transform3);
        contexts.add(context3);

        for (int i = 0; i < len; i++) {
            if (i % 2 == 0) {
                transforms.add((QuadTransform<?>) transformsAndContexts[i]);
            } else {
                contexts.add(transformsAndContexts[i]);
            }
        }

        if (transforms.size() != contexts.size()) {
            throw new IllegalArgumentException("There must be the same number of transforms as contexts specified");
        }

        return withTransformQuad(transforms.toArray(QuadTransform[]::new), contexts.toArray());
    }

    /**
     * Gets a view of this quad sink as a per-vertex model-data emitter that transforms its quads with the given transforms.
     *
     * @param transform1            the first quad transform that applies to every quad.
     * @param context1              the context to be passed to the first transform.
     * @param transform2            the second quad transform that applies to every quad.
     * @param context2              the context to be passed to the second transform.
     * @param transform3            the third quad transform that applies to every quad.
     * @param context3              the context to be passed to the third transform.
     * @param transformsAndContexts the remaining transforms and their associated contexts, alternating between transform and context.
     * @param <C1>                  the type of context passed to the first transform.
     * @param <C2>                  the type of context passed to the second transform.
     * @param <C3>                  the type of context passed to the third transform.
     * @return the per-vertex model-data emitter that transforms quads.
     */
    default <C1, C2, C3> PooledVertexEmitter withTransformVertex(QuadTransform<C1> transform1,
                                                                 @UnknownNullability C1 context1,
                                                                 QuadTransform<C2> transform2,
                                                                 @UnknownNullability C2 context2,
                                                                 QuadTransform<C3> transform3,
                                                                 @UnknownNullability C3 context3,
                                                                 Object... transformsAndContexts) {
        int len = transformsAndContexts.length;

        ArrayList<QuadTransform<?>> transforms = new ArrayList<>(len / 2 + 3);
        ArrayList<Object> contexts = new ArrayList<>(len / 2 + 3);
        transforms.add(transform1);
        contexts.add(context1);
        transforms.add(transform2);
        contexts.add(context2);
        transforms.add(transform3);
        contexts.add(context3);

        for (int i = 0; i < len; i++) {
            if (i % 2 == 0) {
                transforms.add((QuadTransform<?>) transformsAndContexts[i]);
            } else {
                contexts.add(transformsAndContexts[i]);
            }
        }

        if (transforms.size() != contexts.size()) {
            throw new IllegalArgumentException("There must be the same number of transforms as contexts specified");
        }

        return withTransformVertex(transforms.toArray(QuadTransform[]::new), contexts.toArray());
    }

    /**
     * Close this quad sink.
     * <p>
     * This only does anything on pooled emitters.
     */
    default void close() {}

    /**
     * {@return whether this quad sink transforms its quads}
     */
    default boolean isTransformer() {
        return false;
    }

    @Override
    default VertexConsumer getBuffer(RenderType renderType) {
        return asVertexEmitter().setDefaultMaterial(getRendererOrDefault().materialManager().fromVanilla(renderType));
    }

    /**
     * If this quad sink was previously used as a vertex consumer or multi-buffer source, then this flushes the last
     * vertex, emitting the final quad.
     */
    void flushVertices();
}
