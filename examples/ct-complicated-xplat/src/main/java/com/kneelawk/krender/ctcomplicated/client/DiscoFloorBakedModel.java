package com.kneelawk.krender.ctcomplicated.client;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

import com.kneelawk.krender.engine.api.KRenderer;
import com.kneelawk.krender.engine.api.TriState;
import com.kneelawk.krender.engine.api.buffer.QuadEmitter;
import com.kneelawk.krender.engine.api.material.BlendMode;
import com.kneelawk.krender.engine.api.material.RenderMaterial;
import com.kneelawk.krender.engine.api.mesh.Mesh;
import com.kneelawk.krender.engine.api.mesh.MeshBuilder;
import com.kneelawk.krender.engine.api.model.BakedModelCore;
import com.kneelawk.krender.engine.api.model.ModelBlockContext;
import com.kneelawk.krender.engine.api.model.ModelItemContext;
import com.kneelawk.krender.engine.api.model.ModelUtils;

public final class DiscoFloorBakedModel implements BakedModelCore<CTUtils.Data> {
    private static final RenderMaterial BASE_MATERIAL =
        KRenderer.getDefault().materialManager().materialFinder().setBlendMode(BlendMode.CUTOUT).find();
    private static final RenderMaterial GLOW_MATERIAL =
        KRenderer.getDefault().materialManager().materialFinder().setBlendMode(BlendMode.CUTOUT).setEmissive(true)
            .setDiffuseDisabled(true).setAmbientOcclusionMode(TriState.FALSE).find();
    private final TextureAtlasSprite particle;
    private final TextureAtlasSprite[] baseSprites;
    private final TextureAtlasSprite[] glowingSprites;
    private final Mesh itemMesh;

    public DiscoFloorBakedModel(TextureAtlasSprite particle, TextureAtlasSprite[] baseSprites,
                                TextureAtlasSprite[] glowingSprites) {
        this.particle = particle;
        this.baseSprites = baseSprites;
        this.glowingSprites = glowingSprites;

        MeshBuilder meshBuilder = KRenderer.getDefault().meshBuilder();
        QuadEmitter emitter = meshBuilder.emitter();
        for (Direction side : Direction.values()) {
            emitter.square(side, 0f, 0f, 1f, 1f, 0f);
            emitter.setUv(0, 0f, 0f);
            emitter.setUv(1, 0f, 1f);
            emitter.setUv(2, 1f, 1f);
            emitter.setUv(3, 1f, 0f);
            emitter.spriteBake(baseSprites[0], QuadEmitter.BAKE_ROTATE_NONE);
            emitter.setQuadColor(-1, -1, -1, -1);
            emitter.setTintIndex(-1);
            emitter.setMaterial(BASE_MATERIAL);
            emitter.emit();

            emitter.square(side, 0f, 0f, 1f, 1f, 0f);
            emitter.setUv(0, 0f, 0f);
            emitter.setUv(1, 0f, 1f);
            emitter.setUv(2, 1f, 1f);
            emitter.setUv(3, 1f, 0f);
            emitter.spriteBake(glowingSprites[0], QuadEmitter.BAKE_ROTATE_NONE);
            emitter.setQuadColor(-1, -1, -1, -1);
            emitter.setTintIndex(-1);
            emitter.setMaterial(GLOW_MATERIAL);
            emitter.emit();
        }

        itemMesh = meshBuilder.build();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return particle;
    }

    @Override
    public ItemTransforms getTransforms() {
        return ModelUtils.BLOCK_DISPLAY;
    }

    @Override
    public CTUtils.Data getBlockKey(ModelBlockContext ctx) {
        return CTUtils.getData(true, true, ctx);
    }

    @Override
    public void renderBlock(QuadEmitter renderTo, CTUtils.Data blockKey) {
        CTUtils.render(BASE_MATERIAL, baseSprites, renderTo, blockKey);
        CTUtils.render(GLOW_MATERIAL, glowingSprites, renderTo, blockKey);
    }

    @Override
    public void renderItem(QuadEmitter renderTo, ModelItemContext ctx) {
        itemMesh.outputTo(renderTo);
    }
}
