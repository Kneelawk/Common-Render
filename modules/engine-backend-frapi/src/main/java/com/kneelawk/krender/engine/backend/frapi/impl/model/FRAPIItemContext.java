package com.kneelawk.krender.engine.backend.frapi.impl.model;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;

import com.kneelawk.krender.engine.api.model.ModelItemContext;

public class FRAPIItemContext implements RenderContext {
    private final ModelItemContext ctx;
    private final FRAPIEmitter emitter;

    public FRAPIItemContext(ModelItemContext ctx, com.kneelawk.krender.engine.api.buffer.QuadEmitter emitter) {
        this.ctx = ctx;
        this.emitter = new FRAPIEmitter(emitter);
    }

    @Override
    public QuadEmitter getEmitter() {
        return emitter;
    }

    @Override
    public void pushTransform(QuadTransform transform) {
        emitter.pushTransform(transform);
    }

    @Override
    public void popTransform() {
        emitter.popTransform();
    }

    @Override
    public BakedModelConsumer bakedModelConsumer() {
        return new BakedModelConsumer() {
            @Override
            public void accept(BakedModel model) {
                model.emitItemQuads(ctx.stack(), ctx.randomSupplier(), FRAPIItemContext.this);
            }

            @Override
            public void accept(BakedModel model, @Nullable BlockState state) {
                model.emitItemQuads(ctx.stack(), ctx.randomSupplier(), FRAPIItemContext.this);
            }
        };
    }
}
