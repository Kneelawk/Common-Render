package com.kneelawk.krender.engine.api.model;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import com.kneelawk.krender.engine.api.mesh.Mesh;

/**
 * Simple model core that just holds a mesh and does not have any dynamic features.
 *
 * @param mesh     the static mesh contained in this model
 * @param particle the particle sprite of this model.
 * @param useAo    whether this model should have ambient occlusion.
 * @param gui3d    whether this model is 3d in inventories.
 */
public record SimpleModelCore(Mesh mesh, TextureAtlasSprite particle, boolean useAo, boolean gui3d)
    implements StaticBakedModelCore {
    @Override
    public Mesh getMesh() {
        return mesh;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return useAo;
    }

    @Override
    public boolean isGui3d() {
        return gui3d;
    }

    @Override
    public boolean usesBlockLight() {
        return gui3d;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return particle;
    }

    @Override
    public ItemTransforms getTransforms() {
        return ModelUtils.BLOCK_DISPLAY;
    }
}
