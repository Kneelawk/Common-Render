package com.kneelawk.krender.engine.backend.neoforge.impl.model;

import java.util.LinkedHashSet;
import java.util.Set;

import net.neoforged.neoforge.client.ChunkRenderTypeSet;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockState;

import com.kneelawk.krender.engine.api.buffer.QuadEmitter;
import com.kneelawk.krender.engine.backend.neoforge.impl.NFRenderer;
import com.kneelawk.krender.engine.backend.neoforge.impl.mesh.NFRootQuadEmitter;
import com.kneelawk.krender.engine.base.buffer.BaseQuadFormat;

public class RenderTypeCollector {
    private static final ThreadLocal<RenderTypeCollector> POOL =
        ThreadLocal.withInitial(RenderTypeCollector::new);

    private final Maker maker = new Maker();
    private BlockState state;
    private final Set<RenderType> renderTypes = new LinkedHashSet<>();

    public static RenderTypeCollector get(BlockState state) {
        RenderTypeCollector emitter = POOL.get();
        emitter.state = state;
        emitter.renderTypes.clear();
        return emitter;
    }

    public QuadEmitter emitter() {
        maker.clear();
        return maker;
    }

    public ChunkRenderTypeSet getRenderTypes() {
        maker.flushVertices();
        return ChunkRenderTypeSet.of(renderTypes);
    }

    private class Maker extends NFRootQuadEmitter {
        public Maker() {
            super(NFRenderer.INSTANCE);
            begin(new int[BaseQuadFormat.TOTAL_STRIDE], 0);
        }

        @Override
        public void emitDirectly() {
            RenderType vanilla = getMaterial().toVanillaBlock();
            if (vanilla == null) {
                // deprecated vanilla path -- may be removed some day
                ChunkRenderTypeSet types = ItemBlockRenderTypes.getRenderLayers(state);
                types.forEach(renderTypes::add);
            }
            renderTypes.add(vanilla);
        }
    }
}
