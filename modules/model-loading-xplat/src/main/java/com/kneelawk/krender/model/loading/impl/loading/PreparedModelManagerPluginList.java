package com.kneelawk.krender.model.loading.impl.loading;

import java.util.List;

import com.kneelawk.krender.model.loading.api.ModelManagerPlugin;
import com.kneelawk.krender.model.loading.impl.KRLog;

public record PreparedModelManagerPluginList(List<ModelManagerPlugin> plugins,
                                             List<? extends PreparedModelManagerPlugin<?>> preparedPlugins) {
    public ModelManagerPluginManager loadPlugins() {
        ModelManagerPluginContextImpl ctx = new ModelManagerPluginContextImpl();

        for (ModelManagerPlugin plugin : plugins) {
            try {
                plugin.init(ctx);
            } catch (Exception e) {
                KRLog.LOGGER.error("Error initializing model bakery plugin", e);
            }
        }

        for (PreparedModelManagerPlugin<?> plugin : preparedPlugins) {
            try {
                plugin.init(ctx);
            } catch (Exception e) {
                KRLog.LOGGER.error("Error initializing model bakery plugin", e);
            }
        }

        return ctx.createManager();
    }
}
