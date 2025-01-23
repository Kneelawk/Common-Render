package com.kneelawk.krender.engine.api.util.bits;

/**
 * Blank bit mask for use as buffer space.
 */
public class BufferBits implements Bits {
    private final int fullShift;
    private final int bitCount;
    private final int unitIndex;
    private final boolean split;

    /**
     * Create a buffer space bit mask with a fixed shift and fixed bit count.
     *
     * @param shift      the shift of this buffer space.
     * @param bitCount   the number of bits used as buffer space.
     * @param unitLength the number of bits in a unit (32 for int units, 64 for long units).
     * @return a new {@link BufferBits} for the given buffer space.
     */
    public static BufferBits ofFixed(int shift, int bitCount, int unitLength) {
        return new BufferBits(shift, bitCount, unitLength);
    }

    /**
     * Create a buffer space bit mask with a fixed shift and fixed bit count for int units.
     *
     * @param shift    the shift of this buffer space.
     * @param bitCount the number of bits used as buffer space.
     * @return a new {@link BufferBits} for the given buffer space.
     */
    public static BufferBits ofFixedI(int shift, int bitCount) {
        return ofFixed(shift, bitCount, 32);
    }

    /**
     * Create a buffer space bit mask with a fixed shift and fixed bit count for long units.
     *
     * @param shift    the shift of this buffer space.
     * @param bitCount the number of bits used as buffer space.
     * @return a new {@link BufferBits} for the given buffer space.
     */
    public static BufferBits ofFixedJ(int shift, int bitCount) {
        return ofFixed(shift, bitCount, 64);
    }

    /**
     * Create a buffer space bit mask to ensure the given number of bits are filled.
     *
     * @param shift           the shift of this buffer space.
     * @param targetTotalBits the number of bits that should be occupied after this buffer space has been added.
     * @param unitLength      the number of bits in a unit (32 for int units, 64 for long units).
     * @return a new {@link BufferBits} for the given buffer space.
     */
    public static BufferBits targetTotalBits(int shift, int targetTotalBits, int unitLength) {
        if (shift > targetTotalBits) {
            throw new IllegalStateException(
                "Shift is already farther than target total bits. Shift: " + shift + ", target total bits: " +
                    targetTotalBits);
        }
        return new BufferBits(shift, targetTotalBits - shift, unitLength);
    }

    /**
     * Create a buffer space bit mask to ensure the given number of bits are filled for int units.
     *
     * @param shift           the shift of this buffer space.
     * @param targetTotalBits the number of bits that should be occupied after this buffer space has been added.
     * @return a new {@link BufferBits} for the given buffer space.
     */
    public static BufferBits targetTotalBitsI(int shift, int targetTotalBits) {
        return targetTotalBits(shift, targetTotalBits, 32);
    }

    /**
     * Create a buffer space bit mask to ensure the given number of bits are filled for long units.
     *
     * @param shift           the shift of this buffer space.
     * @param targetTotalBits the number of bits that should be occupied after this buffer space has been added.
     * @return a new {@link BufferBits} for the given buffer space.
     */
    public static BufferBits targetTotalBitsJ(int shift, int targetTotalBits) {
        return targetTotalBits(shift, targetTotalBits, 64);
    }

    /**
     * Create a buffer space bit mask to ensure the given number of bits are filled after the given bit mask.
     *
     * @param shift           the bit mask after which the buffer space is to be placed.
     * @param targetTotalBits the number of bits that should be occupied after this buffer space has been added.
     * @param unitLength      the number of bits in a unit (32 for int units, 64 for long units).
     * @return a new {@link BufferBits} for the given buffer space.
     */
    public static BufferBits targetTotalBits(Bits shift, int targetTotalBits, int unitLength) {
        return targetTotalBits(shift.fullShift() + shift.bitCount(), targetTotalBits, unitLength);
    }

    /**
     * Create a buffer space bit mask to ensure the given number of bits are filled after the given bit mask for int units.
     *
     * @param shift           the bit mask after which the buffer space is to be placed.
     * @param targetTotalBits the number of bits that should be occupied after this buffer space has been added.
     * @return a new {@link BufferBits} for the given buffer space.
     */
    public static BufferBits targetTotalBitsI(Bits shift, int targetTotalBits) {
        return targetTotalBits(shift.fullShift() + shift.bitCount(), targetTotalBits, 32);
    }

    /**
     * Create a buffer space bit mask to ensure the given number of bits are filled after the given bit mask for long units.
     *
     * @param shift           the bit mask after which the buffer space is to be placed.
     * @param targetTotalBits the number of bits that should be occupied after this buffer space has been added.
     * @return a new {@link BufferBits} for the given buffer space.
     */
    public static BufferBits targetTotalBitsJ(Bits shift, int targetTotalBits) {
        return targetTotalBits(shift.fullShift() + shift.bitCount(), targetTotalBits, 64);
    }

    /**
     * Create a buffer space bit mask to fill the rest of the current unit.
     *
     * @param shift      the shift of this buffer space.
     * @param unitLength the number of bits in a unit (32 for int units, 64 for long units).
     * @return a new {@link BufferBits} for the given buffer space.
     */
    public static BufferBits fillUnit(int shift, int unitLength) {
        int smallShift = shift % unitLength;
        return new BufferBits(shift, unitLength - smallShift, unitLength);
    }

    /**
     * Create a buffer space bit mask to fill the rest of the current unit for int units.
     *
     * @param shift the shift of this buffer space.
     * @return a new {@link BufferBits} for the given buffer space.
     */
    public static BufferBits fillUnitI(int shift) {
        return fillUnit(shift, 32);
    }

    /**
     * Create a buffer space bit mask to fill the rest of the current unit for long units.
     *
     * @param shift the shift of this buffer space.
     * @return a new {@link BufferBits} for the given buffer space.
     */
    public static BufferBits fillUnitJ(int shift) {
        return fillUnit(shift, 64);
    }

    /**
     * Create a buffer space bit mask to fill the rest of the current unit after the given bit mask.
     *
     * @param shift      the bit mask after which the buffer space is to be placed.
     * @param unitLength the number of bits in a unit (32 for int units, 64 for long units).
     * @return a new {@link BufferBits} for the given buffer space.
     */
    public static BufferBits fillUnit(Bits shift, int unitLength) {
        return fillUnit(shift.fullShift() + shift.bitCount(), unitLength);
    }

    /**
     * Create a buffer space bit mask to fill the rest of the current unit after the given bit mask for int units.
     *
     * @param shift the bit mask after which the buffer space is to be placed.
     * @return a new {@link BufferBits} for the given buffer space.
     */
    public static BufferBits fillUnitI(Bits shift) {
        return fillUnit(shift.fullShift() + shift.bitCount(), 32);
    }

    /**
     * Create a buffer space bit mask to fill the rest of the current unit after the given bit mask for long units.
     *
     * @param shift the bit mask after which the buffer space is to be placed.
     * @return a new {@link BufferBits} for the given buffer space.
     */
    public static BufferBits fillUnitJ(Bits shift) {
        return fillUnit(shift.fullShift() + shift.bitCount(), 64);
    }

    private BufferBits(int fullShift, int bitCount, int unitLength) {
        this.fullShift = fullShift;
        this.bitCount = bitCount;
        this.unitIndex = fullShift / unitLength;
        int shift = fullShift % unitLength;
        this.split = shift + bitCount > unitLength;
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
}
