package com.kneelawk.krender.engine.api.material;

/**
 * Static material view.
 */
public interface RenderMaterial extends MaterialView {
    /**
     * {@return an integer id of this material}
     *
     * This is only guaranteed to be the same for a single runtime. Materials' integer ids are likely to change across
     * restarts.
     */
    int integerId();
}
