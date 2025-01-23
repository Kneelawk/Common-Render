package com.kneelawk.krender.engine.api.util.bits;

import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

import net.minecraft.util.Mth;

public class EnumBits<E extends Enum<E>> implements Bits {
    private final IntBits bits;
    private final IntFunction<E> fromIndex;
    private final ToIntFunction<E> toIndex;

    /**
     * Create an enum bit mask with no shift.
     *
     * @param enumClass the class of the enum.
     * @param <E>       the type of enum managed.
     * @return a new {@link EnumBits} for the given bits.
     */
    public static <E extends Enum<E>> EnumBits<E> of(Class<E> enumClass) {
        E[] values = enumClass.getEnumConstants();
        int valuesLen = values.length;
        int bitCount = Mth.ceillog2(valuesLen);

        IntFunction<E> fromIndex = i -> {
            if (i < 0) return values[0];
            if (i >= valuesLen) return values[valuesLen - 1];
            return values[i];
        };
        ToIntFunction<E> toIndex = E::ordinal;

        return new EnumBits<>(IntBits.of(bitCount), fromIndex, toIndex);
    }

    /**
     * Create an enum bit mask with the given shift.
     *
     * @param shift      the number of bits to shift the data.
     * @param enumClass  the class of the enum.
     * @param unitLength the number of bits in a unit (32 for int units, 64 for long units).
     * @param <E>        the type of enum managed.
     * @return a new {@link EnumBits} for the given bits.
     */
    public static <E extends Enum<E>> EnumBits<E> of(int shift, Class<E> enumClass, int unitLength) {
        E[] values = enumClass.getEnumConstants();
        int valuesLen = values.length;
        int bitCount = Mth.ceillog2(valuesLen);

        IntFunction<E> fromIndex = i -> {
            if (i < 0) return values[0];
            if (i >= valuesLen) return values[valuesLen - 1];
            return values[i];
        };
        ToIntFunction<E> toIndex = E::ordinal;

        return new EnumBits<>(IntBits.of(shift, bitCount, unitLength), fromIndex, toIndex);
    }

    /**
     * Create an enum bit mask with the given shift for int units.
     *
     * @param shift     the number of bits to shift the data.
     * @param enumClass the class of the enum.
     * @param <E>       the type of enum managed.
     * @return a new {@link EnumBits} for the given bits.
     */
    public static <E extends Enum<E>> EnumBits<E> ofI(int shift, Class<E> enumClass) {
        return of(shift, enumClass, 32);
    }

    /**
     * Create an enum bit mask with the given shift for long units.
     *
     * @param shift     the number of bits to shift the data.
     * @param enumClass the class of the enum.
     * @param <E>       the type of enum managed.
     * @return a new {@link EnumBits} for the given bits.
     */
    public static <E extends Enum<E>> EnumBits<E> ofJ(int shift, Class<E> enumClass) {
        return of(shift, enumClass, 64);
    }

    /**
     * Create an enum bit mask with the given shift, but without splitting across multiple bit units.
     *
     * @param shift      the number of bits to shift the data.
     * @param enumClass  the class of the enum.
     * @param unitLength the number of bits in a unit (32 for int units, 64 for long units).
     * @param <E>        the type of enum managed.
     * @return a new {@link EnumBits} for the given bits.
     */
    public static <E extends Enum<E>> EnumBits<E> ofNoSplit(int shift, Class<E> enumClass, int unitLength) {
        E[] values = enumClass.getEnumConstants();
        int valuesLen = values.length;
        int bitCount = Mth.ceillog2(valuesLen);

        IntFunction<E> fromIndex = i -> {
            if (i < 0) return values[0];
            if (i >= valuesLen) return values[valuesLen - 1];
            return values[i];
        };
        ToIntFunction<E> toIndex = E::ordinal;

        return new EnumBits<>(IntBits.ofNoSplit(shift, bitCount, unitLength), fromIndex, toIndex);
    }

    /**
     * Create an enum bit mask with the given shift for int units, but without splitting across multiple bit units.
     *
     * @param shift     the number of bits to shift the data.
     * @param enumClass the class of the enum.
     * @param <E>       the type of enum managed.
     * @return a new {@link EnumBits} for the given bits.
     */
    public static <E extends Enum<E>> EnumBits<E> ofINoSplit(int shift, Class<E> enumClass) {
        return of(shift, enumClass, 32);
    }

    /**
     * Create an enum bit mask with the given shift for long units, but without splitting across multiple bit units.
     *
     * @param shift     the number of bits to shift the data.
     * @param enumClass the class of the enum.
     * @param <E>       the type of enum managed.
     * @return a new {@link EnumBits} for the given bits.
     */
    public static <E extends Enum<E>> EnumBits<E> ofJNoSplit(int shift, Class<E> enumClass) {
        return of(shift, enumClass, 64);
    }

    /**
     * Create an enum bit mask shifted after the given bit mask.
     *
     * @param shift      the number of bits to shift the data.
     * @param enumClass  the class of the enum.
     * @param unitLength the number of bits in a unit (32 for int units, 64 for long units).
     * @param <E>        the type of enum managed.
     * @return a new {@link EnumBits} for the given bits.
     */
    public static <E extends Enum<E>> EnumBits<E> of(Bits shift, Class<E> enumClass, int unitLength) {
        return of(shift.fullShift() + shift.bitCount(), enumClass, unitLength);
    }

    /**
     * Create an enum bit mask shifted after the given bit mask for int units.
     *
     * @param shift     the number of bits to shift the data.
     * @param enumClass the class of the enum.
     * @param <E>       the type of enum managed.
     * @return a new {@link EnumBits} for the given bits.
     */
    public static <E extends Enum<E>> EnumBits<E> ofI(Bits shift, Class<E> enumClass) {
        return of(shift.fullShift() + shift.bitCount(), enumClass, 32);
    }

    /**
     * Create an enum bit mask shifted after the given bit mask for long units.
     *
     * @param shift     the number of bits to shift the data.
     * @param enumClass the class of the enum.
     * @param <E>       the type of enum managed.
     * @return a new {@link EnumBits} for the given bits.
     */
    public static <E extends Enum<E>> EnumBits<E> ofJ(Bits shift, Class<E> enumClass) {
        return of(shift.fullShift() + shift.bitCount(), enumClass, 64);
    }

    /**
     * Create an enum bit mask shifted after the given bit mask, but without splitting across multiple bit units.
     *
     * @param shift      the number of bits to shift the data.
     * @param enumClass  the class of the enum.
     * @param unitLength the number of bits in a unit (32 for int units, 64 for long units).
     * @param <E>        the type of enum managed.
     * @return a new {@link EnumBits} for the given bits.
     */
    public static <E extends Enum<E>> EnumBits<E> ofNoSplit(Bits shift, Class<E> enumClass, int unitLength) {
        return ofNoSplit(shift.fullShift() + shift.bitCount(), enumClass, unitLength);
    }

    /**
     * Create an enum bit mask shifted after the given bit mask for int units, but without splitting across multiple bit units.
     *
     * @param shift     the number of bits to shift the data.
     * @param enumClass the class of the enum.
     * @param <E>       the type of enum managed.
     * @return a new {@link EnumBits} for the given bits.
     */
    public static <E extends Enum<E>> EnumBits<E> ofINoSplit(Bits shift, Class<E> enumClass) {
        return ofNoSplit(shift.fullShift() + shift.bitCount(), enumClass, 32);
    }

    /**
     * Create an enum bit mask shifted after the given bit mask for long units, but without splitting across multiple bit units.
     *
     * @param shift     the number of bits to shift the data.
     * @param enumClass the class of the enum.
     * @param <E>       the type of enum managed.
     * @return a new {@link EnumBits} for the given bits.
     */
    public static <E extends Enum<E>> EnumBits<E> ofJNoSplit(Bits shift, Class<E> enumClass) {
        return ofNoSplit(shift.fullShift() + shift.bitCount(), enumClass, 64);
    }

    private EnumBits(IntBits bits, IntFunction<E> fromIndex, ToIntFunction<E> toIndex) {
        this.bits = bits;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    @Override
    public int fullShift() {
        return bits.fullShift();
    }

    @Override
    public int bitCount() {
        return bits.bitCount();
    }

    @Override
    public int unitIndex() {
        return bits.unitIndex();
    }

    @Override
    public boolean split() {
        return bits.split();
    }

    /**
     * Gets an enum from 32 bits of data.
     *
     * @param bits the bits to get the enum from.
     * @return the read enum.
     */
    public E getI(int bits) {
        return fromIndex.apply(this.bits.getI(bits));
    }

    /**
     * Gets an enum from 64 bits of data.
     *
     * @param bits1 the lower half of the 64 bits to get the enum from.
     * @param bits2 the upper half of the 64 bits to get the enum from.
     * @return the read enum.
     */
    public E getI(int bits1, int bits2) {
        return fromIndex.apply(bits.getI(bits1, bits2));
    }

    /**
     * Gets an enum from 64 bits of data.
     *
     * @param bits the bits to get the enum from.
     * @return the read enum.
     */
    public E getJ(long bits) {
        return fromIndex.apply(this.bits.getJ(bits));
    }

    /**
     * Gets an enum from 128 bits of data.
     *
     * @param bits1 the lower half of the 128 bits to get the enum from.
     * @param bits2 the upper half of the 128 bits to get the enum from.
     * @return the read enum.
     */
    public E getJ(long bits1, long bits2) {
        return fromIndex.apply(bits.getJ(bits1, bits2));
    }

    /**
     * Sets an enum within 32 bits of data.
     *
     * @param bits  the bits to set the enum within.
     * @param value the enum value to set.
     * @return the new bits with the value applied.
     */
    public int setI(int bits, E value) {
        return this.bits.setI(bits, toIndex.applyAsInt(value));
    }

    /**
     * Sets an enum within the high end of 64 bits of data.
     *
     * @param bits2 the high end of the bits to set the enum within.
     * @param value the enum value to set.
     * @return the new high bits with the value applied.
     */
    public int setIHigh(int bits2, E value) {
        return bits.setIHigh(bits2, toIndex.applyAsInt(value));
    }

    /**
     * Sets an enum within 64 bits of data.
     *
     * @param bits  the bits to set the enum within.
     * @param value the enum value to set.
     * @return the new bits with the value applied.
     */
    public long setJ(long bits, E value) {
        return this.bits.setJ(bits, toIndex.applyAsInt(value));
    }

    /**
     * Sets the enum within the high end of 128 bits of data.
     *
     * @param bits2 the high end of the bits to set the enum within.
     * @param value the enum value to set.
     * @return the new high bits with the value applied.
     */
    public long setJHigh(long bits2, E value) {
        return bits.setJHigh(bits2, toIndex.applyAsInt(value));
    }
}
