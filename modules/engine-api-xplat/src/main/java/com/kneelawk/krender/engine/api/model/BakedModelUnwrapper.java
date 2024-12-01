package com.kneelawk.krender.engine.api.model;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.resources.model.BakedModel;

/**
 * Implemented by backends to provide backend-dependent access to baked model internals.
 * <p>
 * <b>Note:</b> this class is implemented by backends. Please use {@link ModelUtils#getCore(BakedModel)} instead.
 */
@ApiStatus.OverrideOnly
public interface BakedModelUnwrapper {
    /**
     * Implemented by backends to provide backend-dependent access to baked model internals.
     * <p>
     * <b>Note:</b> this class is implemented by backends. Please use {@link ModelUtils#getCore(BakedModel)} instead.
     *
     * @param model the baked model to be unwrapped.
     * @return a baked model core that can be used to render the baked model.
     */
    @Nullable BakedModelCore<?> unwrap(BakedModel model);
}
