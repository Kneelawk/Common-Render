package com.kneelawk.krender.engine.base.mesh;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import com.kneelawk.krender.engine.api.KRenderer;
import com.kneelawk.krender.engine.api.buffer.QuadView;
import com.kneelawk.krender.engine.api.mesh.Mesh;

/**
 * Empty mesh implementation.
 */
public final class EmptyMesh implements Mesh {
    private final BaseKRendererApi renderer;

    /**
     * Creates a new empty mesh associated with the given renderer.
     *
     * @param renderer the renderer that this empty mesh is associated with.
     */
    public EmptyMesh(BaseKRendererApi renderer) {this.renderer = renderer;}

    @Override
    public void forEach(Consumer<QuadView> consumer) {}

    @Override
    public @Nullable KRenderer getRenderer() {
        return renderer;
    }
}
