package com.kneelawk.krender.engine.base.convert;

import com.kneelawk.krender.engine.api.buffer.QuadEmitter;
import com.kneelawk.krender.engine.base.buffer.BaseQuadEmitter;
import com.kneelawk.krender.engine.base.buffer.BaseQuadFormat;
import com.kneelawk.krender.engine.base.buffer.RootQuadEmitter;

/**
 * Quad emitter that wraps a {@link BaseQuadEmitter} from another backend.
 */
public class ConvertingQuadEmitter extends RootQuadEmitter {
    /**
     * The quad emitter to emit to.
     */
    protected final QuadEmitter target;

    /**
     * Creates a new quad emitter for emitting to a binary-compatible backend.
     *
     * @param renderer the renderer that this quad emitter will be associated with.
     * @param target   the emitter to wrap.
     */
    public ConvertingQuadEmitter(BaseKRendererApi renderer, QuadEmitter target) {
        super(renderer);
        this.target = target;
        begin(new int[BaseQuadFormat.TOTAL_STRIDE], 0);
    }

    @Override
    public void emitDirectly() {
        // base copyTo impl can handle copying to other impls
        copyTo(target);
        target.emit();
    }
}
