package com.kneelawk.krender.engine.api.util.bits;

/**
 * Bit mask for getting and setting integers.
 */
public class IntBits implements Bits {
    /**
     * Create an integer bit mask with no shift.
     *
     * @param bitCount   the number of bits in the data.
     * @return a new {@link IntBits} for the given bits.
     */
    public static IntBits of(int bitCount) {
        // no need for custom unit-length versions here, because a shift of 0 means no splitting
        return new IntBits(0, bitCount, (1L << bitCount) - 1L, 0, 64);
    }

    /**
     * Create an integer bit mask with the given shift.
     *
     * @param shift      the number of bits to shift the data.
     * @param bitCount   the number of bits in the data.
     * @param unitLength the number of bits in a unit (32 for int units, 64 for long units).
     * @return a new {@link IntBits} for the given bits.
     */
    public static IntBits of(int shift, int bitCount, int unitLength) {
        int smallShift = shift % unitLength;
        long mask2;
        if (smallShift + bitCount > unitLength) {
            mask2 = 1L << (smallShift + bitCount - unitLength);
        } else {
            mask2 = 0;
        }

        return new IntBits(shift, bitCount, ((1L << bitCount) - 1L) << smallShift, mask2, unitLength);
    }

    /**
     * Create an integer bit mask with the given shift for int units.
     *
     * @param shift    the number of bits to shift the data.
     * @param bitCount the number of bits in the data.
     * @return a new {@link IntBits} for the given bits.
     */
    public static IntBits ofI(int shift, int bitCount) {
        return of(shift, bitCount, 32);
    }

    /**
     * Create an integer bit mask with the given shift for long units.
     *
     * @param shift    the number of bits to shift the data.
     * @param bitCount the number of bits in the data.
     * @return a new {@link IntBits} for the given bits.
     */
    public static IntBits ofJ(int shift, int bitCount) {
        return of(shift, bitCount, 64);
    }

    /**
     * Create an integer bit mask with the given shift, but without splitting across multiple bit units.
     *
     * @param shift      the number of bits to shift the data.
     * @param bitCount   the number of bits in the data.
     * @param unitLength the number of bits in a unit (32 for int units, 64 for long units).
     * @return a new {@link IntBits} for the given bits.
     */
    public static IntBits ofNoSplit(int shift, int bitCount, int unitLength) {
        int smallShift = shift % unitLength;
        if (smallShift + bitCount > unitLength) {
            throw new IllegalStateException(
                "Attempted to split an IntBits constructed with splitting disabled. Unit length: " + unitLength +
                    ", shift: " + smallShift + ", bit count: " + bitCount + ", ending bit: " + (smallShift + bitCount));
        }

        return new IntBits(shift, bitCount, ((1L << bitCount) - 1L) << smallShift, 0, unitLength);
    }

    /**
     * Create an integer bit mask with the given shift for int units, but without splitting across multiple bit units.
     *
     * @param shift    the number of bits to shift the data.
     * @param bitCount the number of bits in the data.
     * @return a new {@link IntBits} for the given bits.
     */
    public static IntBits ofINoSplit(int shift, int bitCount) {
        return ofNoSplit(shift, bitCount, 32);
    }

    /**
     * Create an integer bit mask with the given shift for long units, but without splitting across multiple bit units.
     *
     * @param shift    the number of bits to shift the data.
     * @param bitCount the number of bits in the data.
     * @return a new {@link IntBits} for the given bits.
     */
    public static IntBits ofJNoSplit(int shift, int bitCount) {
        return ofNoSplit(shift, bitCount, 64);
    }

    /**
     * Create an integer bit mask shifted after the given bit mask.
     *
     * @param shift      the bit mask for these bits to be after.
     * @param bitCount   the number of bits in the data.
     * @param unitLength the number of bits in a unit (32 for int units, 64 for long units).
     * @return a new {@link IntBits} for the given bits.
     */
    private static IntBits of(Bits shift, int bitCount, int unitLength) {
        return of(shift.fullShift() + shift.bitCount(), bitCount, unitLength);
    }

    /**
     * Create an integer bit mask shifted after the given bit mask for int units.
     *
     * @param shift    the bit mask for these bits to be after.
     * @param bitCount the number of bits in the data.
     * @return a new {@link IntBits} for the given bits.
     */
    private static IntBits ofI(Bits shift, int bitCount) {
        return of(shift, bitCount, 32);
    }

    /**
     * Create an integer bit mask shifted after the given bit mask for long units.
     *
     * @param shift    the bit mask for these bits to be after.
     * @param bitCount the number of bits in the data.
     * @return a new {@link IntBits} for the given bits.
     */
    private static IntBits ofJ(Bits shift, int bitCount) {
        return of(shift, bitCount, 64);
    }

    /**
     * Create an integer bit mask shifted after the given bit mask, but without splitting across multiple bit units.
     *
     * @param shift      the bit mask for these bits to be after.
     * @param bitCount   the number of bits in the data.
     * @param unitLength the number of bits in a unit (32 for int units, 64 for long units).
     * @return a new {@link IntBits} for the given bits.
     */
    private static IntBits ofNoSplit(Bits shift, int bitCount, int unitLength) {
        return ofNoSplit(shift.fullShift() + shift.bitCount(), bitCount, unitLength);
    }

    /**
     * Create an integer bit mask shifted after the given bit mask for int units, but without splitting across multiple bit units.
     *
     * @param shift    the bit mask for these bits to be after.
     * @param bitCount the number of bits in the data.
     * @return a new {@link IntBits} for the given bits.
     */
    public static IntBits ofINoSplit(Bits shift, int bitCount) {
        return ofNoSplit(shift, bitCount, 32);
    }

    /**
     * Create an integer bit mask shifted after the given bit mask for long units, but without splitting across multiple bit units.
     *
     * @param shift    the bit mask for these bits to be after.
     * @param bitCount the number of bits in the data.
     * @return a new {@link IntBits} for the given bits.
     */
    public static IntBits ofJNoSplit(Bits shift, int bitCount) {
        return ofNoSplit(shift, bitCount, 64);
    }

    private final int fullShift;
    private final int unitIndex;
    private final int shift;
    private final int shift2;
    private final int bitCount;
    private final boolean split;
    private final long mask;
    private final int maskI;
    private final long mask2;
    private final int mask2I;
    private final long inverseMask;
    private final int inverseMaskI;
    private final long inverseMask2;
    private final int inverseMask2I;

    /**
     * Creates a new {@link IntBits} with the given arguments.
     *
     * @param fullShift  the full number of bits to shift the data, even beyond unit length.
     * @param bitCount   the number of bits in the data.
     * @param mask       the bit mask for the data.
     * @param mask2      the split bit mask for the upper half of the data.
     * @param unitLength the number of bits in a unit (32 for int units, 64 for long units).
     */
    private IntBits(int fullShift, int bitCount, long mask, long mask2, int unitLength) {
        this.fullShift = fullShift;
        this.unitIndex = fullShift / unitLength;
        this.shift = fullShift % unitLength;
        this.shift2 = unitLength - this.shift;
        this.bitCount = bitCount;
        this.split = this.shift + bitCount > unitLength;
        this.mask = mask;
        this.maskI = (int) mask;
        this.mask2 = mask2;
        this.mask2I = (int) mask2;
        this.inverseMask = ~mask;
        this.inverseMaskI = ~this.maskI;
        this.inverseMask2 = ~mask2;
        this.inverseMask2I = ~this.mask2I;
    }

    @Override
    public int fullShift() {
        return fullShift;
    }

    @Override
    public int bitCount() {
        return bitCount;
    }

    @Override
    public int unitIndex() {
        return unitIndex;
    }

    @Override
    public boolean split() {
        return split;
    }

    /**
     * Gets an integer from 32 bits of data.
     *
     * @param bits the bits to get the int from.
     * @return the read int.
     */
    public int getI(int bits) {
        return (bits & maskI) >>> shift;
    }

    /**
     * Gets an integer from 64 bits of data.
     *
     * @param bits1 the lower half of the 64 bits to get the int from.
     * @param bits2 the upper half of the 64 bits to get the int from.
     * @return the read int.
     */
    public int getI(int bits1, int bits2) {
        if (!split) return getI(bits2);
        return ((bits1 & maskI) >>> shift) | ((bits2 & mask2I) << shift2);
    }

    /**
     * Gets an integer from 64 bits of data.
     *
     * @param bits the bits to get the int from.
     * @return the read int.
     */
    public int getJ(long bits) {
        return (int) ((bits & mask) >>> shift);
    }

    /**
     * Gets an integer from 128 bits of data.
     *
     * @param bits1 the lower half of the 128 bits to get the int from.
     * @param bits2 the upper half of the 128 bits to get the int from.
     * @return the read int.
     */
    public int getJ(long bits1, long bits2) {
        if (!split) return getJ(bits1);
        return (int) (((bits1 & mask) >>> shift) | ((bits2 & mask2) << shift2));
    }

    /**
     * Sets an integer within 32 bits of data.
     *
     * @param bits  the bits to set the integer within.
     * @param value the integer value to set.
     * @return the new bits with the value applied.
     */
    public int setI(int bits, int value) {
        return (bits & inverseMaskI) | (value << shift);
    }

    /**
     * Sets an integer within the high end of 64 bits of data.
     *
     * @param bits2 the high end of the bits to set the integer within.
     * @param value the integer value to set.
     * @return the new high bits with the value applied.
     */
    public int setIHigh(int bits2, int value) {
        return (bits2 & inverseMask2I) | (value >>> shift2);
    }

    /**
     * Sets an integer within 64 bits of data.
     *
     * @param bits  the bits to set the integer within.
     * @param value the integer value to set.
     * @return the new bits with the value applied.
     */
    public long setJ(long bits, int value) {
        return (bits & inverseMask) | ((long) value << shift);
    }

    /**
     * Sets the integer within the high end of 128 bits of data.
     *
     * @param bits2 the high end of the bits to set the integer within.
     * @param value the integer value to set.
     * @return the new high bits with the value applied.
     */
    public long setJHigh(long bits2, int value) {
        return (bits2 & inverseMask2) | ((long) value >>> shift2);
    }
}
