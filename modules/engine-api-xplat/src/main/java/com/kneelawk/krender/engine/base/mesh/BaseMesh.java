package com.kneelawk.krender.engine.base.mesh;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import com.kneelawk.krender.engine.api.KRenderer;
import com.kneelawk.krender.engine.api.buffer.QuadEmitter;
import com.kneelawk.krender.engine.api.buffer.QuadView;
import com.kneelawk.krender.engine.api.mesh.Mesh;
import com.kneelawk.krender.engine.base.buffer.BaseQuadEmitter;
import com.kneelawk.krender.engine.base.buffer.BaseQuadFormat;
import com.kneelawk.krender.engine.base.buffer.BaseQuadView;

/**
 * Mesh base implementation.s
 */
public class BaseMesh implements Mesh {
    /**
     * A cursor per thread, allowing this mesh to be iterated over concurrently on different threads.
     */
    protected final ThreadLocal<BaseQuadView> cursorPool = ThreadLocal.withInitial(this::createCursor);

    /**
     * The renderer that this mesh is associated with.
     */
    protected final BaseKRendererApi renderer;
    /**
     * The data held by this mesh.
     */
    protected final int[] data;

    /**
     * Creates a new base mesh.
     *
     * @param renderer the renderer that the new mesh will be associated with.
     * @param data     the data of the mesh.
     */
    public BaseMesh(BaseKRendererApi renderer, int[] data) {
        this.renderer = renderer;
        this.data = data;
    }

    /**
     * {@return a new base quad view for use as a cursor for this thread}
     */
    protected BaseQuadView createCursor() {
        return new BaseQuadView(renderer);
    }

    @Override
    public void forEach(Consumer<QuadView> consumer) {
        forEach(consumer, cursorPool.get());
    }

    /**
     * Use the given quad view as a cursor.
     *
     * @param consumer the consumer of each quad in the cursor.
     * @param cursor   the cursor.
     */
    protected void forEach(Consumer<QuadView> consumer, BaseQuadView cursor) {
        final int[] data = this.data;
        final int limit = data.length;
        int index = 0;

        while (index < limit) {
            cursor.load(data, index);
            consumer.accept(cursor);
            index += BaseQuadFormat.TOTAL_STRIDE;
        }
    }

    @Override
    public void outputTo(QuadEmitter emitter) {
        if (emitter instanceof BaseQuadEmitter e) {
            final int[] data = this.data;
            final int limit = data.length;
            int index = 0;

            while (index < limit) {
                e.copyFrom(data, index);
                e.emitDirectly();
                index += BaseQuadFormat.TOTAL_STRIDE;
            }
        } else {
            Mesh.super.outputTo(emitter);
        }
    }

    @Override
    public @Nullable KRenderer getRenderer() {
        return renderer;
    }
}
