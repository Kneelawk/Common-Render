package com.kneelawk.krender.engine.api.model;

import org.jetbrains.annotations.Nullable;

import org.joml.Vector3f;

import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;

import com.kneelawk.krender.engine.api.KRenderer;
import com.kneelawk.krender.engine.base.model.BakedModelCoreProvider;

/**
 * Utilities for models.
 */
public final class ModelUtils {
    private ModelUtils() {}

    /**
     * Tries to obtain a {@link BakedModelCore} from a {@link BakedModel} if that baked model either contains a baked
     * model core or can be converted into one.
     *
     * @param model the baked model to get the core of.
     * @return the core of the given baked model or {@code null} if the given baked model did not contain and could not
     * be converted into a model core.
     */
    public static @Nullable BakedModelCore<?> getCore(BakedModel model) {
        if (model instanceof BakedModelCoreProvider provider) {
            return provider.krender$getCore();
        }

        for (KRenderer renderer : KRenderer.getByPriority()) {
            BakedModelCore<?> core = renderer.bakedModelUnwrapper().unwrap(model);
            if (core != null) {
                return core;
            }
        }

        return null;
    }

    /**
     * Creates a transform accepting the same values that would be in a json {@code display} block.
     *
     * @param rx the rotation x value.
     * @param ry the rotation y value.
     * @param rz the rotation z value.
     * @param tx the translation x value.
     * @param ty the translation y value.
     * @param tz the translation z value.
     * @param sx the scale x value.
     * @param sy the scale y value.
     * @param sz the scale z value.
     * @return the resulting item transform.
     */
    public static ItemTransform createTransform(float rx, float ry, float rz, float tx, float ty, float tz, float sx,
                                                float sy, float sz) {
        Vector3f rotation = new Vector3f(rx, ry, rz);
        Vector3f translation = new Vector3f(tx, ty, tz);
        translation.mul(0.0625f);
        translation.set(Math.clamp(translation.x, -5f, 5f), Math.clamp(translation.y, -5f, 5f),
            Math.clamp(translation.z, -5f, 5f));
        Vector3f scale = new Vector3f(Math.clamp(sx, -4f, 4f), Math.clamp(sy, -4f, 4f), Math.clamp(sz, -4f, 4f));
        return new ItemTransform(rotation, translation, scale);
    }

    /**
     * Block {@code gui} transform value.
     */
    public static final ItemTransform GUI_TRANSFORM =
        createTransform(30f, 225f, 0f, 0f, 0f, 0f, 0.625f, 0.625f, 0.625f);
    /**
     * Block {@code ground} transform value.
     */
    public static final ItemTransform GROUND_TRANSFORM = createTransform(0f, 0f, 0f, 0f, 3f, 0f, 0.25f, 0.25f, 0.25f);
    /**
     * Block {@code fixed} transform value.
     */
    public static final ItemTransform FIXED_TRANSFORM = createTransform(0f, 0f, 0f, 0f, 0f, 0f, 0.5f, 0.5f, 0.5f);
    /**
     * Block {@code thirdperson_righthand} transform value.
     */
    public static final ItemTransform THIRDPERSON_RIGHTHAND_TRANSFORM =
        createTransform(75f, 45f, 0f, 0f, 0.25f, 0f, 0.375f, 0.375f, 0.375f);
    /**
     * Block {@code firstperson_righthand} transform value.
     */
    public static final ItemTransform FIRSTPERSON_RIGHTHAND_TRANSFORM =
        createTransform(0f, 45f, 0f, 0f, 0f, 0f, 0.4f, 0.4f, 0.4f);
    /**
     * Block {@code firstperson_lefthand} transform value.
     */
    public static final ItemTransform FIRSTPERSON_LEFTHAND_TRANSFORM =
        createTransform(0f, 225f, 0f, 0f, 0f, 0f, 0.4f, 0.4f, 0.4f);

    /**
     * The default block transforms.
     */
    public static final ItemTransforms BLOCK_DISPLAY =
        new ItemTransforms(THIRDPERSON_RIGHTHAND_TRANSFORM, THIRDPERSON_RIGHTHAND_TRANSFORM,
            FIRSTPERSON_LEFTHAND_TRANSFORM, FIRSTPERSON_RIGHTHAND_TRANSFORM, ItemTransform.NO_TRANSFORM, GUI_TRANSFORM,
            GROUND_TRANSFORM, FIXED_TRANSFORM);
}
