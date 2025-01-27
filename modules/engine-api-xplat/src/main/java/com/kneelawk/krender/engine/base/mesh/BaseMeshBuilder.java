package com.kneelawk.krender.engine.base.mesh;

import org.jetbrains.annotations.Nullable;

import com.kneelawk.krender.engine.api.KRenderer;
import com.kneelawk.krender.engine.api.buffer.QuadEmitter;
import com.kneelawk.krender.engine.api.mesh.Mesh;
import com.kneelawk.krender.engine.api.mesh.MeshBuilder;
import com.kneelawk.krender.engine.base.buffer.BaseQuadEmitter;
import com.kneelawk.krender.engine.base.buffer.BaseQuadFormat;
import com.kneelawk.krender.engine.base.buffer.RootQuadEmitter;

/**
 * Base {@link MeshBuilder} implementation.
 */
public class BaseMeshBuilder implements MeshBuilder {
    /**
     * The quad emitter implementation linked to this mesh builder.
     */
    protected final BaseQuadEmitter maker;
    /**
     * The renderer that this mesh builder, its emitter, and all built meshes will be associated with.
     */
    protected final BaseKRendererApi renderer;
    /**
     * This mesh builder's scratch data buffer.
     */
    protected int[] data = new int[256];
    /**
     * The current index within the scratch buffer.
     */
    protected int index = 0;
    /**
     * The current length of the scratch buffer.
     */
    protected int limit = data.length;

    /**
     * Creates a new base mesh builder.
     *
     * @param renderer the render that this mesh builder, its emitter, and all built meshes will be associated with.
     */
    public BaseMeshBuilder(BaseKRendererApi renderer) {
        this.maker = createMaker(renderer);
        this.renderer = renderer;
        maker.begin(data, index);
    }

    /**
     * Create a new associated quad emitter linked to this mesh builder.
     * <p>
     * Sadly, a method called during construction is the only to create inner classes in the constructor.
     *
     * @param renderer the renderer for the quad emitter.
     * @return the new associated quad emitter.
     */
    protected BaseQuadEmitter createMaker(BaseKRendererApi renderer) {
        return new Maker(renderer);
    }

    /**
     * Makes sure this buffer and its associated buffer have enough capacity for another quad.
     *
     * @param stride the size of the next quad.
     */
    protected void ensureCapacity(int stride) {
        if (stride > limit - index) {
            limit *= 2;
            final int[] bigger = new int[limit];
            System.arraycopy(data, 0, bigger, 0, index);
            data = bigger;
            maker.begin(data, index);
        }
    }

    /**
     * Collects an emitted quad, adding it to this builder's mesh data, and ensuring there is enough space for the next quad.
     *
     * @param stride the size of the next quad.
     * @return the new base index.
     */
    protected int increment(int stride) {
        index += stride;
        ensureCapacity(stride);
        return index;
    }

    @Override
    public QuadEmitter emitter() {
        maker.begin(data, index);
        return maker;
    }

    @Override
    public Mesh build() {
        // just make sure everything's flushed
        maker.flushVertices();

        final int[] packed = new int[index];
        System.arraycopy(data, 0, packed, 0, index);
        index = 0;
        maker.begin(data, index);
        return new BaseMesh(renderer, packed);
    }

    @Override
    public @Nullable KRenderer getRenderer() {
        return renderer;
    }

    private class Maker extends RootQuadEmitter {
        public Maker(BaseKRendererApi renderer) {
            super(renderer);
        }

        @Override
        public void emitDirectly() {
            baseIndex = increment(BaseQuadFormat.TOTAL_STRIDE);
        }
    }
}
