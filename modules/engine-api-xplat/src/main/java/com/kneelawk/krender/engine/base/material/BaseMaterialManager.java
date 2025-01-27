package com.kneelawk.krender.engine.base.material;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;

import com.kneelawk.krender.engine.api.KRenderer;
import com.kneelawk.krender.engine.api.material.BlendMode;
import com.kneelawk.krender.engine.api.material.GlintMode;
import com.kneelawk.krender.engine.api.material.MaterialFinder;
import com.kneelawk.krender.engine.api.material.MaterialManager;
import com.kneelawk.krender.engine.api.material.MaterialView;
import com.kneelawk.krender.engine.api.material.RenderMaterial;
import com.kneelawk.krender.engine.api.util.TriState;

/**
 * Base {@link MaterialManager} implementation capable of handling all default materials without any extensions enabled.
 */
public class BaseMaterialManager implements MaterialManager {
    /**
     * The maximum material id value.
     */
    public static final int MATERIAL_COUNT = 0x8000;

    /**
     * The renderer that this material manager is associated with.
     */
    protected final KRenderer renderer;

    /**
     * The material format used by materials of this manager.
     */
    protected final BaseMaterialFormat format;

    /**
     * Lookup array of materials indexed by their integer ids.
     */
    protected final RenderMaterial[] materials = new RenderMaterial[MATERIAL_COUNT];

    /**
     * Cache of finder to render material instances.
     */
    protected final ConcurrentHashMap<BaseMaterialFinder, RenderMaterial> finderLookup = new ConcurrentHashMap<>();

    /**
     * Atomic render material id counter.
     */
    protected final AtomicInteger nextId = new AtomicInteger(0);

    /**
     * A map of materials by resource-location id.
     */
    protected final ConcurrentHashMap<ResourceLocation, RenderMaterial> materialsById = new ConcurrentHashMap<>();

    /**
     * The default bits that a material finder starts with.
     */
    protected final int defaultBits;

    /**
     * The backend's material factory.
     */
    protected final MaterialFactory materialFactory;

    /**
     * The default material.
     */
    protected final RenderMaterial defaultMaterial;

    /**
     * The missing material.
     */
    protected final RenderMaterial missingMaterial;

    /**
     * Function for creating a render material based on the given material finder, for when the requested material has not been created yet.
     */
    protected final Function<BaseMaterialFinder, RenderMaterial> createMaterial = this::createMaterial;

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
            return finderLookup.computeIfAbsent(this, createMaterial);
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
    public BaseMaterialManager(KRenderer renderer, MaterialFactory materialFactory) {
        this(renderer, computeDefaultBits(renderer), materialFactory);
    }

    /**
     * Creates a new {@link BaseMaterialManager}.
     *
     * @param renderer        the renderer that this material manager is associated with.
     * @param defaultBits     the default bits for a material in this material manager.
     * @param materialFactory the factory for materials.
     */
    protected BaseMaterialManager(KRenderer renderer, int defaultBits,
                                  MaterialFactory materialFactory) {
        this.renderer = renderer;
        this.format = BaseMaterialFormat.get(renderer);
        this.defaultBits = defaultBits;
        this.materialFactory = materialFactory;

        missingMaterial = materialFinder().setName(MaterialView.MISSING_ID.toString()).find();
        defaultMaterial = materialFinder().setName(MaterialView.DEFAULT_ID.toString()).find();
    }

    private static int computeDefaultBits(KRenderer renderer) {
        int defaultBits = 0;

        BaseMaterialFormat format = BaseMaterialFormat.get(renderer);

        defaultBits = format.blendMode.setI(defaultBits, BlendMode.DEFAULT);
        defaultBits = format.emissive.setI(defaultBits, false);
        defaultBits = format.diffuseDisabled.setI(defaultBits, false);
        defaultBits = format.ambientOcclusion.setI(defaultBits, TriState.DEFAULT);
        defaultBits = format.glintMode.setI(defaultBits, GlintMode.DEFAULT);
        defaultBits = format.texture.setI(defaultBits, renderer.textureManager().blockAtlas().intId());

        return defaultBits;
    }

    private RenderMaterial createMaterial(BaseMaterialFinder finder) {
        int id = nextId.getAndIncrement();
        RenderMaterial material = materialFactory.create(finder, id);
        materials[id] = material;
        return material;
    }

    @Override
    public RenderMaterial materialByIntId(int id) {
        if (id < -1 || id >= MATERIAL_COUNT) throw new IllegalArgumentException("Invalid material int id");
        RenderMaterial material = materials[id];
        if (material == null)
            throw new IllegalStateException("Attempted to request a material " + id +
                " that does not exist. This likely indicates mesh corruption.");
        return material;
    }

    @Override
    public int maxIntId() {
        return MATERIAL_COUNT;
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
        return materialsById.get(id);
    }

    @Override
    public boolean registerMaterial(ResourceLocation id, RenderMaterial material) {
        if (materialsById.containsKey(id)) return false;

        materialsById.put(id, material);

        return true;
    }

    @Override
    public boolean registerOrUpdateMaterial(ResourceLocation id, RenderMaterial material) {
        return materialsById.put(id, material) == null;
    }

    @Override
    public @Nullable KRenderer getRenderer() {
        return renderer;
    }

    /**
     * A backend-implemented factory for render materials.
     */
    public interface MaterialFactory {
        /**
         * Create a render material specific to the backend being implemented.
         *
         * @param finder the material finder that was used to create this material.
         * @param intId  integer id of the material to be created.
         * @return the new render material.
         */
        RenderMaterial create(BaseMaterialView finder, int intId);
    }
}
