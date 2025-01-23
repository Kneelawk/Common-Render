package com.kneelawk.krender.engine.api.util.bits;

/**
 * Abstract bit mask type.
 */
public interface Bits {
    /**
     * {@return the full shift of the }
     */
    int fullShift();

    /**
     * {@return the number of bits used for data}
     */
    int bitCount();

    /**
     * {@return the index of the long to get from}
     */
    int unitIndex();

    /**
     * {@return whether the mask is split between two units}
     */
    boolean split();
}
