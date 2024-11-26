package com.kneelawk.krender.engine.neoforge.impl;

import java.util.concurrent.atomic.AtomicReference;

import net.neoforged.neoforge.client.ChunkRenderTypeSet;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;

import com.kneelawk.krender.engine.api.KRenderer;
import com.kneelawk.krender.engine.api.buffer.QuadEmitter;
import com.kneelawk.krender.engine.api.material.RenderMaterial;
import com.kneelawk.krender.engine.api.mesh.Mesh;
import com.kneelawk.krender.engine.api.mesh.MeshBuilder;
import com.kneelawk.krender.engine.api.model.StaticBakedModelCore;
import com.kneelawk.krender.engine.api.util.DirectionIds;
import com.kneelawk.krender.engine.neoforge.impl.mixin.impl.Accessor_SimpleBakedModel;

public class SimpleAdapterModelCore implements StaticBakedModelCore {
    private final SimpleBakedModel model;
    private final AtomicReference<Mesh> mesh = new AtomicReference<>(null);

    public SimpleAdapterModelCore(SimpleBakedModel model) {this.model = model;}

    @Override
    public Mesh getMesh() {
        if (mesh.get() == null) {
            RenderMaterial material = KRenderer.getDefault().materialManager().defaultMaterial();
            ChunkRenderTypeSet renderTypes = ((Accessor_SimpleBakedModel) model).krender$getBlockRenderTypes();
            if (renderTypes != null && !renderTypes.isEmpty()) {
                material = KRenderer.getDefault().materialManager().materialFinder()
                    .fromVanilla(renderTypes.asList().getFirst()).find();
            }

            MeshBuilder builder = KRenderer.getDefault().meshBuilder();
            QuadEmitter emitter = builder.emitter();
            RandomSource random = RandomSource.create(42);

            for (int i = 0; i < DirectionIds.DIRECTION_COUNT; i++) {
                Direction cullFace = DirectionIds.idToDirection(i);

                random.setSeed(42);

                for (BakedQuad quad : model.getQuads(null, cullFace, random)) {
                    emitter.fromVanilla(quad, material, cullFace);
                    emitter.emit();
                }
            }

            Mesh mesh = builder.build();

            if (this.mesh.compareAndSet(null, mesh)) {
                return mesh;
            }
        }

        return mesh.get();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return model.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return model.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return model.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return model.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return model.getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
        return model.getTransforms();
    }

    @Override
    public ItemOverrides getOverrides() {
        return model.getOverrides();
    }
}
