package com.kneelawk.krender.engine.api.util;

import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;

/**
 * Represents a boolean with a default value.
 */
public enum TriState implements StringRepresentable {
    /**
     * Represents a {@code false} value.
     */
    FALSE,

    /**
     * Represents a default value depending on context.
     */
    DEFAULT,

    /**
     * Represents a {@code true} value.
     */
    TRUE;

    /**
     * Tri state codec.
     */
    public static final Codec<TriState> CODEC = Codec.either(StringRepresentable.fromEnum(TriState::values), Codec.BOOL)
        .xmap(either -> either.map(Function.identity(), TriState::of), tri -> {
            if (tri != DEFAULT) return Either.right(tri.booleanValue());
            else return Either.left(tri);
        });

    /**
     * Gets the tri-state for the given boolean value.
     *
     * @param bool the boolean value.
     * @return the associated tri-state.
     */
    public static TriState of(boolean bool) {
        return bool ? TRUE : FALSE;
    }

    /**
     * Gets the tri-state for the given nullable boolean.
     *
     * @param bool the nullable boolean value.
     * @return the associated tri-state.
     */
    public static TriState of(@Nullable Boolean bool) {
        return bool == null ? DEFAULT : of(bool.booleanValue());
    }

    /**
     * {@return whether this tri-state is true}
     */
    public boolean booleanValue() {
        return this == TRUE;
    }

    /**
     * {@return this tri-state as a nullable boolean value}
     */
    public @Nullable Boolean getBoxed() {
        return this == DEFAULT ? null : booleanValue();
    }

    @Override
    public @NotNull String getSerializedName() {
        return switch (this) {
            case FALSE -> "false";
            case DEFAULT -> "default";
            case TRUE -> "true";
        };
    }
}
