package com.kneelawk.krender.engine.api;

import org.jetbrains.annotations.Nullable;

import com.kneelawk.krender.engine.api.backend.KRenderBackend;
import com.kneelawk.krender.engine.api.convert.TypeConverter;
import com.kneelawk.krender.engine.api.material.MaterialManager;
import com.kneelawk.krender.engine.api.mesh.MeshBuilder;
import com.kneelawk.krender.engine.api.model.BakedModelFactory;
import com.kneelawk.krender.engine.impl.backend.BackendManager;

/**
 * Describes an implementation of the KRender Engine API.
 */
public interface KRenderer {

    /**
     * {@return the default renderer}
     */
    static KRenderer getDefault() {
        return BackendManager.getDefault();
    }

    /**
     * {@return the default renderer or null if none are present}
     */
    static @Nullable KRenderer tryGetDefault() {
        return BackendManager.tryGetDefault();
    }

    /**
     * Gets the renderer from the backend with the given name.
     *
     * @param name the name of the backend to get the renderer from.
     * @return the renderer from the requested backend.
     * @throws RuntimeException if a backend with the given name could not be found.
     */
    static KRenderer get(String name) {
        return BackendManager.get(name);
    }

    /**
     * Gets the renderer from the backend with the given name, if present.
     *
     * @param name the name of the backend to get the renderer from.
     * @return the renderer from the requested backend, or {@code null} if no backend exists with the given name.
     */
    static @Nullable KRenderer tryGet(String name) {
        return BackendManager.tryGet(name);
    }

    /**
     * {@return a baked model factory associated with this backend}
     */
    BakedModelFactory bakedModelFactory();

    /**
     * {@return a mesh builder for meshes associated with this backend}
     */
    MeshBuilder meshBuilder();

    /**
     * {@return this backend's material manager}
     */
    MaterialManager materialManager();

    /**
     * {@return this backend's type converter}
     */
    TypeConverter converter();
}
