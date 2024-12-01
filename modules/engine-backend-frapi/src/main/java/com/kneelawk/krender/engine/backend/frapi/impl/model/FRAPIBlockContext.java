package com.kneelawk.krender.engine.backend.frapi.impl.model;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;

import org.joml.Vector2f;
import org.joml.Vector3f;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;

import com.kneelawk.krender.engine.api.buffer.QuadEmitter;
import com.kneelawk.krender.engine.api.model.ModelBlockContext;

class FRAPIBlockContext implements RenderContext {
    private final ModelBlockContext ctx;
    private final FRAPIEmitter emitter;

    public FRAPIBlockContext(ModelBlockContext ctx, QuadEmitter emitter) {
        this.ctx = ctx;
        this.emitter = new FRAPIEmitter(emitter);
    }

    @Override
    public net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter getEmitter() {
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
    @Deprecated(forRemoval = true)
    public BakedModelConsumer bakedModelConsumer() {
        return new BakedModelConsumer() {
            @Override
            public void accept(BakedModel model) {
                model.emitBlockQuads(ctx.level(), ctx.state(), ctx.pos(), ctx.random(), FRAPIBlockContext.this);
            }

            @Override
            public void accept(BakedModel model, @Nullable BlockState state) {
                model.emitBlockQuads(ctx.level(), state, ctx.pos(), ctx.random(), FRAPIBlockContext.this);
            }
        };
    }

    private record Emitter(Emitter parent, QuadTransform emitter) {}
}
