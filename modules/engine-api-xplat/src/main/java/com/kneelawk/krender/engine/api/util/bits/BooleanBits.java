package com.kneelawk.krender.engine.api.util.bits;

/**
 * Bit mask for getting and setting boolean values.
 */
public class BooleanBits implements Bits {
    private final int fullShift;
    private final int unitIndex;
    private final long mask;
    private final int maskI;
    private final long inverseMask;
    private final int inverseMaskI;

    private BooleanBits(int fullShift, int unitLength) {
        this.fullShift = fullShift;
        this.unitIndex = fullShift / unitLength;
        int shift = fullShift % unitLength;

        this.mask = (1L << shift);
        this.maskI = (int) this.mask;
        this.inverseMask = ~this.mask;
        this.inverseMaskI = ~this.maskI;
    }

    @Override
    public int fullShift() {
        return fullShift;
    }

    @Override
    public int bitCount() {
        return 1;
    }

    @Override
    public int unitIndex() {
        return unitIndex;
    }

    @Override
    public boolean split() {
        return false;
    }

    /**
     * Gets a boolean from 32 bits of data.
     *
     * @param bits the bits to get the boolean from.
     * @return the read boolean.
     */
    public boolean getI(int bits) {
        return (bits & maskI) != 0;
    }

    /**
     * Gets a boolean from 64 bits of data.
     *
     * @param bits the bits to get the boolean from.
     * @return the read boolean.
     */
    public boolean getJ(long bits) {
        return (bits & mask) != 0L;
    }

    /**
     * Sets a boolean within 32 bits of data.
     *
     * @param bits  the bits to set the boolean within.
     * @param value the boolean value to set.
     * @return the new bits with the value applied.
     */
    public int setI(int bits, boolean value) {
        return value ? (bits | maskI) : (bits & inverseMaskI);
    }

    /**
     * Sets a boolean within 64 bits of data.
     *
     * @param bits  the bits to set the boolean within.
     * @param value the boolean value to set.
     * @return the new bits with the value applied.
     */
    public long setJ(long bits, boolean value) {
        return value ? (bits | mask) : (bits & inverseMask);
    }
}
