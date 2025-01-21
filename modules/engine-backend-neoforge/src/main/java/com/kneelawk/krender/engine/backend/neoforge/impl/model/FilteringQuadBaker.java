package com.kneelawk.krender.engine.backend.neoforge.impl.model;

import java.util.List;

import com.google.common.collect.ImmutableList;

import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import com.kneelawk.krender.engine.api.buffer.QuadEmitter;
import com.kneelawk.krender.engine.api.texture.SpriteFinder;
import com.kneelawk.krender.engine.backend.neoforge.impl.NFRenderer;
import com.kneelawk.krender.engine.backend.neoforge.impl.mesh.NFRootQuadEmitter;
import com.kneelawk.krender.engine.base.buffer.BaseQuadFormat;

public class FilteringQuadBaker {
    private static final ThreadLocal<FilteringQuadBaker> POOL = ThreadLocal.withInitial(FilteringQuadBaker::new);

    public static FilteringQuadBaker get(@Nullable Direction cullFace, @Nullable RenderType renderType,
                                         BlockState state) {
        FilteringQuadBaker baker = POOL.get();
        baker.clear();
        baker.cullFace = cullFace;
        baker.renderType = renderType;
        baker.state = state;
        return baker;
    }

    private final Maker maker = new Maker();
    private final List<BakedQuad> quads = new ObjectArrayList<>();
    private final SpriteFinder finder =
        SpriteFinder.get(Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS));

    private @Nullable Direction cullFace;
    private @Nullable RenderType renderType;
    private BlockState state;

    public void clear() {
        maker.clear();
        quads.clear();
    }

    public QuadEmitter emitter() {
        maker.clear();
        return maker;
    }

    public List<BakedQuad> bake() {
        maker.flushVertices();
        ImmutableList<BakedQuad> res = ImmutableList.copyOf(quads);
        clear();
        return res;
    }

    private class Maker extends NFRootQuadEmitter {
        public Maker() {
            super(NFRenderer.INSTANCE);
            begin(new int[BaseQuadFormat.TOTAL_STRIDE], 0);
        }

        @Override
        public void emitDirectly() {
            if (cullFace == getCullFace() && checkRenderType()) {
                TextureAtlasSprite sprite = finder.find(this);
                quads.add(toBakedQuad(sprite));
            }
        }

        private boolean checkRenderType() {
            if (renderType == null) return true;

            RenderType quadType = getMaterial().toVanillaBlock();
            if (quadType == null) {
                return ItemBlockRenderTypes.getRenderLayers(state).contains(renderType);
            } else {
                return renderType == quadType;
            }
        }
    }
}
