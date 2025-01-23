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

    /**
     * Creates a new boolean bit mask with no shift.
     *
     * @return a new {@link BooleanBits} for the given flag.
     */
    public static BooleanBits of() {
        return new BooleanBits(0, 64);
    }

    /**
     * Creates a new boolean bit mask with the given shift.
     *
     * @param shift      the number of bits to shift the boolean flag by.
     * @param unitLength the number of bits in a unit (32 for int units, 64 for long units).
     * @return a new {@link BooleanBits} for the given flag.
     */
    public static BooleanBits of(int shift, int unitLength) {
        return new BooleanBits(shift, unitLength);
    }

    /**
     * Creates a new boolean bit mask with the given shift for int units.
     *
     * @param shift the number of bits to shift the boolean flag by.
     * @return a new {@link BooleanBits} for the given flag.
     */
    public static BooleanBits ofI(int shift) {
        return of(shift, 32);
    }

    /**
     * Creates a new boolean bit mask with the given shift for long units.
     *
     * @param shift the number of bits to shift the boolean flag by.
     * @return a new {@link BooleanBits} for the given flag.
     */
    public static BooleanBits ofJ(int shift) {
        return of(shift, 64);
    }

    /**
     * Creates a new boolean bit mask shifted after the given bit mask.
     *
     * @param shift      the bit mask for this flag to be after.
     * @param unitLength the number of bits in a unit (32 for int units, 64 for long units).
     * @return a new {@link BooleanBits} for the given flag.
     */
    public static BooleanBits of(Bits shift, int unitLength) {
        return new BooleanBits(shift.fullShift() + shift.bitCount(), unitLength);
    }

    /**
     * Creates a new boolean bit mask shifted after the given bit mask for int units.
     *
     * @param shift the bit mask for this flag to be after.
     * @return a new {@link BooleanBits} for the given flag.
     */
    public static BooleanBits ofI(Bits shift) {
        return of(shift, 32);
    }

    /**
     * Creates a new boolean bit mask shifted after the given bit mask for long units.
     *
     * @param shift the bit mask for this flag to be after.
     * @return a new {@link BooleanBits} for the given flag.
     */
    public static BooleanBits ofJ(Bits shift) {
        return of(shift, 64);
    }

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
