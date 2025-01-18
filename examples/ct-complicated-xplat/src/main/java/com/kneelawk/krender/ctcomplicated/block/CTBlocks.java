package com.kneelawk.krender.ctcomplicated.block;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

import com.kneelawk.kregistry.core.api.KRegistrar;
import com.kneelawk.krender.ctcomplicated.CTComplicated;

public class CTBlocks {
    private static final KRegistrar<Block> BLOCKS = CTComplicated.REGISTRARS.get(Registries.BLOCK);
    private static final KRegistrar<Item> ITEMS = CTComplicated.REGISTRARS.get(Registries.ITEM);

    public static final Supplier<GlassBlock> CT_GLASS = BLOCKS.register("ct_glass", () -> new GlassBlock(
        BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.HAT).strength(0.3f).sound(SoundType.GLASS)
            .noOcclusion().isValidSpawn(CTBlocks::never).isRedstoneConductor(CTBlocks::never)
            .isSuffocating(CTBlocks::never).isViewBlocking(CTBlocks::never)));
    public static final Supplier<BlockItem> CT_GLASS_ITEM =
        ITEMS.register("ct_glass", () -> new BlockItem(CT_GLASS.get(), new Item.Properties()));
    public static final Supplier<Block> DISCO_FLOOR = BLOCKS.register("disco_floor", () -> new Block(
        BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.DRAGON).strength(0.3f).sound(SoundType.GLASS)
            .isValidSpawn(CTBlocks::never).isSuffocating(CTBlocks::never)));
    public static final Supplier<BlockItem> DISCO_FLOOR_ITEM =
        ITEMS.register("disco_floor", () -> new BlockItem(DISCO_FLOOR.get(), new Item.Properties()));

    public static void register() {
    }

    private static boolean never(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return false;
    }

    private static boolean never(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos,
                                 EntityType<?> entityType) {
        return false;
    }
}
