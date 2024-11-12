package com.kneelawk.krender.staticmodels.misc;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import com.kneelawk.commonevents.api.Listen;
import com.kneelawk.commonevents.api.Scan;
import com.kneelawk.kregistry.core.api.KHolder;
import com.kneelawk.kregistry.core.api.KRegistrar;
import com.kneelawk.kregistry.core.api.RegisterCallback;
import com.kneelawk.krender.staticmodels.StaticModelsMod;
import com.kneelawk.krender.staticmodels.blocks.SMBlocks;

import static com.kneelawk.krender.staticmodels.SMConstants.tt;

@Scan
public class SMCreativeTabs {
    private static final KRegistrar<CreativeModeTab> CREATIVE_TABS =
        StaticModelsMod.REGISTRARS.get(Registries.CREATIVE_MODE_TAB);

    public static final KHolder<CreativeModeTab> MAIN = CREATIVE_TABS.register("main",
        () -> new CreativeModeTab.Builder(null, -1).title(tt("itemGroup", "main"))
            .icon(() -> new ItemStack(SMBlocks.LAMP_ITEM.get())).displayItems((itemDisplayParameters, output) -> {
                output.accept(SMBlocks.LAMP_ITEM.get());
            }).build());

    @Listen(RegisterCallback.class)
    public static void register(RegisterCallback.Context ctx) {}
}
