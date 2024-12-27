package com.kneelawk.krender.engine.api.model;

import org.jetbrains.annotations.UnknownNullability;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;

import com.kneelawk.krender.engine.api.buffer.QuadEmitter;

/**
 * Implementors of this interface function as cross-platform {@link BakedModel}s.
 * <p>
 * This interface abstracts away the loader-platform-specific extra methods that can be implemented on {@link BakedModel}s.
 *
 * @param <BK> the type that is used as a cache key when caching or used to pass data to the emitter when not.
 */
public interface BakedModelCore<BK> {
    /**
     * {@return whether this model has ambient occlusion}
     */
    boolean useAmbientOcclusion();

    /**
     * {@return whether this model should appear rotated in inventory GUIs}
     */
    boolean isGui3d();

    /**
     * {@return whether this model is lit from the side ({@code true}) or head-on ({@code false})}
     */
    boolean usesBlockLight();

    /**
     * {@return the particle sprite for this model}
     */
    TextureAtlasSprite getParticleIcon();

    /**
     * {@return the rotation of this model as an item}
     */
    ItemTransforms getTransforms();

    /**
     * Gets the model key used to pass information to {@link #renderBlock(QuadEmitter, Object)}.
     * <p>
     * This key may also be used for caching.
     * <p>
     * Note: This method will usually be executed on the chunk building threads.
     *
     * @param ctx the context used to access the level, block-pos, and everything else needed to create the model key.
     * @return the model key used to pass data to the actual renderer.
     */
    @UnknownNullability
    BK getBlockKey(ModelBlockContext ctx);

    /**
     * Actually renders the block.
     *
     * @param renderTo the quad emitter to render to.
     * @param blockKey the block data to render.
     */
    void renderBlock(QuadEmitter renderTo, @UnknownNullability BK blockKey);

    /**
     * Render the item.
     *
     * @param renderTo the quad emitter to render to.
     * @param ctx      the context used to access the item stack.
     */
    void renderItem(QuadEmitter renderTo, ModelItemContext ctx);
}
