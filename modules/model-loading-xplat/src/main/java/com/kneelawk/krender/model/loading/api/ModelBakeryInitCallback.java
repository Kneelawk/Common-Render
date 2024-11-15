package com.kneelawk.krender.model.loading.api;

import com.kneelawk.commonevents.api.Event;
import com.kneelawk.krender.model.loading.impl.KRLog;

/**
 * Callback fired when the model bakery plugin system is being initialized.
 * <p>
 * This can serve as a client entrypoint for registering model bakery plugins on platforms where the client is not
 * initialized before resource reload begins.
 * <p>
 * Note: This may be fired from threads besides the main client thread.
 */
@FunctionalInterface
public interface ModelBakeryInitCallback {
    /**
     * Event fired when the model bakery plugin system is being initialized.
     */
    Event<ModelBakeryInitCallback> EVENT =
        Event.createSimple(ModelBakeryInitCallback.class, KRLog.error("Error in model bakery init callback"));

    /**
     * Called when the model bakery plugin system is being initialized.
     *
     * @param ctx the context that provides convenience methods for registering plugins.
     */
    void init(Context ctx);

    /**
     * Model bakery initialization context.
     */
    interface Context {
        /**
         * Use to register a simple {@link ModelBakeryPlugin}.
         * <p>
         * Shortcut for {@link ModelBakeryPlugin#register(ModelBakeryPlugin)}.
         *
         * @param plugin the plugin to register.
         * @see ModelBakeryPlugin#register(ModelBakeryPlugin)
         */
        void register(ModelBakeryPlugin plugin);

        /**
         * Used to register a {@link PreparableModelBakeryPlugin} that loads resources before registering objects.
         * <p>
         * Shortcut for {@link ModelBakeryPlugin#registerPreparable(PreparableModelBakeryPlugin.ResourceLoader, PreparableModelBakeryPlugin)}.
         *
         * @param loader the resource loader.
         * @param plugin the plugin that registers objects.
         * @param <T>    the type of resource that the plugin loads.
         * @see ModelBakeryPlugin#registerPreparable(PreparableModelBakeryPlugin.ResourceLoader, PreparableModelBakeryPlugin)
         */
        <T> void registerPreparable(PreparableModelBakeryPlugin.ResourceLoader<T> loader,
                                    PreparableModelBakeryPlugin<T> plugin);
    }
}
