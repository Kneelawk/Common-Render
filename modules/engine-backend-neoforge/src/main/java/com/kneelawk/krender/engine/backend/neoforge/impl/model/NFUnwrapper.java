package com.kneelawk.krender.engine.backend.neoforge.impl.model;

import java.lang.reflect.Method;

import net.neoforged.neoforge.client.model.data.ModelData;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import com.kneelawk.krender.engine.api.model.BakedModelCore;
import com.kneelawk.krender.engine.api.model.BakedModelUnwrapper;

public class NFUnwrapper implements BakedModelUnwrapper {
    @Override
    public @Nullable BakedModelCore<?> unwrap(BakedModel model) {
        if (isImplemented(model, "getQuads", BlockState.class, Direction.class, RandomSource.class, ModelData.class,
            RenderType.class) ||
            isImplemented(model, "getModelData", BlockAndTintGetter.class, BlockPos.class, BlockState.class,
                ModelData.class) ||
            isImplemented(model, "getRenderTypes", BlockState.class, RandomSource.class, ModelData.class) ||
            isImplemented(model, "getRenderTypes", ItemStack.class, boolean.class) ||
            isImplemented(model, "getRenderPasses", ItemStack.class, boolean.class)) {
            return new NFUnwrappedModel(model);
        }

        return null;
    }

    private static boolean isImplemented(Object o, String name, Class<?>... args) {
        try {
            Class<?> oClass = o.getClass();
            Method method = oClass.getMethod(name, args);
            return method.getDeclaringClass().equals(oClass);
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}
