package com.kneelawk.krender.engine.base.material;

import net.minecraft.util.Mth;

import com.kneelawk.krender.engine.api.util.TriState;
import com.kneelawk.krender.engine.api.material.BlendMode;
import com.kneelawk.krender.engine.api.material.MaterialView;

/**
 * Exposes base-specific material view api that materials should implement in order to be compatible with the other base
 * implementations.
 */
public interface BaseMaterialViewApi extends MaterialView {
    /**
     * Array of blend modes for fast lookup.
     */
    BlendMode[] BLEND_MODES = BlendMode.values();
    /**
     * Number of blend modes in array.
     */
    int BLEND_MODE_COUNT = BLEND_MODES.length;
    /**
     * Array of tri state values for fast lookup.
     */
    TriState[] TRI_STATES = TriState.values();
    /**
     * Number of try state values in array.
     */
    int TRI_STATE_COUNT = TRI_STATES.length;

    /**
     * Blend mode bit length.
     */
    int BLEND_MODE_BIT_LENGTH = Mth.ceillog2(BLEND_MODE_COUNT);
    /**
     * Emissive flag bit length.
     */
    int EMISSIVE_BIT_LENGTH = 1;
    /**
     * Diffuse shading disable flag bit length.
     */
    int DIFFUSE_BIT_LENGTH = 1;
    /**
     * Ambient occlusion bit length.
     */
    int AO_BIT_LENGTH = Mth.ceillog2(TRI_STATE_COUNT);

    /**
     * Blend mode bit offset.
     */
    int BLEND_MODE_BIT_OFFSET = 0;
    /**
     * Blend mode bit mask.
     */
    int BLEND_MODE_MASK = bitMask(BLEND_MODE_BIT_LENGTH, BLEND_MODE_BIT_OFFSET);
    /**
     * Emissive flag bit length.
     */
    int EMISSIVE_BIT_OFFSET = BLEND_MODE_BIT_OFFSET + BLEND_MODE_BIT_LENGTH;
    /**
     * Emissive flag bit mask.
     */
    int EMISSIVE_FLAG = bitMask(EMISSIVE_BIT_LENGTH, EMISSIVE_BIT_OFFSET);
    /**
     * Diffuse shading disable flag bit length.
     */
    int DIFFUSE_BIT_OFFSET = EMISSIVE_BIT_OFFSET + EMISSIVE_BIT_LENGTH;
    /**
     * Diffuse shading disable flag bit mask.
     */
    int DIFFUSE_FLAG = bitMask(DIFFUSE_BIT_LENGTH, DIFFUSE_BIT_OFFSET);
    /**
     * Ambient occlusion bit length.
     */
    int AO_BIT_OFFSET = DIFFUSE_BIT_OFFSET + DIFFUSE_BIT_LENGTH;
    /**
     * Ambient occlusion bit mask.
     */
    int AO_MASK = bitMask(AO_BIT_LENGTH, AO_BIT_OFFSET);
    /**
     * Total used bits in a material.
     */
    int TOTAL_BIT_LENGTH = AO_BIT_OFFSET + AO_BIT_LENGTH;
    /**
     * Material bit mask.
     */
    int FULL_BIT_MASK = (1 << TOTAL_BIT_LENGTH) - 1;

    /**
     * Creates a bit mask given the mask length and mask offset.
     *
     * @param len the mask length.
     * @param off the mask offset.
     * @return the bit mask.
     */
    static int bitMask(int len, int off) {
        return ((1 << len) - 1) << off;
    }

    /**
     * Determines whether the given material bits are valid.
     *
     * @param bits the bits to verify.
     * @return {@code true} if the given bits are a valid material.
     */
    static boolean isValid(int bits) {
        int blendMode = (bits & BLEND_MODE_MASK) >>> BLEND_MODE_BIT_OFFSET;
        int ao = (bits & AO_MASK) >>> AO_BIT_OFFSET;

        return blendMode < BLEND_MODE_COUNT && ao < TRI_STATE_COUNT;
    }

    /**
     * {@return the bit value representing this material}
     */
    int getBits();
}
