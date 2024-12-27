package com.kneelawk.krender.model.loading.api;

import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.client.renderer.block.model.UnbakedBlockStateModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import com.kneelawk.krender.model.loading.impl.loading.ModelManagerPluginRegistrar;

/**
 * Allows hooking into the mechanism of the {@link ModelBakery} when it is loading.
 */
@FunctionalInterface
public interface ModelManagerPlugin {
    /**
     * Use to register a simple {@link ModelManagerPlugin}.
     *
     * @param plugin the plugin to register.
     */
    static void register(ModelManagerPlugin plugin) {
        ModelManagerPluginRegistrar.register(plugin);
    }

    /**
     * Initialize the model bakery plugin by registering objects with the given context.
     * <p>
     * This may be called multiple times.
     * <p>
     * This should not cause the additional loading of models from a resource-manager. All resources should already be
     * loaded during a preparation stage using {@link PreparableModelManagerPlugin#register(PreparableModelManagerPlugin.ResourceLoader, PreparableModelManagerPlugin)}.
     * See {@link net.minecraft.resources.FileToIdConverter} for efficient loading of all models that meet a certain
     * criteria.
     *
     * @param ctx the initialization context used to register various functions.
     */
    void init(Context ctx);

    /**
     * The context supplied to a loaded {@link ModelManagerPlugin}.
     */
    @ApiStatus.NonExtendable
    interface Context {
        /**
         * Registers a default block state model that delegates to the given model when baked.
         * <p>
         * The resulting baked model can be obtained with {@code Minecraft.getInstance().getModelManager().getModel(name)}.
         *
         * @param state the name of the model.
         * @param name  where the model is actually found.
         */
        void linkBlockStateToModel(BlockState state, ResourceLocation name);

        /**
         * Registers a set of default block state models that delegate to their associated models when baked.
         * <p>
         * The result baked models can be obtained with {@code Minecraft.getInstance().getModelManager().getModel(name)}.
         *
         * @param models the model names and paths to load and add.
         */
        void linkBlockStatesToModels(Map<BlockState, ResourceLocation> models);

        /**
         * Adds an already loaded model to the {@link ModelBakery}'s set of block state models to bake.
         * <p>
         * A block state model is one that can be obtained with {@code Minecraft.getInstance().getModelManager().getModel(name)}.
         *
         * @param state the name of the model.
         * @param model the model to bake.
         */
        void addBlockStateModel(BlockState state, UnbakedBlockStateModel model);

        /**
         * Adds a collection of already loaded models to the {@link ModelBakery}'s set of block state models to bake.
         * <p>
         * A block state model is one that can be obtained with {@code Minecraft.getInstance().getModelManager().getModel(name)}.
         *
         * @param models the models to bake.
         */
        void addBlockStateModels(Map<BlockState, ? extends UnbakedBlockStateModel> models);

        /**
         * Adds an already loaded model to the {@link ModelBakery}'s set of lower-level models.
         * <p>
         * These models cannot be accessed directly, but can be referenced by a block state model and used that way.
         *
         * @param name  the name to associate the model with.
         * @param model the model to add.
         */
        void addReferenceableModel(ResourceLocation name, UnbakedModel model);

        /**
         * Adds already loaded models to the {@link ModelBakery}'s set of lower-level models.
         * <p>
         * These models cannot be accessed directly, but can be referenced by a block state model and used that way.
         *
         * @param models the models to make available to block state models.
         */
        void addReferenceableModels(Map<ResourceLocation, ? extends UnbakedModel> models);

        /**
         * Registers an extra model to be loaded even if it is not referenced by a block state model.
         * <p>
         * Models added here can be referenced via {@link ModelManagerUtils#getExtraModel(ModelManager, ResourceLocation)}.
         * <p>
         * This can be used to load models added via {@link #addReferenceableModel(ResourceLocation, UnbakedModel)} and
         * {@link #addReferenceableModels(Map)}.
         *
         * @param name the name of the extra model to load.
         */
        void addExtraModel(ResourceLocation name);

        /**
         * Registers a set of extra models to be loaded even if they are not referenced by block state models.
         * <p>
         * Models added here can be referenced via {@link ModelManagerUtils#getExtraModel(ModelManager, ResourceLocation)}.
         * <p>
         * This can be used to load models added via {@link #addReferenceableModel(ResourceLocation, UnbakedModel)} and
         * {@link #addReferenceableModels(Map)}.
         *
         * @param names the names of extra models to load.
         */
        void addExtraModels(Set<ResourceLocation> names);
    }
}
