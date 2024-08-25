package com.kneelawk.krender.engine.api.base;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.IntFunction;

import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import net.minecraft.resources.ResourceLocation;

import com.kneelawk.krender.engine.api.TriState;
import com.kneelawk.krender.engine.api.material.MaterialFinder;
import com.kneelawk.krender.engine.api.material.MaterialManager;
import com.kneelawk.krender.engine.api.material.RenderMaterial;

/**
 * Base {@link MaterialManager} implementation capable of handling all default materials without any extensions enabled.
 *
 * @param <M> the type of {@link RenderMaterial} implementation this uses.
 */
public class BaseMaterialManager<M extends BaseMaterialView & RenderMaterial> implements MaterialManager {
    /**
     * The maximum number of materials possible.
     */
    public static final int MATERIAL_COUNT = 1 << BaseMaterialView.TOTAL_BIT_LENGTH;

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
    }

    /**
     * Creates a new {@link BaseMaterialManager}.
     *
     * @param materialFactory the factory for materials.
     */
    public BaseMaterialManager(IntFunction<M> materialFactory) {
        this(computeDefaultBits(), materialFactory);
    }

    /**
     * Creates a new {@link BaseMaterialManager}.
     *
     * @param defaultBits     the default bits for a material in this material manager.
     * @param materialFactory the factory for materials.
     */
    protected BaseMaterialManager(int defaultBits, IntFunction<M> materialFactory) {
        this.defaultBits = defaultBits;

        for (int i = 0; i < MATERIAL_COUNT; i++) {
            if (BaseMaterialView.isValid(i)) {
                materials[i] = materialFactory.apply(i);
            }
        }

        defaultMaterial = materialFinder().find();
        // make the missing material unequal from all other materials
        missingMaterial = materialFactory.apply(defaultBits);
    }

    private static int computeDefaultBits() {
        int defaultBits = 0;

        defaultBits = defaultBits | (TriState.DEFAULT.ordinal() << BaseMaterialView.AO_BIT_OFFSET);

        return defaultBits;
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
            if (materialsById.containsKey(id)) return false;

            materialsById.put(id, (M) material);

            return true;
        } finally {
            materialsByIdLock.writeLock().unlock();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean registerOrUpdateMaterial(ResourceLocation id, RenderMaterial material) {
        materialsByIdLock.writeLock().lock();
        try {
            return materialsById.put(id, (M) material) == null;
        } finally {
            materialsByIdLock.writeLock().unlock();
        }
    }
}
