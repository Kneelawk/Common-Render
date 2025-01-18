package com.kneelawk.krender.ctcomplicated.misc;

import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import com.kneelawk.kregistry.core.api.KRegistrar;
import com.kneelawk.krender.ctcomplicated.CTComplicated;
import com.kneelawk.krender.ctcomplicated.block.CTBlocks;

import static com.kneelawk.krender.ctcomplicated.CTConstants.tt;

public class CTCreativeTabs {
    private static final KRegistrar<CreativeModeTab> CREATIVE_TABS =
        CTComplicated.REGISTRARS.get(Registries.CREATIVE_MODE_TAB);

    public static final Supplier<CreativeModeTab> CT_TAB = CREATIVE_TABS.register("ct_main",
        () -> CreativeModeTab.builder(null, -1).title(tt("itemGroup", "ct_main"))
            .displayItems((params, output) -> {
                output.accept(CTBlocks.CT_GLASS_ITEM.get());
                output.accept(CTBlocks.DISCO_FLOOR_ITEM.get());
            }).icon(() -> new ItemStack(CTBlocks.CT_GLASS_ITEM.get())).build());

    public static void register() {
    }
}
