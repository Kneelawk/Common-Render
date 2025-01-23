package com.kneelawk.krender.engine.api.material;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;

import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.util.StringRepresentable;

/**
 * Describes different handlings of {@link ItemStackRenderState.FoilType}.
 */
public enum GlintMode implements StringRepresentable {
    /**
     * Use which ever foil type would be used normally.
     */
    DEFAULT(null),
    /**
     * Use no foil type.
     */
    NONE(ItemStackRenderState.FoilType.NONE),
    /**
     * Use standard foil type.
     */
    STANDARD(ItemStackRenderState.FoilType.STANDARD),
    /**
     * Use special foil type.
     */
    SPECIAL(ItemStackRenderState.FoilType.SPECIAL);

    /**
     * Glint mode codec.
     */
    public static final Codec<GlintMode> CODEC = StringRepresentable.fromEnum(GlintMode::values);

    /**
     * The foil type associated with this glint mode.
     */
    public final @Nullable ItemStackRenderState.FoilType foilType;

    GlintMode(@Nullable ItemStackRenderState.FoilType foilType) {
        this.foilType = foilType;
    }

    @Override
    public String getSerializedName() {
        return switch (this) {
            case DEFAULT -> "default";
            case NONE -> "none";
            case STANDARD -> "standard";
            case SPECIAL -> "special";
        };
    }
}
