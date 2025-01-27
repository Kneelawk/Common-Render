package com.kneelawk.krender.engine.base.material;

import java.util.concurrent.ExecutionException;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import net.minecraft.util.Mth;

import com.kneelawk.krender.engine.api.KRenderer;
import com.kneelawk.krender.engine.api.material.BlendMode;
import com.kneelawk.krender.engine.api.material.GlintMode;
import com.kneelawk.krender.engine.api.texture.MaterialTextureManager;
import com.kneelawk.krender.engine.api.util.TriState;
import com.kneelawk.krender.engine.api.util.bits.BooleanBits;
import com.kneelawk.krender.engine.api.util.bits.EnumBits;
import com.kneelawk.krender.engine.api.util.bits.IntBits;

/**
 * Bit format for materials.
 */
public class BaseMaterialFormat {
    private static final Cache<MaterialTextureManager, BaseMaterialFormat> cache =
        CacheBuilder.newBuilder().weakKeys().build();

    /**
     * Gets a base material format for the given texture manager.
     *
     * @param manager the manager to get the material format for.
     * @return the material format for the given texture manager.
     */
    public static BaseMaterialFormat get(MaterialTextureManager manager) {
        try {
            return cache.get(manager, () -> new BaseMaterialFormat(Mth.ceillog2(manager.maxIntId())));
        } catch (ExecutionException e) {
            // should never happen
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a base material format for the given renderer.
     *
     * @param renderer the renderer implementation supplying the texture manager.
     * @return the material format for the given renderer.
     */
    public static BaseMaterialFormat get(KRenderer renderer) {
        return get(renderer.textureManager());
    }

    /**
     * Blend mode bits.
     */
    public final EnumBits<BlendMode> blendMode;
    /**
     * Emissive bits.
     */
    public final BooleanBits emissive;
    /**
     * Diffuse disabled bits.
     */
    public final BooleanBits diffuseDisabled;
    /**
     * Ambient occlusion bits.
     */
    public final EnumBits<TriState> ambientOcclusion;
    /**
     * Glint mode bits.
     */
    public final EnumBits<GlintMode> glintMode;
    /**
     * Texture bits.
     */
    public final IntBits texture;

    /**
     * Create a new base material format for the given number of texture bits.
     *
     * @param textureBits the number of bits allocated to textures.
     */
    public BaseMaterialFormat(int textureBits) {
        blendMode = EnumBits.of(BlendMode.class);
        emissive = BooleanBits.ofI(blendMode);
        diffuseDisabled = BooleanBits.ofI(emissive);
        ambientOcclusion = EnumBits.ofNoSplitI(diffuseDisabled, TriState.class);
        glintMode = EnumBits.ofNoSplitI(ambientOcclusion, GlintMode.class);
        texture = IntBits.ofNoSplitI(glintMode, textureBits);

        Preconditions.checkState(getBitCount() <= 32, "Base material format must not exceed 32 bits");
    }

    /**
     * {@return the number of bits used in this material format}
     */
    public int getBitCount() {
        return texture.fullShift() + texture.bitCount();
    }
}
