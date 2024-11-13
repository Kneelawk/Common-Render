package com.kneelawk.krender.engine.api.util.transform;

import org.jetbrains.annotations.UnknownNullability;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import com.kneelawk.krender.engine.api.buffer.QuadEmitter;
import com.kneelawk.krender.engine.api.buffer.QuadTransform;
import com.kneelawk.krender.engine.api.buffer.QuadView;
import com.kneelawk.krender.engine.api.texture.SpriteFinder;

/**
 * Quad transform that replaces the texture on all quads with the one specified.
 * <p>
 * Note: this assumes all textures are in the block atlas.
 */
public class RetextureQuadTransform implements QuadTransform<TextureAtlasSprite> {
    private static final RetextureQuadTransform INSTANCE = new RetextureQuadTransform();

    /**
     * {@return the quad transform instance}
     */
    public static RetextureQuadTransform getInstance() {
        return INSTANCE;
    }

    private final SpriteFinder finder =
        SpriteFinder.get(Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS));

    private RetextureQuadTransform() {}

    @Override
    public void transform(@UnknownNullability TextureAtlasSprite context, QuadView input, QuadEmitter output) {
        input.copyTo(output);

        TextureAtlasSprite oldSprite = finder.find(input);
        if (!oldSprite.contents().name().equals(MissingTextureAtlasSprite.getLocation())) {
            for (int i = 0; i < 4; i++) {
                float u = input.getU(i);
                float v = input.getV(i);

                u = oldSprite.getUOffset(u);
                v = oldSprite.getVOffset(v);

                u = context.getU(u);
                v = context.getV(v);

                output.setUv(i, u, v);
            }
        }
    }
}
