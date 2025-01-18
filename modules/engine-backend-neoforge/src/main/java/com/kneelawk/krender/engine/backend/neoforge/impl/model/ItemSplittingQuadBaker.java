package com.kneelawk.krender.engine.backend.neoforge.impl.model;

import java.util.List;
import java.util.Map;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectLinkedOpenHashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;

import com.kneelawk.krender.engine.api.buffer.QuadEmitter;
import com.kneelawk.krender.engine.api.material.RenderMaterial;
import com.kneelawk.krender.engine.api.model.BakedModelCore;
import com.kneelawk.krender.engine.api.texture.SpriteFinder;
import com.kneelawk.krender.engine.backend.neoforge.impl.NFRenderer;
import com.kneelawk.krender.engine.backend.neoforge.impl.mesh.NFRootQuadEmitter;
import com.kneelawk.krender.engine.base.buffer.BaseQuadFormat;

public class ItemSplittingQuadBaker {
    private static final ThreadLocal<ItemSplittingQuadBaker> POOL =
        ThreadLocal.withInitial(ItemSplittingQuadBaker::new);

    private final Maker maker = new Maker();
    private final SpriteFinder finder =
        SpriteFinder.get(Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS));

    private final Map<RenderType, List<BakedQuad>> datas = new Reference2ObjectLinkedOpenHashMap<>();

    private ItemSplittingQuadBaker() {
    }

    public static ItemSplittingQuadBaker get() {
        ItemSplittingQuadBaker baker = POOL.get();
        baker.clear();
        return baker;
    }

    public void clear() {
        datas.clear();
    }

    public QuadEmitter emitter() {
        maker.clear();
        return maker;
    }

    public List<BakedModel> bake(BakedModelCore<?> core) {
        List<BakedModel> models = new ObjectArrayList<>();
        for (var entry : datas.entrySet()) {
            models.add(new ItemBakedModel(core, entry.getKey(), entry.getValue()));
        }
        return models;
    }

    private class Maker extends NFRootQuadEmitter {
        public Maker() {
            super(NFRenderer.INSTANCE);
            begin(new int[BaseQuadFormat.TOTAL_STRIDE], 0);
        }

        @Override
        public void emitDirectly() {
            final TextureAtlasSprite sprite = finder.find(this);

            final RenderType itemRenderType = getItemRenderType(getMaterial());

            final BakedQuad quad = toBakedQuad(sprite);

            datas.computeIfAbsent(itemRenderType, t -> new ObjectArrayList<>()).add(quad);
        }
    }

    public static RenderType getItemRenderType(RenderMaterial material) {
        return switch (material.getBlendMode()) {
            case DEFAULT, TRANSLUCENT -> Sheets.translucentItemSheet();
            case SOLID -> Sheets.solidBlockSheet();
            case CUTOUT_MIPPED, CUTOUT -> Sheets.cutoutBlockSheet();
        };
    }
}
