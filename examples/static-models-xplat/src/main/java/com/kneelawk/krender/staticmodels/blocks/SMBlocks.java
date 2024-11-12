package com.kneelawk.krender.staticmodels.blocks;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import com.kneelawk.commonevents.api.Listen;
import com.kneelawk.commonevents.api.Scan;
import com.kneelawk.kregistry.core.api.KHolder;
import com.kneelawk.kregistry.core.api.KRegistrar;
import com.kneelawk.kregistry.core.api.RegisterCallback;
import com.kneelawk.krender.staticmodels.StaticModelsMod;

@Scan
public class SMBlocks {
    private static final KRegistrar<Block> BLOCKS = StaticModelsMod.REGISTRARS.get(Registries.BLOCK);
    private static final KRegistrar<Item> ITEMS = StaticModelsMod.REGISTRARS.get(Registries.ITEM);

    public static final KHolder<Block> LAMP = BLOCKS.register("lamp", () -> new Block(BlockBehaviour.Properties.of()));
    public static final KHolder<BlockItem> LAMP_ITEM =
        ITEMS.register("lamp", () -> new BlockItem(LAMP.get(), new Item.Properties()));

    @Listen(RegisterCallback.class)
    public static void register(RegisterCallback.Context ctx) {}
}
