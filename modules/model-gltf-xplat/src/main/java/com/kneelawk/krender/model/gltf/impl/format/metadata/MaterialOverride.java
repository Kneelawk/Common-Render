package com.kneelawk.krender.model.gltf.impl.format.metadata;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.function.Supplier;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.Util;

import com.kneelawk.krender.engine.api.TriState;
import com.kneelawk.krender.engine.api.material.BlendMode;
import com.kneelawk.krender.engine.api.material.MaterialFinder;
import com.kneelawk.krender.engine.api.material.MaterialManager;
import com.kneelawk.krender.engine.api.material.RenderMaterial;
import com.kneelawk.krender.engine.api.util.ColorUtil;
import com.kneelawk.krender.model.gltf.impl.format.Codecs;

public record MaterialOverride(Optional<BlendMode> blendMode, Optional<Boolean> colorIndexDisabled,
                               Optional<Boolean> emissive, Optional<Boolean> diffuseDisabled,
                               Optional<TriState> ambientOcclusionMode, OptionalInt colorFactor) {
    private static final Codec<Integer> COLOR_CODEC = Codec.either(Codec.INT, Codec.floatRange(0.0f, 1.0f).listOf()
            .comapFlatMap(list -> Util.fixedSize(list, 4)
                    .map(listx -> ColorUtil.toArgb(listx.get(0), listx.get(1), listx.get(2), listx.get(3))),
                argb -> List.of(ColorUtil.redFloat(argb), ColorUtil.greenFloat(argb), ColorUtil.blueFloat(argb),
                    ColorUtil.alphaFloat(argb))))
        .xmap(either -> either.map(Function.identity(), Function.identity()), Either::right);

    public static final Codec<MaterialOverride> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BlendMode.CODEC.optionalFieldOf("blendModel").forGetter(MaterialOverride::blendMode),
        Codec.BOOL.optionalFieldOf("colorIndexDisabled").forGetter(MaterialOverride::colorIndexDisabled),
        Codec.BOOL.optionalFieldOf("emissive").forGetter(MaterialOverride::emissive),
        Codec.BOOL.optionalFieldOf("diffuseDisabled").forGetter(MaterialOverride::diffuseDisabled),
        TriState.CODEC.optionalFieldOf("ambientOcclusionMode")
            .forGetter(MaterialOverride::ambientOcclusionMode),
        Codecs.optionalInt(COLOR_CODEC.optionalFieldOf("colorFactor")).forGetter(MaterialOverride::colorFactor)
    ).apply(instance, MaterialOverride::new));

    public static final MaterialOverride DEFAULT =
        new MaterialOverride(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
            OptionalInt.empty());

    public RenderMaterial toRenderMaterial(MaterialManager manager, RenderMaterial material) {
        MaterialFinder finder = manager.materialFinder().copyFrom(material);
        blendMode.ifPresent(finder::setBlendMode);
        colorIndexDisabled.ifPresent(finder::setColorIndexDisabled);
        emissive.ifPresent(finder::setEmissive);
        diffuseDisabled.ifPresent(finder::setDiffuseDisabled);
        ambientOcclusionMode.ifPresent(finder::setAmbientOcclusionMode);
        return finder.find();
    }

    public MaterialOverride overlay(MaterialOverride under) {
        return new MaterialOverride(blendMode.or(under::blendMode), colorIndexDisabled.or(under::colorIndexDisabled),
            emissive.or(under::emissive), diffuseDisabled.or(under::diffuseDisabled),
            ambientOcclusionMode.or(under::ambientOcclusionMode), or(colorFactor, under::colorFactor));
    }

    private static OptionalInt or(OptionalInt a, Supplier<? extends OptionalInt> b) {
        if (a.isPresent()) return a;
        return b.get();
    }
}
