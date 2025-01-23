package com.kneelawk.krender.engine.base.material;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.IntFunction;

import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import net.minecraft.resources.ResourceLocation;

import com.kneelawk.krender.engine.api.KRenderer;
import com.kneelawk.krender.engine.api.util.TriState;
import com.kneelawk.krender.engine.api.material.MaterialFinder;
import com.kneelawk.krender.engine.api.material.MaterialManager;
import com.kneelawk.krender.engine.api.material.RenderMaterial;

/**
 * Base {@link MaterialManager} implementation capable of handling all default materials without any extensions enabled.
 *
 * @param <M> the type of {@link RenderMaterial} implementation this uses.
 */
public class BaseMaterialManager<M extends BaseMaterialViewApi & RenderMaterial>
    implements MaterialManager, BaseMaterialManagerApi<M> {
    /**
     * The maximum number of materials possible.
     */
    public static final int MATERIAL_COUNT = 1 << BaseMaterialViewApi.TOTAL_BIT_LENGTH;

    /**
     * The renderer that this material manager is associated with.
     */
    protected final KRenderer renderer;

    /**
     * A map of materials by resource-location id.
     */
    protected final Object2ObjectOpenHashMap<ResourceLocation, M> materialsById = new Object2ObjectOpenHashMap<>();

    /**
     * A lock for the material-by-id map.
     */
    protected final ReentrantReadWriteLock materialsByIdLock = new ReentrantReadWriteLock();

    /**
     * Lookup array of materials indexed by their bits.
     */
    protected final Object[] materials = new Object[MATERIAL_COUNT];

    /**
     * The default bits that a material finder starts with.
     */
    protected final int defaultBits;

    /**
     * The default material.
     */
    protected final RenderMaterial defaultMaterial;

    /**
     * The missing material.
     */
    protected final RenderMaterial missingMaterial;

    /**
     * The default material finder implementation.
     */
    protected class Finder extends BaseMaterialFinder {
        /**
         * Creates a new {@link BaseMaterialFinder} with the given default bits.
         *
         * @param defaultBits the default bits for the new material finder.
         */
        public Finder(int defaultBits) {
            super(defaultBits);
        }

        @Override
        public RenderMaterial find() {
            return (RenderMaterial) materials[bits];
        }

        @Override
        public @Nullable KRenderer getRenderer() {
            return renderer;
        }
    }

    /**
     * Creates a new {@link BaseMaterialManager}.
     *
     * @param renderer        the renderer that this material manager is associated with.
     * @param materialFactory the factory for materials.
     */
    public BaseMaterialManager(KRenderer renderer, IntFunction<M> materialFactory) {
        this(renderer, computeDefaultBits(), materialFactory);
    }

    /**
     * Creates a new {@link BaseMaterialManager}.
     *
     * @param renderer        the renderer that this material manager is associated with.
     * @param defaultBits     the default bits for a material in this material manager.
     * @param materialFactory the factory for materials.
     */
    protected BaseMaterialManager(KRenderer renderer, int defaultBits, IntFunction<M> materialFactory) {
        this.renderer = renderer;
        this.defaultBits = defaultBits;

        for (int i = 0; i < MATERIAL_COUNT; i++) {
            if (BaseMaterialViewApi.isValid(i)) {
                materials[i] = materialFactory.apply(i);
            }
        }

        defaultMaterial = materialFinder().find();
        // make the missing material unequal from all other materials
        missingMaterial = materialFactory.apply(defaultBits);
    }

    private static int computeDefaultBits() {
        int defaultBits = 0;

        defaultBits = defaultBits | (TriState.DEFAULT.ordinal() << BaseMaterialViewApi.AO_BIT_OFFSET);

        return defaultBits;
    }

    @Override
    @SuppressWarnings("unchecked")
    public M getMaterialByBits(int bits) {
        return (M) materials[bits];
    }

    @Override
    public MaterialFinder materialFinder() {
        return new Finder(defaultBits);
    }

    @Override
    public RenderMaterial defaultMaterial() {
        return defaultMaterial;
    }

    @Override
    public RenderMaterial missingMaterial() {
        return missingMaterial;
    }

    @Override
    public @Nullable RenderMaterial materialById(ResourceLocation id) {
        materialsByIdLock.readLock().lock();
        try {
            return materialsById.get(id);
        } finally {
            materialsByIdLock.readLock().unlock();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean registerMaterial(ResourceLocation id, RenderMaterial material) {
        materialsByIdLock.writeLock().lock();
        try {
            return registerMaterialImpl(id, (M) material);
        } finally {
            materialsByIdLock.writeLock().unlock();
        }
    }

    /**
     * Registers the material in a thread-safe environment.
     *
     * @param id       the id of the material.
     * @param material the material to register.
     * @return whether the material was registered.
     */
    protected boolean registerMaterialImpl(ResourceLocation id, M material) {
        if (materialsById.containsKey(id)) return false;

        materialsById.put(id, material);

        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean registerOrUpdateMaterial(ResourceLocation id, RenderMaterial material) {
        materialsByIdLock.writeLock().lock();
        try {
            return registerOrUpdateMaterialImpl(id, (M) material);
        } finally {
            materialsByIdLock.writeLock().unlock();
        }
    }

    /**
     * Registers or replaces a material in a thread-safe environment.
     *
     * @param id       the id of the material.
     * @param material the material to register.
     * @return whether this was the first material to be registered with the given id.
     */
    protected boolean registerOrUpdateMaterialImpl(ResourceLocation id, M material) {
        return materialsById.put(id, material) == null;
    }

    @Override
    public @Nullable KRenderer getRenderer() {
        return renderer;
    }
}
