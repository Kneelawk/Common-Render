package com.kneelawk.krender.engine.api.model;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import com.kneelawk.krender.engine.api.data.DataHolder;
import com.kneelawk.krender.engine.api.data.DataProviderBlockEntity;

/**
 * Context for use when rendering blocks.
 * <p>
 * The data contained may change, but changing or removing a constructor is considered an API-breaking change.
 *
 * @param level  The level access.
 * @param pos    The position of the block being rendered.
 * @param state  The state of the block being rendered.
 * @param random The random for the given block state and position.
 * @param data   The holder with data from an associated {@link DataProviderBlockEntity} if present.
 * @see DataProviderBlockEntity#getData()
 */
public record ModelBlockContext(BlockAndTintGetter level, BlockPos pos, BlockState state,
                                Supplier<RandomSource> random, DataHolder data) {
}
