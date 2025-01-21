package com.kneelawk.krender.engine.backend.neoforge.impl.model;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.ImmutableList;

import net.neoforged.neoforge.client.ChunkRenderTypeSet;

import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

import com.kneelawk.krender.engine.api.buffer.QuadEmitter;
import com.kneelawk.krender.engine.api.texture.SpriteFinder;
import com.kneelawk.krender.engine.api.util.DirectionIds;
import com.kneelawk.krender.engine.backend.neoforge.impl.NFRenderer;
import com.kneelawk.krender.engine.backend.neoforge.impl.mesh.NFRootQuadEmitter;
import com.kneelawk.krender.engine.base.buffer.BaseQuadFormat;

public class SplittingQuadBaker {
    private static final ThreadLocal<SplittingQuadBaker> POOL = ThreadLocal.withInitial(SplittingQuadBaker::new);

    private static final RenderType[] RENDER_TYPES =
        RenderType.CHUNK_BUFFER_LAYERS.toArray(new RenderType[RenderType.CHUNK_BUFFER_LAYERS.size() + 1]);
    private static final RenderType DEFAULT_RENDER_TYPE = RenderType.solid();
    private static final int RENDER_TYPE_COUNT = RENDER_TYPES.length;
    private static final int ALL_RENDER_TYPE = RENDER_TYPE_COUNT - 1;
    private static final int DIRECTION_COUNT = DirectionIds.DIRECTION_COUNT;

    private static final int RENDER_TYPE_SHIFT = 0;
    private static final int RENDER_TYPE_BIT_COUNT = Mth.ceillog2(RENDER_TYPE_COUNT);
    private static final int RENDER_TYPE_FULL_COUNT = 1 << RENDER_TYPE_BIT_COUNT;
    private static final int DIRECTION_SHIFT = RENDER_TYPE_SHIFT + RENDER_TYPE_BIT_COUNT;
    private static final int TOTAL_COUNT = RENDER_TYPE_FULL_COUNT * DIRECTION_COUNT;

    private final Maker maker = new Maker();
    private final SpriteFinder finder =
        SpriteFinder.get(Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS));

    @SuppressWarnings("unchecked")
    private final List<BakedQuad>[] datas = new List[TOTAL_COUNT];
    private final Set<RenderType> renderTypes = new ObjectLinkedOpenHashSet<>();

    private SplittingQuadBaker() {
        for (int i = 0; i < RENDER_TYPE_COUNT; i++) {
            for (int j = 0; j < DIRECTION_COUNT; j++) {
                datas[getIndex(j, i)] = new ObjectArrayList<>();
            }
        }
    }

    public static SplittingQuadBaker get() {
        SplittingQuadBaker baker = POOL.get();
        baker.clear();
        return baker;
    }

    public void clear() {
        renderTypes.clear();
        maker.clear();
        for (List<BakedQuad> data : datas) {
            if (data != null) data.clear();
        }
    }

    public QuadEmitter emitter() {
        maker.clear();
        return maker;
    }

    @SuppressWarnings("unchecked")
    public Result bake() {
        List<BakedQuad>[] meshes = new List[TOTAL_COUNT];
        for (int i = 0; i < RENDER_TYPE_COUNT; i++) {
            for (int j = 0; j < DIRECTION_COUNT; j++) {
                final int index = getIndex(j, i);
                List<BakedQuad> data = datas[index];
                if (data.isEmpty()) {
                    meshes[index] = List.of();
                } else {
                    meshes[index] = ImmutableList.copyOf(datas[index]);
                }
            }
        }

        return new Result(meshes, ChunkRenderTypeSet.of(renderTypes));
    }

    private class Maker extends NFRootQuadEmitter {
        public Maker() {
            super(NFRenderer.INSTANCE);
            begin(new int[BaseQuadFormat.TOTAL_STRIDE], 0);
        }

        @Override
        public void emitDirectly() {
            final TextureAtlasSprite sprite = finder.find(this);

            final Direction cullFace = getCullFace();
            final RenderType renderType = Objects.requireNonNullElse(getMaterial().toVanillaBlock(), DEFAULT_RENDER_TYPE);

            renderTypes.add(renderType);

            final int onlyIndex = getIndex(cullFace, renderType);
            final int allIndex = getAllIndex(cullFace);

            final BakedQuad quad = toBakedQuad(sprite);

            datas[onlyIndex].add(quad);
            datas[allIndex].add(quad);
        }
    }

    public static class Result {
        private final List<BakedQuad>[] meshes;
        private final ChunkRenderTypeSet renderTypes;

        public Result(List<BakedQuad>[] meshes, ChunkRenderTypeSet renderTypes) {
            this.meshes = meshes;
            this.renderTypes = renderTypes;
        }

        public List<BakedQuad> getMesh(@Nullable Direction cullFace, @Nullable RenderType renderType) {
            int index = getIndex(cullFace, renderType);
            if (index == -1) return List.of(); // only happens if something weird happens
            List<BakedQuad> mesh = meshes[index];
            if (mesh == null) return List.of();
            return mesh;
        }

        public ChunkRenderTypeSet getRenderTypes() {
            return renderTypes;
        }
    }

    private static int getAllIndex(@Nullable Direction cullFace) {
        return getIndex(DirectionIds.directionToId(cullFace), ALL_RENDER_TYPE);
    }

    private static int getIndex(@Nullable Direction cullFace, @Nullable RenderType renderType) {
        int i = indexOf(renderType);
        if (i == -1) return -1;
        int j = DirectionIds.directionToId(cullFace);
        return getIndex(j, i);
    }

    private static int getIndex(int directionIndex, int renderTypeIndex) {
        return renderTypeIndex | (directionIndex << DIRECTION_SHIFT);
    }

    private static int indexOf(RenderType thing) {
        int len = RENDER_TYPES.length;
        for (int i = 0; i < len; i++) {
            if (RENDER_TYPES[i] == thing) return i;
        }
        return -1;
    }
}
