package com.kneelawk.krender.ctcomplicated.client;

import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.SpriteGetter;
import net.minecraft.client.resources.model.UnbakedModel;

import com.kneelawk.krender.engine.api.KRenderer;
import com.kneelawk.krender.engine.api.material.BlendMode;

import static com.kneelawk.krender.ctcomplicated.CTConstants.rl;

public class CTGlassUnbakedModel implements UnbakedModel {
    @Override
    public void resolveDependencies(Resolver resolver) {
    }

    @Override
    public TextureSlots.Data getTextureSlots() {
        return new TextureSlots.Data.Builder().addTexture("particle", getMaterial("block/ct_glass"))
            .addTexture("convex", getMaterial("block/ct_glass_convex"))
            .addTexture("horizontal", getMaterial("block/ct_glass_horizontal"))
            .addTexture("vertical", getMaterial("block/ct_glass_vertical"))
            .addTexture("concave", getMaterial("block/ct_glass_concave"))
            .addTexture("center", getMaterial("block/ct_glass_center")).build();
    }

    @Override
    public BakedModel bake(TextureSlots textureSlots, ModelBaker baker, ModelState modelState,
                           boolean hasAmbientOcclusion, boolean useBlockLight, ItemTransforms transforms) {
        SpriteGetter spriteGetter = baker.sprites();
        Function<String, TextureAtlasSprite> material = str -> {
            Material mat = textureSlots.getMaterial(str);
            if (mat == null) return spriteGetter.reportMissingReference(str);
            return spriteGetter.get(mat);
        };

        return KRenderer.getDefault().bakedModelFactory()
            .wrap(new CTGlassBakedModel(true, true, material.apply("particle"), new TextureAtlasSprite[]{
                material.apply("convex"),
                material.apply("horizontal"),
                material.apply("vertical"),
                material.apply("concave"),
                material.apply("center")
            }, KRenderer.getDefault().materialManager().materialFinder().setBlendMode(BlendMode.CUTOUT).find()));
    }

    private static @NotNull Material getMaterial(String str) {
        return new Material(TextureAtlas.LOCATION_BLOCKS, rl(str));
    }
}
