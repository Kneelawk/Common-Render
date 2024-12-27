package com.kneelawk.krender.model.loading.impl.loading;

import com.kneelawk.krender.model.loading.api.ModelManagerPlugin;
import com.kneelawk.krender.model.loading.api.PreparableModelManagerPlugin;
import com.kneelawk.krender.model.loading.impl.KRLog;

public record PreparedModelManagerPlugin<T>(T resource, PreparableModelManagerPlugin<T> plugin) {
    void init(ModelManagerPlugin.Context ctx) {
        try {
            plugin.init(resource, ctx);
        } catch (Exception e) {
            KRLog.LOGGER.error("Error loading model bakery plugin", e);
        }
    }
}
