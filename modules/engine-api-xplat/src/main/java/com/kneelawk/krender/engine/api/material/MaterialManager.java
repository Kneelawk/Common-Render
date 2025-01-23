package com.kneelawk.krender.engine.api.material;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import com.kneelawk.krender.engine.api.RendererDependent;

/**
 * Manages materials, material lookups, material caching, and material finding.
 */
public interface MaterialManager extends RendererDependent {

    /**
     * This gets or creates a material finder for this backend.
     * <p>
     * This method may cache returned material-finders in thread-locals.
     *
     * @return the requested material finder.
     */
    MaterialFinder materialFinder();

    /**
     * {@return this backend's default material}
     */
    RenderMaterial defaultMaterial();

    /**
     * {@return this backend's material that indicates a missing material}
     */
    RenderMaterial missingMaterial();

    /**
     * Gets a {@link RenderMaterial} by its integer id if a material with that integer id exists.
     *
     * @param id the id of the render material to look up.
     * @return the render material created with the given integer id.
     * @throws IllegalStateException if the specified id does not exist.
     * @see RenderMaterial#integerId()
     */
    RenderMaterial materialByIntegerId(int id);

    /**
     * {@return the maximum valid integer id for a render material}
     */
    int maxIntegerId();

    /**
     * Gets a {@link RenderMaterial} by its id if has been registered.
     *
     * @param id the id of the render material to look up.
     * @return the render material registered with the given id or {@code null} if the requested id has not been
     * registered with this material manager.
     */
    @Nullable RenderMaterial materialById(ResourceLocation id);

    /**
     * Registers a {@link RenderMaterial} with a {@link ResourceLocation} id if it does not already exist.
     * <p>
     * This will only register a material if the id it is being registered with has not already been registered.
     *
     * @param id       the id of the render material to register.
     * @param material the render material to register.
     * @return {@code false} if a material with the given id was already present.
     */
    boolean registerMaterial(ResourceLocation id, RenderMaterial material);

    /**
     * Registers a {@link RenderMaterial} with a {@link ResourceLocation} id.
     * <p>
     * This will replace existing materials with new ones. Though on some platforms, replacement of old materials is
     * not possible. In those situations, this method behaves exactly like
     * {@link #registerMaterial(ResourceLocation, RenderMaterial)}.
     *
     * @param id       the id of the render material to register.
     * @param material the render material to register.
     * @return {@code false} if a material with the given id was already present.
     */
    default boolean registerOrUpdateMaterial(ResourceLocation id, RenderMaterial material) {
        return registerMaterial(id, material);
    }

    /**
     * Shortcut for finding a render material from a vanilla render type.
     * <p>
     * This makes a best-effort attempt to find a render material equivalent to the given render type.
     *
     * @param type the render type to find the render material equivalent to.
     * @return the roughly equivalent render material.
     */
    default RenderMaterial fromVanilla(RenderType type) {
        return materialFinder().fromVanilla(type).find();
    }
}
