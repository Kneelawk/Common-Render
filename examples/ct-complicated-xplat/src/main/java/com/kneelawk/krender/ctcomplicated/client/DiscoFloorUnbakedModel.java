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

import static com.kneelawk.krender.ctcomplicated.CTConstants.rl;

public class DiscoFloorUnbakedModel implements UnbakedModel {
    @Override
    public void resolveDependencies(Resolver resolver) {
    }

    @Override
    public TextureSlots.Data getTextureSlots() {
        return new TextureSlots.Data.Builder().addTexture("particle", getMaterial("block/disco_floor"))
            .addTexture("convex", getMaterial("block/disco_floor_convex"))
            .addTexture("horizontal", getMaterial("block/disco_floor_horizontal"))
            .addTexture("vertical", getMaterial("block/disco_floor_vertical"))
            .addTexture("concave", getMaterial("block/disco_floor_concave"))
            .addTexture("center", getMaterial("block/disco_floor_center"))
            .addTexture("glow_convex", getMaterial("block/disco_floor_glow_convex"))
            .addTexture("glow_horizontal", getMaterial("block/disco_floor_glow_horizontal"))
            .addTexture("glow_vertical", getMaterial("block/disco_floor_glow_vertical"))
            .addTexture("glow_concave", getMaterial("block/disco_floor_glow_concave"))
            .addTexture("glow_center", getMaterial("block/disco_floor_glow_center")).build();
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
            .wrap(new DiscoFloorBakedModel(material.apply("particle"), new TextureAtlasSprite[]{
                material.apply("convex"),
                material.apply("horizontal"),
                material.apply("vertical"),
                material.apply("concave"),
                material.apply("center")
            }, new TextureAtlasSprite[]{
                material.apply("glow_convex"),
                material.apply("glow_horizontal"),
                material.apply("glow_vertical"),
                material.apply("glow_concave"),
                material.apply("glow_center")
            }));
    }

    private static @NotNull Material getMaterial(String str) {
        return new Material(TextureAtlas.LOCATION_BLOCKS, rl(str));
    }
}
