package com.kneelawk.krender.engine.backend.frapi.api;

import net.fabricmc.fabric.api.renderer.v1.Renderer;

import com.kneelawk.krender.engine.api.KRenderer;
import com.kneelawk.krender.engine.api.util.TriState;
import com.kneelawk.krender.engine.api.material.BlendMode;
import com.kneelawk.krender.engine.api.material.RenderMaterial;

/**
 * Utility class for converting between KRender types and FRAPI types.
 */
public final class ConversionUtils {
    private ConversionUtils() {}

    /**
     * Converts a FRAPI blend-mode to a KRender blend-mode.
     *
     * @param blendMode the FRAPI blend mode.
     * @return the equivalent KRender blend mode.
     */
    public static BlendMode toKRender(net.fabricmc.fabric.api.renderer.v1.material.BlendMode blendMode) {
        return switch (blendMode) {
            case DEFAULT -> BlendMode.DEFAULT;
            case SOLID -> BlendMode.SOLID;
            case CUTOUT_MIPPED -> BlendMode.CUTOUT_MIPPED;
            case CUTOUT -> BlendMode.CUTOUT;
            case TRANSLUCENT -> BlendMode.TRANSLUCENT;
        };
    }

    /**
     * Converts a KRender blend-mode to a FRAPI blend-mode.
     *
     * @param blendMode the KRender blend-mode.
     * @return the equivalent FRAPI blend-mode.
     */
    public static net.fabricmc.fabric.api.renderer.v1.material.BlendMode toFabric(BlendMode blendMode) {
        return switch (blendMode) {
            case DEFAULT -> net.fabricmc.fabric.api.renderer.v1.material.BlendMode.DEFAULT;
            case SOLID -> net.fabricmc.fabric.api.renderer.v1.material.BlendMode.SOLID;
            case CUTOUT_MIPPED -> net.fabricmc.fabric.api.renderer.v1.material.BlendMode.CUTOUT_MIPPED;
            case CUTOUT -> net.fabricmc.fabric.api.renderer.v1.material.BlendMode.CUTOUT;
            case TRANSLUCENT -> net.fabricmc.fabric.api.renderer.v1.material.BlendMode.TRANSLUCENT;
        };
    }

    /**
     * Converts a FAPI tri-state to a KRender tri-state.
     *
     * @param triState the FAPI tri-state.
     * @return the equivalent KRender tri-state.
     */
    public static TriState toKRender(net.fabricmc.fabric.api.util.TriState triState) {
        return switch (triState) {
            case FALSE -> TriState.FALSE;
            case DEFAULT -> TriState.DEFAULT;
            case TRUE -> TriState.TRUE;
        };
    }

    /**
     * Converts a KRender tri-state to a FAPI tri-state.
     *
     * @param triState the KRender tri-state.
     * @return the equivalent FAPI tri-state.
     */
    public static net.fabricmc.fabric.api.util.TriState toFabric(TriState triState) {
        return switch (triState) {
            case FALSE -> net.fabricmc.fabric.api.util.TriState.FALSE;
            case DEFAULT -> net.fabricmc.fabric.api.util.TriState.DEFAULT;
            case TRUE -> net.fabricmc.fabric.api.util.TriState.TRUE;
        };
    }

    /**
     * Converts a FRAPI render material to a KRender render material.
     *
     * @param renderer the KRenderer to make the render material for.
     * @param material the FRAPI render material.
     * @return the equivalent KRender render material.
     */
    public static RenderMaterial toKRender(KRenderer renderer,
                                           net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial material) {
        return renderer.materialManager().materialFinder().setBlendMode(toKRender(material.blendMode()))
            .setEmissive(material.emissive()).setDiffuseDisabled(material.disableDiffuse())
            .setAmbientOcclusionMode(toKRender(material.ambientOcclusion())).find();
    }

    /**
     * Converts a KRender render material to a FRAPI render material.
     *
     * @param material the KRender render material.
     * @return the equivalent FRAPI render material.
     */
    public static net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial toFabric(RenderMaterial material) {
        return Renderer.get().materialFinder().blendMode(toFabric(material.getBlendMode()))
            .emissive(material.isEmissive()).disableDiffuse(material.isDiffuseDisabled())
            .ambientOcclusion(toFabric(material.getAmbientOcclusionMode())).find();
    }
}
