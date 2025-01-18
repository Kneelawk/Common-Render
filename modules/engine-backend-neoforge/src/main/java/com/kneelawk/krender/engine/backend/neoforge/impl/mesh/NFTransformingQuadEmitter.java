package com.kneelawk.krender.engine.backend.neoforge.impl.mesh;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import com.kneelawk.krender.engine.api.TriState;
import com.kneelawk.krender.engine.api.material.RenderMaterial;
import com.kneelawk.krender.engine.base.BaseKRendererApi;
import com.kneelawk.krender.engine.base.buffer.TransformStack;
import com.kneelawk.krender.engine.base.buffer.TransformingQuadEmitter;

public class NFTransformingQuadEmitter extends TransformingQuadEmitter {
    public NFTransformingQuadEmitter(BaseKRendererApi renderer,
                                     TransformStack transformStack) {
        super(renderer, transformStack);
    }

    @Override
    public BakedQuad toBakedQuad(TextureAtlasSprite sprite) {
        int[] quad = new int[VANILLA_QUAD_STRIDE];
        final RenderMaterial material = getMaterial();

        toVanilla(quad, 0);
        boolean shade = !material.isDiffuseDisabled();

        // NeoForge adds a BakedQuad constructor that handles AO
        boolean hasAo = material.getAmbientOcclusionMode() != TriState.FALSE;
        int emission = material.isEmissive() ? 15 : 0;
        return new BakedQuad(quad, getTintIndex(), getLightFace(), sprite, shade, emission, hasAo);
    }
}
