package com.kneelawk.krender.engine.neoforge.impl.mixin.impl;

import net.neoforged.neoforge.client.ChunkRenderTypeSet;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.resources.model.SimpleBakedModel;

@Mixin(SimpleBakedModel.class)
public interface Accessor_SimpleBakedModel {
    @Accessor("blockRenderTypes")
    ChunkRenderTypeSet krender$getBlockRenderTypes();
}
