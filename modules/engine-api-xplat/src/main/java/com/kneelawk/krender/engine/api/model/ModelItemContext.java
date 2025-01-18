package com.kneelawk.krender.engine.api.model;

import java.util.function.Supplier;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

/**
 * Context for use when rendering items.
 *
 * @param stack          access to the stack being rendered. May be {@link ItemStack#EMPTY} on some platforms.
 * @param randomSupplier a supplier for random sources for the item.
 */
public record ModelItemContext(ItemStack stack, Supplier<RandomSource> randomSupplier) {
}
